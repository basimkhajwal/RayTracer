package raytracer

/**
  * Created by Basim on 18/12/2016.
  */
class Scene(val lights: List[PointLight], val objects: List[SceneObject]) {

  def intersect(ray: Ray): Intersection = {
    val intersections = objects
      .map(o => o.intersect(ray))
      .filterNot(_ == Miss)

    if (intersections.isEmpty) Miss
    else intersections.minBy { case Hit(t, _, _, _) => t }
  }
}
