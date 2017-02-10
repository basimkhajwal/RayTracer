package raytracer.primitives

import raytracer.BSDF
import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 09/02/2017.
  */
case class Intersection (
  dg: DifferentialGeometry,
  primitive: Primitive,
  time: Double
) {
  final def getBSDF: BSDF = primitive.material.getBSDF(dg, dg)
}
