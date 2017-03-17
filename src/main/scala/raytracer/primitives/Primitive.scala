package raytracer.primitives

import raytracer.bxdf.BSDF
import raytracer.math.{BBox, Ray}
import raytracer.shapes.{DifferentialGeometry, Shape}

/**
  * Created by Basim on 28/01/2017.
  */
trait Primitive {

  val worldBound: BBox

  def intersect(ray: Ray): Option[Intersection]

  def getBSDF(dg: DifferentialGeometry): BSDF
}
