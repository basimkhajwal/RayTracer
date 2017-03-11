package raytracer.bxdf
import raytracer.Spectrum
import raytracer.math.Vec3

/**
  * Created by Basim on 10/03/2017.
  */
class SpecularReflection(val colour: Spectrum) extends BxDF{

  override val bsdfType: Int = BSDF.SPECULAR | BSDF.REFLECTION

  override def apply(wo: Vec3, wi: Vec3): Spectrum = Spectrum.BLACK

  override def sample(wo: Vec3, u1: Double, u2: Double): (Vec3, Spectrum) = {
    val wi: Vec3 = Vec3(-wo.x, -wo.y, wo.z)
    (wi, colour)
  }
}
