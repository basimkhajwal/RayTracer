package raytracer.parsing

import raytracer.textures.{ConstantTexture, Texture}
import raytracer.{ConstantTexture, Spectrum}

import scala.collection.mutable
import scala.reflect.ClassTag

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

  def getOne[T : ClassTag](name: String): Option[T] = {
    geomParams.getOne[T](name).orElse(materialParams.getOne[T](name))
  }

  def getOneOr[T : ClassTag](name: String, default: T): T = {
    geomParams.getOneOr[T](name, materialParams.getOneOr[T](name, default))
  }

  private def getTexture[T : ClassTag](
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
