package raytracer.math

import scala.math._

/**
  * Created by Basim on 09/01/2017.
  */
class BBox(val pMin: Vec3, val pMax: Vec3) {
  require(pMin.x <= pMax.x)
  require(pMin.y <= pMax.y)
  require(pMin.z <= pMax.z)

  @inline
  private final def inRange(v: Double, a: Double, b: Double): Boolean = v >= a && v <= b

  def contains(point: Vec3): Boolean = {
    inRange(point.x, pMin.x, pMax.x) &&
    inRange(point.y, pMin.y, pMax.y) &&
    inRange(point.z, pMin.z, pMax.z)
  }

  def union(that: BBox): BBox = {
    new BBox (
      Vec3(
        min(pMin.x, that.pMin.x),
        min(pMin.y, that.pMin.y),
        min(pMin.z, that.pMin.z)
      ),
      Vec3(
        max(pMax.x, that.pMax.x),
        max(pMax.y, that.pMax.y),
        max(pMax.z, that.pMax.z)
      )
    )
  }
}
