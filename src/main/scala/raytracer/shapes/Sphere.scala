package raytracer.shapes

import raytracer.math._

/**
  * Created by Basim on 05/01/2017.
  */
case class Sphere(radius: Double, o2w: Transform) extends Shape {

  override val objectToWorld: Transform = o2w
  override val worldToObject: Transform = o2w.inverse

  override val objectBounds: BBox = BBox(Point(-radius,-radius,-radius), Point(radius,radius,radius))

  val phiMax: Double = 2*math.Pi
  val thetaMin: Double = math.Pi // This took ages to fix :P
  val thetaMax: Double = 0

  def epEQ(a: Double, b: Double): Boolean = {
    b > a-0.0005 && b < a+0.0005
  }

  def epEQ(a: Vec3, b: Vec3): Boolean = {
    epEQ(a(0), b(0)) && epEQ(a(1), b(1)) && epEQ(a(2), b(2))
  }

  override def intersect(worldRay: Ray): Option[(DifferentialGeometry, Double)] = {

    if (epEQ(worldRay.dir, Vec3(-0.4943, -0.8398, -0.22442))) {
      println("gotcha")
    }

    val ray = worldToObject(worldRay)
    val d = ray.dir
    val o = ray.start


    val A = d.x*d.x + d.y*d.y + d.z*d.z
    val B = 2 * (d.x*o.x + d.y*o.y + d.z*o.z)
    val C = o.x*o.x + o.y*o.y + o.z*o.z - radius*radius

    val solution = Solver.quadratic(A, B, C).orNull
    if (solution == null) return None

    val t0 = solution._1
    val t1 = solution._2

    if (t0 <= 0 && t1 <= 0) return None

    val tHit =  if (t0 <= 0) t1 else t0
    val pHit = ray.start + ray.dir*tHit

    val phiAngle = math.atan2(pHit.y, pHit.x)
    val phi = if (phiAngle < 0) phiAngle+2*math.Pi else phiAngle

    val theta = math.acos(pHit.z / radius)
    val u = phi / phiMax
    val v = (theta - thetaMin) / (thetaMax - thetaMin)

    val zradius = math.sqrt(pHit.x*pHit.x + pHit.y*pHit.y)
    val cosphi = pHit.x / zradius
    val sinphi = pHit.y / zradius

    val dpdu = Vec3(-phiMax*pHit.y, phiMax*pHit.x, 0)
    val dpdv = Vec3(pHit.z*cosphi, pHit.z*sinphi, -radius*math.sin(theta)) * (thetaMax-thetaMin)

    val dg = DifferentialGeometry.create(pHit, dpdu, dpdv, u, v, this)
    Some((dg, tHit))
  }

}
