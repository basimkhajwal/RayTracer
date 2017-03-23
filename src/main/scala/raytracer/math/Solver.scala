package raytracer.math

/**
  * Created by Basim on 21/03/2017.
  */
object Solver {

  val EPSILON = 1e-9

  def linearEquation(mat: Array[Double], y0: Double, y1: Double): Option[(Double, Double)] = {
    val det = mat(0)*mat(3)-mat(1)*mat(2)
    if (math.abs(det) < EPSILON) return None

    val x0 = (mat(3)*y0 - mat(1)*y1) / det
    val x1 = (mat(0)*y1 - mat(2)*y0) / det

    if (x0 == Double.NaN || x1 == Double.NaN) None
    else Some(x0, x1)
  }
}
