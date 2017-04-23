package raytracer.materials

import raytracer.Spectrum
import raytracer.bxdf.{BSDF, FresnelNoOp, SpecularReflection}
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture

/**
  * Created by Basim on 29/03/2017.
  */
class MirrorMaterial (
  val r: Texture[Spectrum]
) extends Material {

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {
    val bsdf = new BSDF(dgShading, dgGeom.nn)

    val colour = r(dgShading).clamp()
    if (!colour.isBlack()) bsdf.add(new SpecularReflection(colour, new FresnelNoOp()))

    bsdf
  }
}
