package raytracer

/**
  * Created by Basim on 05/01/2017.
  */
case class Spectrum(val r: Double, val g: Double, val b: Double) {
  def toRGBInt: Int = (b*255.0).toInt + ((g*255.0).toInt << 8) + ((r*255.0).toInt << 16)

  def +(that: Spectrum): Spectrum = Spectrum(r + that.r, g + that.g, b + that.b)
  def *(sf: Double): Spectrum = Spectrum(r * sf, g * sf, b * sf)
  def *(that: Spectrum): Spectrum = Spectrum(r * that.r, g * that.g, b * that.b)

  def clamp: Spectrum = Spectrum(Math.min(r, 1), Math.min(g, 1), Math.min(b, 1))
}

object Spectrum {
  val BLACK = Spectrum(0, 0, 0)
  val WHITE = Spectrum(1, 1, 1)

  trait Scalable { def *(that: Spectrum): Spectrum }
  implicit def doubleToScalable(d: Double): Scalable = _ * d
}