package raytracer.bxdf

import raytracer.Spectrum
import raytracer.math.Vec3

/**
  * Created by Basim on 26/02/2017.
  */
trait BxDF {

  def matches(flags: Int): Boolean = (flags & bsdfType) == bsdfType

  val bsdfType: Int

  def apply(wo: Vec3, wi: Vec3): Spectrum

  def pdf(wo: Vec3, wi: Vec3): Double = {
    if (sameHemisphere(wo, wi)) absCosTheta(wi) / Math.PI else 0
  }

  def sample(wo: Vec3, u1: Double, u2: Double): (Vec3, Spectrum, Double) = { // wi, output, pdf
    val wi = cosineSampleHemisphere(u1, u2)
    val wi2 = if (wo.z < 0) Vec3(wi.x, wi.y, -wi.z) else wi
    (wi2, apply(wo, wi2), pdf(wo, wi2))
  }
}
