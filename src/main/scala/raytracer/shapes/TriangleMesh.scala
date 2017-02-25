package raytracer.shapes
import raytracer.math.{BBox, Point, Ray, Transform}

/**
  * Created by Basim on 12/02/2017.
  */
case class TriangleMesh(
  indices: Array[Int],
  points: Array[Point],
  o2w: Transform
) extends Shape {

  override val objectToWorld: Transform = o2w
  override val worldToObject: Transform = objectToWorld.inverse

  val triangles = indices grouped(3) map { t =>
    Triangle(points(t(0)), points(t(1)), points(t(2)), objectToWorld)
  } toArray

  override val objectBounds: BBox = triangles.map(_.objectBounds).reduce(_.union(_))

  override def intersect(ray: Ray): Option[(DifferentialGeometry, Double)] = {
    val intersections = triangles
      .map(_.intersect(ray))
      .filter(_.isDefined)

    if (intersections.isEmpty) None
    else intersections.minBy(_.get._2)
  }
}
