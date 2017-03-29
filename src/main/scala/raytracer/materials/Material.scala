package raytracer.materials

import raytracer.bxdf.{BSDF, Lambertian}
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture
import raytracer.Spectrum

/**
  * Created by Basim on 28/01/2017.
  */
trait Material {
  def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF
}


