package raytracer.math

import scala.math._

/**
  * Created by Basim on 09/01/2017.
  */
object BBox {
  val empty = BBox(Point(Double.PositiveInfinity, Double.PositiveInfinity, Double.PositiveInfinity),
    Point(Double.NegativeInfinity, Double.NegativeInfinity, Double.NegativeInfinity))

  def fromPoints(points: Point*): BBox = points.foldLeft(empty)(_.union(_))
}

case class BBox(pMin: Point, pMax: Point) {

  @inline
  private final def inRange(v: Double, a: Double, b: Double): Boolean = v >= a && v <= b

  def apply(index: Int): Point = {
    if (index == 0) pMin
    else if (index == 1) Point(pMin.x, pMin.y, pMax.z)
    else if (index == 2) Point(pMin.x, pMax.y, pMin.z)
    else if (index == 3) Point(pMin.x, pMax.y, pMax.z)
    else if (index == 4) Point(pMax.x, pMin.y, pMin.z)
    else if (index == 5) Point(pMax.x, pMin.y, pMax.z)
    else if (index == 6) Point(pMax.x, pMax.y, pMin.z)
    else pMax
  }

  def contains(point: Point): Boolean = {
    inRange(point.x, pMin.x, pMax.x) &&
    inRange(point.y, pMin.y, pMax.y) &&
    inRange(point.z, pMin.z, pMax.z)
  }

  def maximumExtent(): Int = {
    val a = pMax.x - pMin.x
    val b = pMax.y - pMin.y
    val c = pMax.z - pMin.z
    if (a < b) {
      if (a < c) 0
      else 2
    } else {
      if (b < c) 1
      else 2
    }
  }

  def checkIntersect(ray: Ray): Option[Double] = {
    var i = 0
    var t0 = 0.0
    var t1 = Double.PositiveInfinity
    var valid = true

    while (i < 3) {
      val invRayDir = 1 / ray.dir(i)
      var tNear = (pMin(i) - ray.start(i)) * invRayDir
      var tFar  = (pMax(i) - ray.start(i)) * invRayDir

      if (tNear > tFar) {
        val temp = tNear
        tNear = tFar
        tFar = temp
      }

      t0 = if (tNear > t0) tNear else t0
      t1 = if (tFar < t1) tFar else t1
      if (t0 > t1) {
        valid = false
        i = 3
      }

      i += 1
    }

    if (valid) Some(t0) else None
  }

  def union(p: Point): BBox = {
    BBox(
      Point( min(pMin.x, p.x), min(pMin.y, p.y), min(pMin.z, p.z) ),
      Point( max(pMax.x, p.x), max(pMax.y, p.y), max(pMax.z, p.z) )
    )
  }

  def union(that: BBox): BBox = {
    BBox(
      Point( min(pMin.x, that.pMin.x), min(pMin.y, that.pMin.y), min(pMin.z, that.pMin.z) ),
      Point( max(pMax.x, that.pMax.x), max(pMax.y, that.pMax.y), max(pMax.z, that.pMax.z) )
    )
  }
}
