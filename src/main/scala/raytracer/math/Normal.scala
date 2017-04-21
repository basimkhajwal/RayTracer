package raytracer.math

/**
  * Created by Basim on 20/04/2017.
  */
case class Normal(x: Double, y: Double, z: Double) {
  require(!x.isNaN)
  require(!y.isNaN)
  require(!z.isNaN)

  lazy val mag2 = x*x + y*y + z*z
  lazy val mag = Math.sqrt(mag2)
  lazy val nor = this / mag

  def +(that: Normal) = Normal(x + that.x, y + that.y, z + that.z)
  def -(that: Normal) = Normal(x - that.x, y - that.y, z - that.z)

  def dot(that: Vec3): Double = x*that.x + y*that.y + z*that.z
  def dot(that: Normal): Double = x*that.x + y*that.y + z*that.z

  def *(sf: Double) = Normal(x*sf, y*sf, z*sf)
  def /(sf: Double) = Normal(x/sf, y/sf, z/sf)

  def cross(that: Vec3): Vec3 = Vec3(
    y*that.z - z*that.y,
    z*that.x - x*that.z,
    x*that.y - y*that.x
  )
}

object Normal {
  val ZERO = Normal(0,0,0)
  val ONE = Normal(1,1,1)

  trait Scalable { def *(that: Normal): Normal }
  implicit def doubleToScalable(x: Double): Scalable = _ * x

  def apply(v: Vec3): Normal = Normal(v.x, v.y, v.z)
}