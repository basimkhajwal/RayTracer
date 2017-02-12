package raytracer.parsing

import raytracer._

/**
  * Created by Basim on 12/02/2017.
  */
object SceneFactory {


  def makeMaterial(matType: String, textureParams: TextureParams): Material = {
    matType match {

      case "matte" => {
        new MatteMaterial(textureParams.getSpectrumTexture("kd", Spectrum(0.5, 0.5, 0.5)))
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented material type $matType")
    }
  }

  def makeSpectrumTexture(textureClass: String, params: TextureParams): Texture[Spectrum] = {
    textureClass match {
      case "constant" => {
        new ConstantTexture[Spectrum](params.getOneOr("value", Spectrum.WHITE))
      }
      case _ => throw new IllegalArgumentException(s"Unknown spectrum texture class $textureClass")
    }
  }

  def makeFloatTexture(textureClass: String, params: TextureParams): Texture[Double] = {
    textureClass match {
      case "constant" => {
        new ConstantTexture[Double](params.getOneOr("value", 1.0))
      }
      case _ => throw new IllegalArgumentException(s"Unknown float texture class $textureClass")
    }
  }
}
