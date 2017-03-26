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

}
