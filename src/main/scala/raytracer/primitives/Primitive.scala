package raytracer.primitives

import raytracer.math.Ray
import raytracer.BSDF
import raytracer.materials.Material
import raytracer.shapes.{DifferentialGeometry, Shape}

/**
  * Created by Basim on 28/01/2017.
  */
trait Primitive {

  def intersect(ray: Ray): Option[Intersection]

  def getBSDF(dg: DifferentialGeometry): BSDF
}
