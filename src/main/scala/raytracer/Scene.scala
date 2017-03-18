package raytracer

import raytracer.lights.Light
import raytracer.math.Ray
import raytracer.primitives.{Intersection, Primitive}

/**
  * Created by Basim on 18/12/2016.
  */
class Scene(val lights: List[Light], val primitive: Primitive) {
  def intersect(ray: Ray): Option[Intersection] = primitive.intersect(ray)
}
