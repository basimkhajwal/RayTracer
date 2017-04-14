package raytracer.math

/**
  * Created by Basim on 15/12/2016.
  */
object Vec3 {
  val ZERO = Vec3(0, 0, 0)
  val ONE = Vec3(1, 1, 1)

  trait Scalable { def *(that: Vec3): Vec3 }
  implicit def doubleToScalable(x: Double): Scalable = _ * x

  def createCoordinateSystem(v1: Vec3): (Vec3, Vec3) = {
    val v2 = if (Math.abs(v1.x) > Math.abs(v1.y)) {
      val invLen = 1.0 / Math.sqrt(v1.x*v1.x + v1.z*v1.z)
      Vec3(-v1.z * invLen, 0, v1.x * invLen)
    }
    else {
      val invLen = 1.0 / Math.sqrt(v1.y*v1.y + v1.z*v1.z)
      Vec3(0, v1.z * invLen, -v1.y * invLen)
    }
    val v3 = v1 cross v2

    (v2, v3)
  }

  def sphericalDirection(sintheta: Double, costheta: Double, phi: Double): Vec3 = {
    Vec3(sintheta * math.cos(phi), sintheta * math.sin(phi), costheta)
  }
}

case class Vec3(x: Double, y: Double, z: Double) {
  require(!x.isNaN)
  require(!y.isNaN)
  require(!z.isNaN)

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

