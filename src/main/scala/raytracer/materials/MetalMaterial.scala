package raytracer.materials

import raytracer.Spectrum
import raytracer.bxdf.{BSDF, Blinn, FresnelConductor, Microfacet}
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.Texture

/**
  * Created by Basim on 21/04/2017.
  */
class MetalMaterial(
  val eta: Texture[Spectrum],
  val K: Texture[Spectrum],
  val roughness: Texture[Double]
)extends Material{

  override def getBSDF(dgGeom: DifferentialGeometry, dgShading: DifferentialGeometry): BSDF = {

    val bsdf = new BSDF(dgShading, dgGeom.nn)

    val fresnel = new FresnelConductor(eta(dgShading), K(dgShading))
    val md = new Blinn(1 / roughness(dgShading))

    bsdf.add(new Microfacet(Spectrum.WHITE, fresnel, md))

    bsdf
  }
}

object MetalMaterial {
  val copperN = Spectrum(0.200416267,	0.924046397, 1.10221624)
  val copperK = Spectrum(3.91294384, 2.45284033, 2.14220047)
}
