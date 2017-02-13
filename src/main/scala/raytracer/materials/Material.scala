package raytracer.materials

import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture
import raytracer.{BSDF, Lambertian, Spectrum}

/**
  * Created by Basim on 28/01/2017.
  */
trait Material {
  def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF
}

class MatteMaterial (
  kd: Texture[Spectrum]
  /* sig: Texture[Float] assumed to be 0 for now,
   * bump: Texture[Float] also assumed to be 0 */
) extends Material {

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {
    val bsdf = new BSDF(dgGeom, dgGeom.nn)
    bsdf.add(new Lambertian(kd(dgGeom)))
    bsdf
  }
}
