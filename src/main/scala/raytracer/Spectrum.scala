package raytracer

/**
  * Created by Basim on 05/01/2017.
  */
case class Spectrum(r: Double, g: Double, b: Double) {

  def this(colour: Double) = this(colour, colour, colour)

  def toRGBInt: Int = (b*255.0).toInt + ((g*255.0).toInt << 8) + ((r*255.0).toInt << 16)

  def +(that: Spectrum): Spectrum = Spectrum(r + that.r, g + that.g, b + that.b)

  def +(that: Double): Spectrum = Spectrum(r + that, g + that, b + that)

  def -(that: Spectrum): Spectrum = Spectrum(r - that.r, g - that.g, b - that.b)

  def *(sf: Double): Spectrum = Spectrum(r * sf, g * sf, b * sf)

  def /(sf: Double): Spectrum = this * (1 / sf)

  def *(that: Spectrum): Spectrum = Spectrum(r * that.r, g * that.g, b * that.b)

  def /(that: Spectrum): Spectrum = Spectrum(r / that.r, g / that.g, b / that.b)

  def pow(sf: Double): Spectrum = Spectrum(Math.pow(r, sf), Math.pow(g, sf), Math.pow(b, sf))

  def getY(): Double = r * Spectrum.RGB_Y(0) + g * Spectrum.RGB_Y(1) + b * Spectrum.RGB_Y(2)

  @inline
  private def clamp(v: Double, m: Double) = Math.max(0, Math.min(v, m))

  def clamp(max: Double = 1): Spectrum = Spectrum(clamp(r, max), clamp(g, max), clamp(b, max))

  def isBlack(epsilon: Double = 0): Boolean = r <= epsilon && g <= epsilon && b <= epsilon
}

object Spectrum {
  val BLACK = Spectrum(0, 0, 0)
  val WHITE = Spectrum(1, 1, 1)
  val RGB_Y = Array(0.212671, 0.715160, 0.072169)

  def fromRGBInt(col: Int): Spectrum = {
    val r = (col >> 16) & 0xFF
    val g = (col >> 8) & 0xFF
    val b = col & 0xFF
    Spectrum(r / 255.0, g / 255.0, b / 255.0)
  }

  trait Scalable { def *(that: Spectrum): Spectrum }
  implicit def doubleToScalable(d: Double): Scalable = _ * d
}