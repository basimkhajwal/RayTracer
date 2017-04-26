package raytracer.films

import java.awt.image.BufferedImage

import raytracer.Spectrum
import raytracer.filters.Filter
import raytracer.sampling.CameraSample

/**
  * Created by Basim on 24/01/2017.
  */
abstract class Film(
  val filter: Filter, val xResolution: Int, val yResolution: Int, val cropWindow: (Double, Double, Double, Double)) {

  val xStart = math.ceil(xResolution * cropWindow._1).toInt
  val xCount = math.max(1, math.ceil(xResolution * cropWindow._2 - xStart)).toInt
  val yStart = math.ceil(yResolution * cropWindow._3).toInt
  val yCount = math.max(1, math.ceil(yResolution * cropWindow._4 - yStart)).toInt

  class Pixel {
    var r: Double = 0
    var g: Double = 0
    var b: Double = 0
    var weightSum: Double = 0

    def add(spectrum: Spectrum, weight: Double): Unit = {
      r += spectrum.r * weight
      g += spectrum.g * weight
      b += spectrum.b * weight
      weightSum += weight
    }

    def getSpectrum(): Spectrum = {
      if (weightSum == 0) Spectrum.BLACK
      else Spectrum(r / weightSum, g / weightSum, b / weightSum).clamp()
    }
  }

  protected val imageData: Array[Array[Pixel]] =
    new Array[Array[Pixel]](xCount) map (_ => new Array[Pixel](yCount) map (_ => new Pixel()))

  val sampleExtent =
    ((xStart + 0.5 - filter.xWidth).toInt, math.ceil(xStart+xCount-0.5+filter.xWidth).toInt,
     (yStart + 0.5 - filter.yWidth).toInt, math.ceil(yStart+yCount-0.5+filter.yWidth).toInt)

  final def applySample(sample: CameraSample, l: Spectrum): Unit = {

    if (l.isBlack(1e-9)) return

    val imageX = sample.imageX - 0.5
    val imageY = sample.imageY - 0.5
    val x0 = math.max(xStart, math.ceil(imageX - filter.xWidth).toInt)
    val y0 = math.max(yStart, math.ceil(imageY - filter.yWidth).toInt)

    val x1 = math.min(xStart + xCount - 1, (imageX + filter.xWidth).toInt)
    val y1 = math.min(yStart + yCount - 1, (imageY + filter.yWidth).toInt)

    var x = x0

    while (x <= x1) {
      var y = y0
      while (y <= y1) {
        imageData(x)(y).add(l, filter.evaluate(x - imageX, y - imageY))
        y += 1
      }
      x += 1
    }
  }

  def getBufferedImage(): BufferedImage = {
    val image = new BufferedImage(xResolution, yResolution, BufferedImage.TYPE_INT_RGB)

    var x = 0
    while (x < xCount) {
      var y = 0
      while (y < yCount) {
        image.setRGB(x, y, imageData(x)(y).getSpectrum().toRGBInt)
        y += 1
      }
      x += 1
    }

    image
  }

  def saveImage(): Unit
}
