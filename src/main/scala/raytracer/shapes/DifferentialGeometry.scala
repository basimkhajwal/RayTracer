package raytracer.shapes

import raytracer.math.{Point, Vec3}

/**
  * Created by Basim on 10/01/2017.
  */
case class DifferentialGeometry(
  p: Point,
  nn: Vec3,
  u: Double, v: Double, // texture coordinates
  dpdu: Vec3, dpdv: Vec3, // texture partial derivatives of point
  shape: Shape
)
