package raytracer.parsing

import raytracer.Spectrum
import raytracer.materials.Material
import raytracer.math.Transform
import raytracer.textures.Texture

import scala.collection.mutable

/**
  * Created by Basim on 11/02/2017.
  */
class GraphicsState {

  var materialParams: ParamSet = new ParamSet()
  var material: String = "matte"
  var namedMaterial: String = ""

  val namedTransforms: mutable.Map[String, Transform] = mutable.Map()
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

  def makeCopy(): GraphicsState = {
    val copy = new GraphicsState

    copy.materialParams = materialParams
    copy.material = material
    copy.namedMaterial = namedMaterial

    copy.namedTransforms ++= namedTransforms
    copy.namedMaterials ++= namedMaterials
    copy.floatTextures ++= floatTextures
    copy.spectrumTextures ++= spectrumTextures

    copy
  }
}
