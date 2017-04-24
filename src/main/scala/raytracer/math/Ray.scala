package raytracer.math

/**
  * Created by Basim on 05/01/2017.
  */
case class Ray(start: Point, dir: Vec3, depth: Int)

class RayDifferential(
  start: Point, dir: Vec3, depth: Int,
  val rxOrigin: Point, val ryOrigin: Point,
  val rxDirection: Vec3, val ryDirection: Vec3
) extends Ray(start, dir, depth)

object RayDifferential {
  def apply(
    start: Point, dir: Vec3, depth: Int,
    rxOrigin: Point, ryOrigin: Point,
    rxDirection: Vec3, ryDirection: Vec3
  ): RayDifferential = {
    new RayDifferential(
      start, dir, depth,
      rxOrigin, ryOrigin,
      rxDirection, ryDirection
    )
  }
}
