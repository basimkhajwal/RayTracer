package raytracer.materials

import raytracer.Spectrum
import raytracer.bxdf._
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture

/**
  * Created by Basim on 29/03/2017.
  */
class PlasticMaterial(
  val kd: Texture[Spectrum],
  val ks: Texture[Spectrum],
  val rough: Texture[Double]
  /* bump: Texture[Float] */
) extends Material {

  val fresnel = new FresnelDielectric(1.5, 1)

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {
    val bsdf = new BSDF(dgShading, dgGeom.nn)

    val d = kd(dgShading).clamp
    val s = ks(dgShading).clamp
    val r = rough(dgShading)

    bsdf.add(new Lambertian(d))
    bsdf.add(new Microfacet(s, fresnel, new Blinn(1 / r)))

    bsdf
  }
}
