package raytracer.filters

/**
  * Created by Basim on 27/03/2017.
  */
class BoxFilter(xw: Double, yw: Double) extends Filter(xw, yw) {
  override def evaluate(x: Double, y: Double): Double = 1
}
