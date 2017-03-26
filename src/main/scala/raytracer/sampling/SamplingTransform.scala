package raytracer.sampling

/**
  * Created by Basim on 26/03/2017.
  */
object SamplingTransform {

  def uniformSampleDisk(u1: Double, u2: Double): (Double, Double) = {
    val r = Math.sqrt(u1)
    val theta = 2 * Math.PI * u2
    (r * Math.cos(theta), r * Math.sin(theta))
  }

  def concentricSampleDisk(u1: Double, u2: Double): (Double, Double) = {
    val sx = 2*u1-1
    val sy = 2*u2-1

    if (sx == 0.0 && sy == 0.0) return (0, 0)

    var r: Double = 0
    var theta: Double = 0

    if (sx >= -sy) {
      if (sx > sy) {
        r = sx
        theta = if (sy > 0.0) sy/r else 8 + sy/r
      } else {
        r = sy
        theta = 2 - sx/r
      }
    } else {
      if (sx <= sy) {
        r = -sx
        theta = 4 - sy/r
      } else {
        r = -sy
        theta = 6 + sx/r
      }
    }

    theta *= Math.PI / 4
    (r * Math.cos(theta), r * Math.sin(theta))
  }
}
