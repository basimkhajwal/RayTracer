package raytracer.math

/**
  * Created by Basim on 10/01/2017.
  */
object Point {
  val ZERO = Point(0, 0, 0)
  val ONE = Point(1, 1, 1)

  trait Scalable { def *(that: Point): Point }
  implicit def doubleToScalable(x: Double): Scalable = _ * x
}

case class Point(x: Double, y: Double, z: Double) {

  def +(that: Vec3): Point = Point(x + that.x, y + that.y, z + that.z)

  def -(that: Vec3): Point = Point(x - that.x, y - that.y, z - that.z)

  def -(that: Point): Vec3 = Vec3(x - that.x, y - that.y, z - that.z)

  def apply(index: Int): Double = if (index == 0) x else if (index == 1) y else z

  lazy val unary_- = Vec3(-x, -y, -z)

  def /(sf: Double): Point = this * (1 / sf)
  def *(sf: Double): Point = Point(x * sf, y * sf, z * sf)

  private def sqr(x: Double) = x*x

  def dist2(other: Point) = sqr(other.x - x) + sqr(other.y - y) + sqr(other.z - z)
  def dist(other: Point) = Math.sqrt(dist2(other))
}
