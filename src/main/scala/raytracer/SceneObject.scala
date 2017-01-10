package raytracer

import raytracer.math.Ray

/**
  * Created by Basim on 05/01/2017.
  */
trait SceneObject {
  def intersect(ray: Ray): Option[Intersection]
}
