package raytracer.parsing

import raytracer.{Material, Spectrum, Texture}
import raytracer.math.Transform

import scala.collection.mutable

/**
  * Created by Basim on 11/02/2017.
  */
class GraphicsState {
  val namedTransforms: mutable.Map[String, Transform] = mutable.Map()

  var materialParams: ParamSet = new ParamSet()
  var material: String = "matte"
  var namedMaterial: String = ""

  val namedMaterials: mutable.Map[String, Material] = mutable.Map()
  val floatTextures: mutable.Map[String, Texture[Double]] = mutable.Map()
  val spectrumTextures: mutable.Map[String, Texture[Spectrum]] = mutable.Map()

  def getTextureParams(geomParams: ParamSet, matParams: ParamSet = materialParams): TextureParams = {
    TextureParams(geomParams, matParams, floatTextures, spectrumTextures)
  }

  def createMaterial(params: ParamSet): Material = {
    if (namedMaterial != "" && namedMaterials.isDefinedAt(namedMaterial))
      namedMaterials(namedMaterial)
    else
      SceneFactory.makeMaterial(material, getTextureParams(params))
  }
}
