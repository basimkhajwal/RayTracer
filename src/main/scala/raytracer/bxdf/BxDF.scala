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
}
