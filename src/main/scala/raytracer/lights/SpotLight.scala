package raytracer.lights

import raytracer.Spectrum
import raytracer.math.{Point, Transform, Vec3}

/**
  * Created by Basim on 18/04/2017.
  */
class SpotLight(
  lightToWorld: Transform,
  intensity: Spectrum,
  width: Double,
  fall: Double
)extends Light {

  val worldToLight = lightToWorld.inverse

  val lightPos = lightToWorld(Point.ZERO)
  val cosTotalWidth = math.cos(math.toRadians(width))
  val cosFalloffStart = math.cos(math.toRadians(fall))

  val power = intensity * 2 * math.Pi * (1 - 0.5*(cosFalloffStart + cosTotalWidth))

  def falloff(w: Vec3): Double = {
    val wl = worldToLight(w).nor
    val costheta = wl.z
    if (costheta < cosTotalWidth) return 0
    if (costheta > cosFalloffStart) return 1

    val delta = (costheta - cosTotalWidth) / (cosFalloffStart - cosTotalWidth)
    delta*delta*delta*delta
  }

  override def sample(point: Point): (Spectrum, Vec3, Double) = {
    val lightVec = lightPos - point
    val wi = lightVec.nor
    val lum = intensity * falloff(-wi) / lightVec.mag2

    (lum, wi, lightVec.mag)
  }
}
