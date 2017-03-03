package raytracer.films

import raytracer.Spectrum

/**
  * Created by Basim on 24/01/2017.
  */
abstract class Film(val xResolution: Int, val yResolution: Int) {

  def applySample(imageX: Int, imageY: Int, l: Spectrum)

  def saveImage(): Unit
}
