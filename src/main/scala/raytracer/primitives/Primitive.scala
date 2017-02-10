package raytracer.primitives

import raytracer.math.Ray
import raytracer.{BSDF, Material}
import raytracer.shapes.{DifferentialGeometry, Shape}

/**
  * Created by Basim on 28/01/2017.
  */
class Primitive(val shape: Shape, val material: Material) {

  final def intersect(ray: Ray): Option[Intersection] = {
    shape.intersect(ray).map(x => Intersection(x._1, this, x._2))
  }

  final def getBSDF(dg: DifferentialGeometry): BSDF = {
    material.getBSDF(dg, shape.getShadingGeometry(dg))
  }
}
