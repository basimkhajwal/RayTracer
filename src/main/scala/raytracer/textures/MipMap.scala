package raytracer.textures

import raytracer.Spectrum
import raytracer.filters.LanczosFilter

/**
  * Created by Basim on 23/04/2017.
  */
/*
*  Steps:
*   1. Re-sample image so dimensions are to next highest POT
*   2. Build MipMap pyramid
*   3. Write code for the trilinear filtering algorithm
*   4. Write elliptically weighted averaging algorithm
* */

class MipMap[T : Manifest](
  val xRes: Int, val yRes: Int, val data: Array[T],
  val doTrilinear: Boolean, val maxAnisotropy: Double, val wrapMode: ImageWrap
)(implicit evidence: T => MipMappable[T]) {


}

trait MipMappable[T] {
  def +(that: T): T
  def *(that: T): T
  def *(that: Double): T
  def zero(): T
  def clamp(): T
}

object MipMap {

  def nextPOT(n: Int): Int = {
    var v = n-1
    v |= v >> 1
    v |= v >> 2
    v |= v >> 4
    v |= v >> 8
    v |= v >> 16
    v+1
  }

  implicit def doubleToMipMap(x: Double) = new MipMappable[Double] {
    override def +(that: Double): Double = x+that
    override def *(that: Double): Double = x*that
    override def zero(): Double = 0
    def clamp(): Double = math.max(0, x)
  }

  implicit def spectrumToMipMap(s: Spectrum) = new MipMappable[Spectrum] {
    override def +(that: Spectrum): Spectrum = s+that
    override def *(that: Double): Spectrum = s*that
    override def *(that: Spectrum): Spectrum = s*that
    override def zero(): Spectrum = Spectrum.BLACK
    override def clamp(): Spectrum = s.clamp(Double.PositiveInfinity)
  }

  case class ResampleWeight(firstTexel: Int, weight: Array[Double])

  def resampleWeights(oldres: Int, newres: Int): Array[ResampleWeight] = {
    val wt = new Array[ResampleWeight](newres)
    val filterWidth = 2.0

    var i = 0
    while (i < newres) {
      val center = (i + 0.5) * oldres / newres
      val firstTexel = ((center - filterWidth) + 0.5).toInt
      val weight = new Array[Double](4)

      var j = 0
      while (j < 4) {
        val pos = firstTexel + j + 0.5
        weight(j) = LanczosFilter.lanczos((pos - center) / filterWidth)
        j += 1
      }

      val invSumWts = 1.0 / (weight(0) + weight(1) + weight(2) + weight(3))
      j = 0
      while (j < 4) {
        weight(j) *= invSumWts
        j += 1
      }

      wt(i) = ResampleWeight(firstTexel, weight)
      i += 1
    }

    wt
  }

  def create[T : Manifest](
    xRes: Int, yRes: Int, data: Array[T],
    dt: Boolean = false, ma: Double = 8,
    wm: ImageWrap = TEXTURE_REPEAT)(implicit ev: T => MipMappable[T]): MipMap[T] = {

    val width = nextPOT(xRes)
    val height = nextPOT(yRes)

    if (width==xRes && height==yRes) return new MipMap[T](xRes, yRes, data, dt, ma, wm)

    val resampledImage = new Array[T](width * height)
    val zero = data.head.zero()
    var x = 0
    var y = 0
    var j = 0

    y = 0
    val xWeights = resampleWeights(xRes, width)
    while (y < yRes) {
      x = 0
      while (x < width) {
        resampledImage(y*width+x) = zero
        j = 0
        while (j < 4) {
          var origX = xWeights(x).firstTexel + j
          if (wm == TEXTURE_REPEAT) {
            origX %= xRes
          } else if (wm == TEXTURE_CLAMP) {
            origX = math.max(0, math.min(origX, xRes - 1))
          }

          if (origX >= 0 && origX < xRes) {
            resampledImage(y*width+x) =
              resampledImage(y*width+x) + (data(y*xRes+origX) * xWeights(x).weight(j))
          }
          j += 1
        }
        x += 1
      }
      y += 1
    }

    val yWeights = resampleWeights(yRes, height)
    val workData = new Array[T](height)

    x = 0
    while (x < width) {
      y = 0
      while (y < height) {
        workData(y) = zero

        j = 0
        while (j < 4) {
          var offset = yWeights(y).firstTexel + j
          if (wm == TEXTURE_REPEAT) {
            offset %= yRes
          } else if (wm == TEXTURE_CLAMP) {
            offset = math.max(0, math.min(offset, yRes-1))
          }

          if (offset >= 0 && offset < yRes) {
            workData(y) =
              workData(y) + (resampledImage(offset*width + x) * yWeights(y).weight(j))
          }

          j += 1
        }
        y += 1
      }

      y = 0
      while (y < height) {
        resampledImage(y*width + x) = workData(y).clamp()
        y += 1
      }
      x += 1
    }

    new MipMap[T](width, height, resampledImage, dt, ma, wm)
  }

}

sealed trait ImageWrap
object TEXTURE_REPEAT extends ImageWrap
object TEXTURE_BLACK extends ImageWrap
object TEXTURE_CLAMP extends ImageWrap
