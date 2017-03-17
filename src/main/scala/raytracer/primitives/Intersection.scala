package raytracer.primitives

import raytracer.bxdf.BSDF
import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 09/02/2017.
  */
case class Intersection (
  dg: DifferentialGeometry,
  primitive: Primitive,
  time: Double
) {
  final def getBSDF(): BSDF = primitive.getBSDF(dg)
}
