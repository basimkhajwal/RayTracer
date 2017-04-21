package raytracer.shapes

import raytracer.math._

/**
  * Created by Basim on 14/01/2017.
  */
case class Triangle(mesh: TriangleMesh, idx: Int) extends Shape {

  val EPSILON = 1e-10

  val v0 = mesh.indices(3*idx+0)
  val v1 = mesh.indices(3*idx+1)
  val v2 = mesh.indices(3*idx+2)

  val p1 = mesh.points(v0)
  val p2 = mesh.points(v1)
  val p3 = mesh.points(v2)

  val uvs: Array[Double] = if (mesh.hasUVS) {
    Array(
      mesh.uvs(2*v0), mesh.uvs(2*v0+1),
      mesh.uvs(2*v1), mesh.uvs(2*v1+1),
      mesh.uvs(2*v2), mesh.uvs(2*v2+1)
    )
  } else {
    Array(
      0, 0,
      1, 0,
      1, 1
    )
  }

  override val objectToWorld: Transform = mesh.objectToWorld
  override val worldToObject: Transform = mesh.worldToObject

  override val objectBounds: BBox = BBox.fromPoints(p1, p2, p3)

  override def intersect(worldRay: Ray): Option[(DifferentialGeometry, Double)] = {

    val ray = worldToObject(worldRay)

    val e1 = p2 - p1
    val e2 = p3 - p1
    val s1 = ray.dir cross e2

    val divisor = s1 dot e1
    if (divisor == 0) return None
    val invDivisor = 1 / divisor

    val s = ray.start - p1
    val b1 = (s dot s1) * invDivisor
    if (b1 < 0 || b1 > 1) return None

    val s2 = s cross e1
    val b2 = (ray.dir dot s2) * invDivisor
    if (b2 < 0 || b1 + b2 > 1) return None

    val tHit = (e2 dot s2) * invDivisor
    if (tHit <= EPSILON) return None

    // Compute partial derivatives
    val du1 = uvs(0)-uvs(4)
    val du2 = uvs(2)-uvs(4)
    val dv1 = uvs(1)-uvs(5)
    val dv2 = uvs(3)-uvs(5)
    val dp1 = p1-p3
    val dp2 = p2-p3

    val det = du1*dv2 - dv1*du2
    if (det == 0) throw new IllegalArgumentException("Error got a zero determinant!")

    val invDet = 1 / det
    val dpdu = (dv2 * dp1 - dv1 * dp2) * invDet
    val dpdv = (-du2 * dp1 + du1 * dp2) * invDet

    val b0 = 1 - b1 - b2
    val surfacePoint = b0*p1 + b1*p2 + b2*p3
    val tu = b0*uvs(0) + b1*uvs(2) + b2*uvs(4)
    val tv = b0*uvs(1) + b1*uvs(3) + b2*uvs(5)

    val dg = DifferentialGeometry.create(surfacePoint, dpdu, dpdv, tu, tv, this)
    Some((dg, tHit))
  }

  val shadingMat = Array(
    uvs(2)-uvs(0), uvs(4)-uvs(0),
    uvs(3)-uvs(1), uvs(5)-uvs(1)
  )

  override def getShadingGeometry(dg: DifferentialGeometry): DifferentialGeometry = {
    if (!mesh.hasNormals) return dg

    val c0 = dg.u - uvs(0)
    val c1 = dg.v - uvs(1)
    val solution = Solver.linearEquation(shadingMat, c0, c1).orNull

    val b =
      if (solution == null) Array(1/3.0, 1/3.0, 1/3.0)
      else Array(1 - solution._1 - solution._2, solution._1, solution._2)

    val ns =
      objectToWorld(
        b(0)*mesh.normals(v0) +
        b(1)*mesh.normals(v1) +
        b(2)*mesh.normals(v2)
      ).nor

    var ss = dg.dpdu.nor
    var ts = ss cross ns

    if (ts.mag2 > 0) {
      ts = ts.nor
      ss = ts.cross(ns)
    } else {
      val coordSystem = Vec3.createCoordinateSystem(Vec3(ns.x, ns.y, ns.z))
      ss = coordSystem._1
      ts = coordSystem._2
    }

    new DifferentialGeometry(dg.p, ns, dg.u, dg.v, ss, ts, this)
  }
}
