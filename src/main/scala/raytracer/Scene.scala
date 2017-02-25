package raytracer

import raytracer.lights.Light
import raytracer.math.Ray
import raytracer.primitives.{Intersection, Primitive}

/**
  * Created by Basim on 18/12/2016.
  */
class Scene(val lights: List[Light], val objects: List[Primitive]) {

  def intersect(ray: Ray, minT: Double = 0, maxT: Double = Double.PositiveInfinity): Option[Intersection] = {
    val intersections = objects
      .map(_.intersect(ray)
            .flatMap(i => if (i.time > maxT || i.time < minT) None else Some(i)))

    if (intersections.isEmpty) None
    else
      intersections.minBy(_ match {
        case None => Double.PositiveInfinity
        case Some(Intersection(_, _, t)) => t
      })
  }
}
