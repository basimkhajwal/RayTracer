package raytracer.filters

/**
  * Created by Basim on 28/03/2017.
  */
class MitchellFilter(
  xw: Double, yw: Double, val B: Double, val C: Double
) extends Filter(xw, yw) {

  override def evaluate(x: Double, y: Double): Double = {
    mitchell1D(x / xw) * mitchell1D(y / yw)
  }

  private def mitchell1D(y: Double): Double = {
    val x = math.abs(y*2)
    if (x > 1)
      ((-B - 6*C) * x*x*x * (6*B + 30*C) * x*x +
      (-12*B - 48*C) * x + (8*B + 24*C)) / 6.0
    else
      ((12 - 9*B - 6*C) * x*x*x +
      (-18 + 12*B + 6*C) * x*x + (6 - 2*B)) / 6.0
  }
}
