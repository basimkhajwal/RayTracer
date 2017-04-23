package raytracer

/**
  * Created by Basim on 05/01/2017.
  */
case class Spectrum(r: Double, g: Double, b: Double) {

  /*
  assert(r >= 0)
  assert(g >= 0)
  assert(b >= 0)*/

  def this(colour: Double) = this(colour, colour, colour)

  def toRGBInt: Int = (b*255.0).toInt + ((g*255.0).toInt << 8) + ((r*255.0).toInt << 16)

  def +(that: Spectrum): Spectrum = Spectrum(r + that.r, g + that.g, b + that.b)

  def +(that: Double): Spectrum = Spectrum(r + that, g + that, b + that)

  def -(that: Spectrum): Spectrum = Spectrum(r - that.r, g - that.g, b - that.b)

  def *(sf: Double): Spectrum = Spectrum(r * sf, g * sf, b * sf)

  def /(sf: Double): Spectrum = this * (1 / sf)

  def *(that: Spectrum): Spectrum = Spectrum(r * that.r, g * that.g, b * that.b)

  def /(that: Spectrum): Spectrum = Spectrum(r / that.r, g / that.g, b / that.b)

  @inline
  private def clamp(v: Double, m: Double) = Math.max(0, Math.min(v, m))

  def clamp(max: Double = 1): Spectrum = Spectrum(clamp(r, max), clamp(g, max), clamp(b, max))

  def isBlack(epsilon: Double = 0): Boolean = r <= epsilon && g <= epsilon && b <= epsilon
}

object Spectrum {
  val BLACK = Spectrum(0, 0, 0)
  val WHITE = Spectrum(1, 1, 1)

  trait Scalable { def *(that: Spectrum): Spectrum }
  implicit def doubleToScalable(d: Double): Scalable = _ * d
}