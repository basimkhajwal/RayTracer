package raytracer.bxdf
import raytracer.Spectrum
import raytracer.math.Vec3

/**
  * Created by Basim on 12/04/2017.
  */
class SpecularTransmission(
  val transmittance: Spectrum,
  val etaI: Double,
  val etaT: Double
) extends BxDF {

  val fresnel = new FresnelDielectric(etaI, etaT)

  override val bsdfType: Int = BSDF.TRANSMISSION | BSDF.SPECULAR

  override def apply(wo: Vec3, wi: Vec3): Spectrum = Spectrum.BLACK

  override def sample(wo: Vec3, u1: Double, u2: Double): (Vec3, Spectrum) = {

    val entering = cosTheta(wo) > 0
    val (ei, et) = if (entering) (etaI, etaT) else (etaT, etaI)

    val sini2 = sinTheta2(wo)
    val eta = ei / et
    val sint2 = eta * eta * sini2

    if (sint2 >= 1) return (Vec3.ZERO, Spectrum.BLACK)

    val cost = (if (entering) -1 else 1) * math.sqrt(1 - sint2)
    val sintOverSini = eta
    val wi = Vec3(sintOverSini * -wo.x, sintOverSini * -wo.y, cost).nor

    val F = fresnel.evaluate(cosTheta(wo))
    val lum = (Spectrum.WHITE-F) * transmittance * ((et*et)/(ei*ei)) / absCosTheta(wi)

    (wi, lum)
  }
}
