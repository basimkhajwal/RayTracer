package raytracer.materials
import raytracer.Spectrum
import raytracer.bxdf._
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture

/**
  * Created by Basim on 01/05/2017.
  */
class UberMaterial(
  val Kd: Texture[Spectrum],
  val Ks: Texture[Spectrum],
  val Kr: Texture[Spectrum],
  val Kt: Texture[Spectrum],
  val roughness: Texture[Double],
  val index: Texture[Double],
  val opacity: Texture[Spectrum]
) extends Material {

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {

    val bsdf = new BSDF(dgShading, dgGeom.nn)

    val op = opacity(dgShading).clamp()
    if (!op.isWhite()) {
      bsdf.add(new SpecularTransmission(Spectrum.WHITE - op, 1, 1))
    }

    val kd = Kd(dgShading).clamp() * op
    if (!kd.isBlack()) {
      bsdf.add(new Lambertian(kd))
    }

    val e = index(dgShading)
    val fresnel = new FresnelDielectric(e, 1)
    val ks = Ks(dgShading).clamp() * op
    if (!ks.isBlack()) {
      val rough = roughness(dgShading)
      bsdf.add(new Microfacet(ks, fresnel, new Blinn(1 / rough)))
    }

    val kr = Kr(dgShading).clamp() * op
    if (!kr.isBlack()) {
      bsdf.add(new SpecularReflection(kr, fresnel))
    }

    val kt = Kt(dgShading).clamp() * op
    if (!kt.isBlack()) {
      bsdf.add(new SpecularTransmission(kt, e, 1))
    }

    bsdf
  }
}
