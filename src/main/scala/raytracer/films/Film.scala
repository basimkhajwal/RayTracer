package raytracer.films

import java.awt.image.BufferedImage

import raytracer.Spectrum

/**
  * Created by Basim on 24/01/2017.
  */
trait Film {

  val xResolution: Int
  val yResolution: Int

  def applySample(imageX: Int, imageY: Int, l: Spectrum)

  def getResult: BufferedImage
}
