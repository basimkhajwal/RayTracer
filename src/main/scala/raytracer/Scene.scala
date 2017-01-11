package raytracer

import raytracer.math.Ray
import raytracer.shapes.Shape

/**
  * Created by Basim on 18/12/2016.
  */
class Scene(val lights: List[PointLight], val objects: List[Shape]) {

  def intersect(ray: Ray): Option[Intersection] = {
    val intersections = objects
      .map(o => o.intersect(ray))
      .filterNot(_.isEmpty)

    if (intersections.isEmpty) None
    else intersections.minBy { case Some(Intersection(t, _, _, _)) => t }
  }
}
