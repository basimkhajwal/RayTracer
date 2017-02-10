package raytracer

import raytracer.math.Ray
import raytracer.primitives.{Intersection, Primitive}
import raytracer.shapes.{DifferentialGeometry, Shape}

/**
  * Created by Basim on 18/12/2016.
  */
class Scene(val lights: List[PointLight], val objects: List[Primitive]) {

  def intersect(ray: Ray): Option[Intersection] = {
    val intersections = objects
      .map(o => o.intersect(ray))
      .filterNot(_.isEmpty)

    if (intersections.isEmpty) None
    else intersections.minBy { case Some(Intersection(_, _, t)) => t }
  }
}
