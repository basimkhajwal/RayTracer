package raytracer.materials

import raytracer.Spectrum
import raytracer.bxdf.BSDF
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture

/**
  * Created by Basim on 29/03/2017.
  */
class PlasticMaterial(
  val kd: Texture[Spectrum],
  val ks: Texture[Spectrum],
  val rough: Texture[Float]
  /* bump: Texture[Float] */
) extends Material {

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {
    val bsdf = new BSDF(dgShading, dgGeom.nn)

    // TODO: Add fresnel for Microfacet specular reflection

    bsdf
  }
}
