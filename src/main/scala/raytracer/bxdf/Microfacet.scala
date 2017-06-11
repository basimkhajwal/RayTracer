package raytracer.bxdf

import raytracer.Spectrum
import raytracer.math.Vec3

/**
  * Created by Basim on 02/04/2017.
  */
class Microfacet(
  val reflectance: Spectrum,
  val fresnel: Fresnel,
  val distribution: MicrofacetDistribution
) extends BxDF {

  override val bsdfType: Int = BSDF.REFLECTION | BSDF.GLOSSY

  override def apply(wo: Vec3, wi: Vec3): Spectrum = {
    val cosThetaO = absCosTheta(wo)
    val cosThetaI = absCosTheta(wi)

    if (cosThetaI == 0 || cosThetaO == 0) return Spectrum.BLACK

    val whFull = wi + wo
    if (whFull.mag2 == 0) return Spectrum.BLACK
    val wh = whFull.nor

    val cosThetaH = wi dot wh
    val F = fresnel.evaluate(cosThetaH)

    reflectance * distribution.D(wh) * G(wo, wi, wh) * F / (4 * cosThetaI * cosThetaO)
  }

  private def G(wo: Vec3, wi: Vec3, wh: Vec3): Double = {
    val NdotWh = absCosTheta(wh)
    val NdotWo = absCosTheta(wo)
    val NdotWi = absCosTheta(wi)
    val WOdotWh = (wo dot wh).abs
    math.min(1, math.min(2 * NdotWh * NdotWo / WOdotWh, 2 * NdotWh * NdotWi / WOdotWh ))
  }

  override def sample(wo: Vec3, u1: Double, u2: Double): (Vec3, Spectrum, Double) = {
    val (wi, pdf) = distribution.sample(wo, u1, u2)
    if (!sameHemisphere(wo, wi)) (wi, Spectrum.BLACK, pdf)
    else (wi, apply(wo, wi), pdf)
  }
}

trait MicrofacetDistribution {
  def D(wh: Vec3): Double
  def pdf(wo: Vec3, wi: Vec3): Double = 0
  def sample(wo: Vec3, u1: Double, u2: Double): (Vec3, Double)
}

class Blinn(
  e: Double
) extends MicrofacetDistribution {
  val exponent = if (e > 10000 || e.isNaN) 10000 else e

  def D(wh: Vec3): Double = {
    val costhetah = absCosTheta(wh)
    (exponent+2) * (0.5 / math.Pi) * math.pow(costhetah, exponent)
  }

  override def sample(wo: Vec3, u1: Double, u2: Double): (Vec3, Double) = {
    val costheta = math.pow(u1, 1 / (exponent+1))
    val sintheta = math.sqrt(math.max(0, 1 - costheta*costheta))
    val phi = u2 * 2 * math.Pi

    val whFirst = Vec3.sphericalDirection(sintheta, costheta, phi)
    val wh = if (sameHemisphere(wo, whFirst)) whFirst else -whFirst

    val wi = -wo + 2 * (wo dot wh) * wh
    val pdf = innerPDF(wo, wh, costheta)

    (wi, pdf)
  }

  private def innerPDF(wo: Vec3, wh: Vec3, costheta: Double): Double = {
    if (wo.dot(wh) > 0)
      ((exponent + 1) * math.pow(costheta, exponent)) /
        (2 * Math.PI * 4 * wo.dot(wh))
    else
      0
  }

  override def pdf(wo: Vec3, wi: Vec3): Double = {
    val wh = (wo + wi).nor
    val costheta = absCosTheta(wh)
    innerPDF(wo, wh, costheta)
  }
}
