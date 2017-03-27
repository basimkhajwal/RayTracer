package raytracer.filters

/**
  * Created by Basim on 27/03/2017.
  */
abstract class Filter(val xWidth: Double, val yWidth: Double){
  def evaluate(x: Double, y: Double): Double
}
