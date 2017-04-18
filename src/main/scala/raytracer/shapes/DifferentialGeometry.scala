package raytracer.shapes

import raytracer.math.{Point, Transform, Vec3}

/**
  * Created by Basim on 10/01/2017.
  */
class DifferentialGeometry(
  val p: Point,
  val nn: Vec3,
  val u: Double, val v: Double, // texture coordinates
  val dpdu: Vec3, val dpdv: Vec3, // texture partial derivatives of point
  val shape: Shape
)

object DifferentialGeometry {
  def create(
    p: Point, dpduW: Vec3, dpdvW: Vec3,
    u: Double, v: Double, shape: Shape
  ): DifferentialGeometry = {
    val o2w = shape.objectToWorld

    val dpdu = o2w(dpduW)
    val dpdv = o2w(dpdvW)

    var nn = (dpdu cross dpdv).nor

    if (o2w.swapsHandedness) nn *= -1

    new DifferentialGeometry(o2w(p), nn, u, v, dpdu, dpdv, shape)
  }
}
