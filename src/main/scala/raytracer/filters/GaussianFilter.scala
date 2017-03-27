package raytracer.filters

/**
  * Created by Basim on 27/03/2017.
  */
class GaussianFilter(xw: Double, yw: Double, val alpha: Double) extends Filter(xw, yw) {

  val expX = math.exp(-alpha * xWidth * xWidth)
  val expY = math.exp(-alpha * yWidth * yWidth)

  private def gaussian(d: Double, expv: Double): Double = (math.exp(-alpha * d * d) - expv).max(0)

  override def evaluate(x: Double, y: Double) = gaussian(x, expX) * gaussian(y, expY)
}
