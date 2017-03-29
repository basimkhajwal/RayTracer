package raytracer.materials

import raytracer.Spectrum
import raytracer.bxdf.{BSDF, Lambertian}
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture

/**
  * Created by Basim on 29/03/2017.
  */
class MatteMaterial (
  kd: Texture[Spectrum]
  /* sig: Texture[Float] assumed to be 0 for now,
   * bump: Texture[Float] also assumed to be 0 */
) extends Material {

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {
    val bsdf = new BSDF(dgShading, dgGeom.nn)
    bsdf.add(new Lambertian(kd(dgGeom)))
    bsdf
  }
}
