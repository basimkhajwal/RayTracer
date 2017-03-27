package raytracer.filters

/**
  * Created by Basim on 27/03/2017.
  */
class TriangleFilter(xw: Double, yw: Double) extends Filter(xw, yw) {
  override def evaluate(x: Double, y: Double): Double = {
    (xWidth - x.abs).max(0) * (yWidth - y.abs).max(0)
  }
}
