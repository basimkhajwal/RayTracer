package raytracer.shapes

import raytracer.{Spectrum}
import raytracer.math._
import raytracer.Constants._

/**
  * Created by Basim on 14/01/2017.
  */
case class Triangle(p1: Point, p2: Point, p3: Point, colour: Spectrum) extends Shape {

  override val objectToWorld: Transform = Transform.identity
  override val worldToObject: Transform = Transform.identity

  override val objectBounds: BBox = BBox.fromPoints(p1, p2, p3)

  private val e1: Vec3 = p2-p1
  private val e2: Vec3 = p3-p1
  private val nor = e1.cross(e2)

  override def intersect(ray: Ray): Option[(DifferentialGeometry, Double)] = {

    // Compute triangle intersection using Moller-Trombore algorithm
    val D = ray.dir
    val P = D.cross(e2)
    val det = e1.dot(P)
    if (math.abs(det) < EPSILON) return None

    val invDet = 1 / det
    val T = ray.start - p1
    val u = T.dot(P) * invDet
    if (u < 0 || u > 1) return None

    val Q = T.cross(e1)
    val v = D.dot(Q) * invDet
    if (v < 0 || u + v > 1) return None

    val t = e2.dot(Q) * invDet
    if (t < 0) return None

    val intersectionPoint = (1-u-v)*p1 + u*p2 + v*p3
    val n = if (det < 0) -nor else nor
    val surfacePoint = intersectionPoint + n*EPSILON

    Some((DifferentialGeometry(surfacePoint, n, u, v, this), t))
  }
}
