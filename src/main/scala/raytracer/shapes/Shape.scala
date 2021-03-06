package raytracer.shapes

import raytracer.math.{BBox, Ray, Transform}

/**
  * Created by Basim on 10/01/2017.
  */
trait Shape {
  val objectToWorld: Transform
  val worldToObject: Transform

  val objectBounds: BBox
  lazy val worldBounds: BBox = objectToWorld(objectBounds)

  def intersect(ray: Ray): Option[(DifferentialGeometry, Double)]

  def getShadingGeometry(dg: DifferentialGeometry): DifferentialGeometry = dg
}
