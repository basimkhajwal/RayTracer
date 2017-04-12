package raytracer.materials
import raytracer.Spectrum
import raytracer.bxdf.{BSDF, FresnelDielectric, SpecularReflection, SpecularTransmission}
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture

/**
  * Created by Basim on 12/04/2017.
  */
class GlassMaterial(
  val reflectance: Texture[Spectrum],
  val transmittance: Texture[Spectrum],
  val refractiveIndex: Texture[Double]
  /* bump: Texture[Double] ... */
) extends Material{

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {
    val ior = refractiveIndex(dgShading)
    val bsdf = new BSDF(dgShading, dgGeom.nn, ior)

    val R = reflectance(dgShading).clamp
    val T = transmittance(dgShading).clamp

    if (!R.isBlack()) bsdf.add(new SpecularReflection(R, new FresnelDielectric(1, ior)))

    if (!T.isBlack()) bsdf.add(new SpecularTransmission(T, 1, ior))

    bsdf
  }
}
