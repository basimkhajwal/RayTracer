package raytracer.shapes

import raytracer.math._

/**
  * Created by Basim on 14/01/2017.
  */
case class Triangle(mesh: TriangleMesh, idx: Int) extends Shape {

  val EPSILON = 1e-9

  val v0 = mesh.indices(3*idx)
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

  private val e1: Vec3 = p2-p1
  private val e2: Vec3 = p3-p1
  private val nor = e1.cross(e2)

  override def intersect(worldRay: Ray): Option[(DifferentialGeometry, Double)] = {

    val ray = worldToObject(worldRay)

    // Compute triangle intersection using Moller-Trombore algorithm
    val D = ray.dir
    val P = D.cross(e2)
    val disc = e1.dot(P)
    if (math.abs(disc) < EPSILON) return None

    val invDisc = 1 / disc
    val T = ray.start - p1
    val u = T.dot(P) * invDisc
    if (u < 0 || u > 1) return None

    val Q = T.cross(e1)
    val v = D.dot(Q) * invDisc
    if (v < 0 || u + v > 1) return None

    val t = e2.dot(Q) * invDisc
    if (t < 0) return None

    val intersectionPoint = (1-u-v)*p1 + u*p2 + v*p3
    val n = if (disc < 0) -nor else nor
    val surfacePoint = intersectionPoint + n*EPSILON

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

    val dg = DifferentialGeometry(objectToWorld(surfacePoint), objectToWorld(n), u, v, objectToWorld(dpdu), objectToWorld(dpdv), this)
    Some((dg, t))
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

    val nn =
      objectToWorld(
        b(0)*mesh.normals(v0) +
        b(1)*mesh.normals(v1) +
        b(2)*mesh.normals(v2)
      ).nor

    DifferentialGeometry(dg.p, -nn, dg.u, dg.v, dg.dpdu, dg.dpdv, this)
  }
}
