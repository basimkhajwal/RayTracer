package raytracer.shapes

import raytracer.math.{BBox, Transform}

/**
  * Created by Basim on 10/01/2017.
  */
trait Shape {
  val objectToWorld: Transform
  val worldToObject: Transform

  val objectBounds: BBox
  val worldBounds: BBox = objectToWorld(objectBounds)
}
