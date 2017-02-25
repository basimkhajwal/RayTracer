package raytracer.primitives

import raytracer.BSDF
import raytracer.materials.Material
import raytracer.math.Ray
import raytracer.shapes.{DifferentialGeometry, Shape}

/**
  * Created by Basim on 25/02/2017.
  */
class GeometricPrimitive(val shape: Shape, val material: Material) extends Primitive {

  final def intersect(ray: Ray): Option[Intersection] = {
    shape.intersect(ray).map(x => Intersection(x._1, this, x._2))
  }

  final def getBSDF(dg: DifferentialGeometry): BSDF = {
    material.getBSDF(dg, shape.getShadingGeometry(dg))
  }
}
