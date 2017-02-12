package raytracer.shapes
import raytracer.Spectrum
import raytracer.math.{BBox, Point, Ray, Transform}

/**
  * Created by Basim on 12/02/2017.
  */
case class TriangleMesh(
  indices: Array[Int],
  points: Array[Point]
) extends Shape {

  val triangles = indices grouped(3) map { t =>
    Triangle(points(t(0)), points(t(1)), points(t(2)), Spectrum.WHITE)
  } toArray

  override val objectToWorld: Transform = Transform.identity
  override val worldToObject: Transform = Transform.identity

  override val objectBounds: BBox = triangles.map(_.objectBounds).reduce(_.union(_))

  // TODO: Improve the triangle intersection efficiency
  override def intersect(ray: Ray): Option[(DifferentialGeometry, Double)] = {
    val intersections = triangles
      .map(_.intersect(ray))
      .filter(_.isDefined)

    if (intersections.isEmpty) None
    else intersections.minBy(_.get._2)
  }
}
