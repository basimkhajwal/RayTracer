/**
  * Created by Basim on 15/12/2016.
  */
object Vec3 {
  val ZERO = Vec3(0, 0, 0)
  val ONE = Vec3(1, 1, 1)

  trait Scalable { def *(that: Vec3): Vec3 }

  implicit def doubleToScalable(x: Double): Scalable = (that: Vec3) => that * x
}

case class Vec3(x: Double, y: Double, z: Double) {

  lazy val mag2 = x*x + y*y + z*z
  lazy val mag = Math.sqrt(mag2)
  lazy val nor = this / mag

  def +(that: Vec3): Vec3 = Vec3(x + that.x, y + that.y, z + that.z)
  def -(that: Vec3): Vec3 = Vec3(x - that.x, y - that.y, z - that.z)

  def /(sf: Double): Vec3 = this * (1 / sf)
  def *(sf: Double): Vec3 = Vec3(x * sf, y * sf, z * sf)

  private def sqr(x: Double) = x*x

  def dist2(other: Vec3) = sqr(other.x - x) + sqr(other.y - y) + sqr(other.z - z)
  def dist(other: Vec3) = Math.sqrt(dist2(other))

  def dot(that: Vec3): Double = x*that.x + y*that.y + z*that.z
}

