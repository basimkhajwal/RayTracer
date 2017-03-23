package raytracer.shapes

import raytracer.math.{Point, Vec3}

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
  def apply(
    p: Point, nn: Vec3,
    u: Double, v: Double,
    dpdu: Vec3, dpdv: Vec3,
    shape: Shape
  ): DifferentialGeometry = {

    if (shape.objectToWorld.swapsHandedness) {
      new DifferentialGeometry(p, -nn, u, v, dpdu, dpdv, shape)
    } else {
      new DifferentialGeometry(p, -nn, u, v, dpdu, dpdv, shape)
    }
  }
}
