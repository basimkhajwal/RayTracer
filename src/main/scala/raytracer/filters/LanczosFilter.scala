package raytracer.filters

/**
  * Created by Basim on 23/04/2017.
  */
class LanczosFilter(xw: Double, yw: Double, val tau: Double) extends Filter(xw, yw){
  override def evaluate(x: Double, y: Double): Double = {
    LanczosFilter.lanczos(x / xWidth, tau) * LanczosFilter.lanczos(y / yWidth, tau)
  }
}

object LanczosFilter {

  def lanczos(n: Double, tau: Double = 2): Double = {
    val x = n.abs * math.Pi
    if (x < math.Pi*1e-5) return 1
    if (x > math.Pi) return 0

    val sinc = math.sin(x * tau) / (x * tau)
    val lanczos = math.sin(x) / x
    sinc * lanczos
  }
}
