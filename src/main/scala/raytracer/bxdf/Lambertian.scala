package raytracer.bxdf

import raytracer.Spectrum
import raytracer.math.Vec3

/**
  * Created by Basim on 26/02/2017.
  */
class Lambertian(reflectance: Spectrum) extends BxDF {

  override val bsdfType: Int = BSDF.REFLECTION | BSDF.DIFFUSE

  override def apply(wo: Vec3, wi: Vec3): Spectrum = reflectance * (1 / Math.PI)
}
