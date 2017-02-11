package raytracer.parsing

import raytracer.{ConstantTexture, Spectrum, Texture}

import scala.collection.mutable

/**
  * Created by Basim on 11/02/2017.
  */
case class TextureParams(
  geomParams: ParamSet,
  materialParams: ParamSet,
  floatTextures: mutable.Map[String, Texture[Double]],
  spectrumTextures: mutable.Map[String, Texture[Spectrum]]
) {

  def getSpectrumTexture(name: String, default: Spectrum): Texture[Spectrum] = {
    getTexture[Spectrum](name, default, spectrumTextures)
  }

  def getFloatTexture(name: String, default: Double): Texture[Double] = {
    getTexture[Double](name, default, floatTextures)
  }

  def getOneOr[T](name: String, default: T): T = {
    geomParams.getOneOr(name, materialParams.getOneOr(name, default))
  }

  private def getTexture[T](
    name: String,
    default: T,
    textureMap: mutable.Map[String, Texture[T]]
  ): Texture[T] = {

    val texName = getOneOr[String](name, "")

    if (texName == "") {
      val textureValue = getOneOr[T](name, default)
      new ConstantTexture[T](textureValue)

    } else {
      textureMap(texName)
    }
  }
}
