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

  val fresnel = new FresnelNoOp() //new FresnelDielectric(1.5, 1)

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {
    val bsdf = new BSDF(dgShading, dgGeom.nn)

    val d = kd(dgShading).clamp
    val s = ks(dgShading).clamp

    bsdf.add(new Lambertian(d))
    bsdf.add(new SpecularReflection(s, fresnel))

    bsdf
  }
}
