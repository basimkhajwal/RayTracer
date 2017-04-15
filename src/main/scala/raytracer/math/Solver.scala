package raytracer.math

/**
  * Created by Basim on 21/03/2017.
  */
object Solver {

  val EPSILON = 1e-9

  def quadratic(A: Double, B: Double, C: Double): Option[(Double, Double)] = {
    val d2 = B*B - 4*A*C
    if (d2 < 0) return None
    val d = math.sqrt(d2)
    val t0 = (-B - d) / (2 * A)
    val t1 = (-B + d) / (2 * A)
    Some((t0, t1))
  }

  def linearEquation(mat: Array[Double], y0: Double, y1: Double): Option[(Double, Double)] = {
    val det = mat(0)*mat(3)-mat(1)*mat(2)
    if (math.abs(det) < EPSILON) return None

    val x0 = (mat(3)*y0 - mat(1)*y1) / det
    val x1 = (mat(0)*y1 - mat(2)*y0) / det

    if (x0.isNaN || x1.isNaN) None else Some(x0, x1)
  }
}
