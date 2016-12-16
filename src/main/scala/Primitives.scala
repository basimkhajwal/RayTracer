/**
  * Created by Basim on 15/12/2016.
  */

import Constants._

case class Ray(val start: Vec3, val dir: Vec3) {
  private val m = dir.mag2
  assert(m > 1-EPSILON && m<1+EPSILON)
}

case class Spectrum(val r: Double, val g: Double, val b: Double) {
  def toRGBInt: Int = (r*255.0).toInt + ((g*255.0).toInt << 8) + ((b*255.0).toInt << 16)

  def +(that: Spectrum): Spectrum = Spectrum(r + that.r, g + that.g, b + that.b)
  def *(sf: Double): Spectrum = Spectrum(r * sf, g * sf, b * sf)
  def clamp: Spectrum = Spectrum(Math.min(r, 1), Math.min(g, 1), Math.min(b, 1))
}
object Spectrum {
  val BLACK = Spectrum(0, 0, 0)
  val WHITE = Spectrum(1, 1, 1)

  trait Scalable { def *(that: Spectrum): Spectrum }
  implicit def doubleToScalable(d: Double): Scalable = _ * d
}

case class PointLight(val pos: Vec3, val colour: Spectrum)

trait Intersection
object Miss extends Intersection
case class Hit(val t: Double, val point: Vec3, val normal: Vec3) extends Intersection
