package raytracer.films

import raytracer.Spectrum

/**
  * Created by Basim on 24/01/2017.
  */
abstract class Film(xRes: Int, yRes: Int) {

  val xResolution = xRes
  val yResolution = yRes

  def applySample(imageX: Int, imageY: Int, l: Spectrum)

  def saveImage: Unit
}
