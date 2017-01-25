package raytracer.math

/**
  * Created by Basim on 15/12/2016.
  */
object Vec3 {
  val ZERO = Vec3(0, 0, 0)
  val ONE = Vec3(1, 1, 1)

  trait Scalable { def *(that: Vec3): Vec3 }
  implicit def doubleToScalable(x: Double): Scalable = _ * x
}

case class Vec3(x: Double, y: Double, z: Double) {
  require(x != Double.NaN)
  require(y != Double.NaN)
  require(z != Double.NaN)

  lazy val mag2 = x*x + y*y + z*z
  lazy val mag = Math.sqrt(mag2)
  lazy val nor = this / mag

  def +(that: Vec3): Vec3 = Vec3(x + that.x, y + that.y, z + that.z)
  def -(that: Vec3): Vec3 = Vec3(x - that.x, y - that.y, z - that.z)

  def apply(index: Int): Double = if (index == 0) x else if (index == 1) y else z

  lazy val unary_- = Vec3(-x, -y, -z)

  def /(sf: Double): Vec3 = this * (1 / sf)
  def *(sf: Double): Vec3 = Vec3(x * sf, y * sf, z * sf)

  def dot(that: Vec3): Double = x*that.x + y*that.y + z*that.z

  def cross(that: Vec3): Vec3 = Vec3(
    y*that.z - z*that.y,
    z*that.x - x*that.z,
    x*that.y - y*that.x
  )
}

