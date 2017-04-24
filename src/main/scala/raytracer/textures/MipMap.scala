package raytracer.textures

import raytracer.Spectrum
import raytracer.filters.LanczosFilter

/**
  * Created by Basim on 23/04/2017.
  */

class SpectrumMipMap(
  val width: Int, val height: Int, val data: Array[Spectrum],
  val doTrilinear: Boolean, val maxAnisotropy: Double, val wrapMode: ImageWrap
) {

  val nLevels = 1 + math.round(math.log(math.max(width, height)) / math.log(2)).toInt
  val pyramid = new Array[BlockedArray[Spectrum]](nLevels)

  init()

  def texel(level: Int, s: Int, t: Int): Spectrum = {
    val l = pyramid(level)

    var x: Int = s
    var y: Int = t

    if (wrapMode == TEXTURE_REPEAT) {
      if (x < 0) x += l.width
      if (y < 0) y += l.height
      x %= l.width
      y %= l.height
    } else if (wrapMode == TEXTURE_CLAMP) {
      x = math.max(0, math.min(x, l.width-1))
      y = math.max(0, math.min(y, l.height-1))
    } else if (x < 0 || x >= l.width || y < 0 || t >= l.height) {
      return Spectrum.BLACK
    }

    l(x, y)
  }

  def init(): Unit = {
    pyramid(0) = new BlockedArray[Spectrum](width, height, data)

    var i = 1
    while (i < nLevels) {
      val xRes = math.max(1, pyramid(i-1).width/2)
      val yRes = math.max(1, pyramid(i-1).height/2)
      pyramid(i) = new BlockedArray[Spectrum](xRes, yRes)

      var x = 0
      while (x < xRes) {
        var y = 0
        while (y < yRes) {
          pyramid(i)(x, y) =
             (texel(i-1, 2*x, 2*y)   + texel(i-1, 2*x+1, 2*y) +
              texel(i-1, 2*x, 2*y+1) + texel(i-1, 2*x+1, 2*y+1)) * 0.25

          y += 1
        }
        x += 1
      }
      i += 1
    }
  }

  def triangle(inLevel: Int, x: Double, y: Double): Spectrum = {
    val level = math.max(0, math.min(inLevel, nLevels-1))
    val s = x * pyramid(level).width - 0.5
    val t = y * pyramid(level).height - 0.5
    val s0 = s.toInt
    val t0 = t.toInt
    val ds = s - s0
    val dt = t - t0

    texel(level, s0, t0) * ((1-ds) * (1-dt)) +
    texel(level, s0, t0+1) * ((1-ds) * dt) +
    texel(level, s0+1, t0) * (ds * (1-dt)) +
    texel(level, s0+1, t0+1) * (ds * dt)
  }

  def lookUp(x: Double, y: Double, width: Double): Spectrum = {
    val level = nLevels - 1 + (math.log(math.max(width, 1e-8f)) / math.log(2))

    if (level < 0) {
      triangle(0, x, y)
    } else if (level >= nLevels - 1) {
      texel(nLevels - 1, 0, 0)
    } else {
      val iLevel = level.toInt
      val delta = level - iLevel
      triangle(iLevel, x, y)*(1-delta) + triangle(iLevel+1, x, y)*delta
    }
  }

  def lookUp(
    s: Double, t: Double,
    ds0: Double, dt0: Double, ds1: Double, dt1: Double
  ): Spectrum = {
    lookUp(s, t,
      2 * math.max(
        math.max(math.abs(ds0), math.abs(dt0)),
        math.max(math.abs(ds1), math.abs(dt1))
      )
    )
    // TODO: Write elliptically weighted averaging algorithm
  }
}

class BlockedArray[T : Manifest](val width: Int, val height: Int, val data: Array[T]) {
  def this(width: Int, height: Int) = this(width, height, new Array[T](width * height))

  def apply(x: Int, y: Int): T = data(y*width + x)
  def update(x: Int, y: Int, v: T): Unit = {
    data(y*width + x) = v
  }
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

  def createSpectrum(
    xRes: Int, yRes: Int, data: Array[Spectrum],
    dt: Boolean = false, ma: Double = 8,
    wm: ImageWrap = TEXTURE_REPEAT): SpectrumMipMap = {

    val width = nextPOT(xRes)
    val height = nextPOT(yRes)

    if (width==xRes && height==yRes) return new SpectrumMipMap(xRes, yRes, data, dt, ma, wm)

    val resampledImage = new Array[Spectrum](width * height)
    var x = 0
    var y = 0
    var j = 0

    y = 0
    val xWeights = resampleWeights(xRes, width)
    while (y < yRes) {
      x = 0
      while (x < width) {
        resampledImage(y*width+x) = Spectrum.BLACK
        j = 0
        while (j < 4) {
          var origX = xWeights(x).firstTexel + j
          if (wm == TEXTURE_REPEAT) {
            if (origX < 0) origX += xRes
            else origX %= xRes
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
    val workData = new Array[Spectrum](height)

    x = 0
    while (x < width) {
      y = 0
      while (y < height) {
        workData(y) = Spectrum.BLACK

        j = 0
        while (j < 4) {
          var offset = yWeights(y).firstTexel + j
          if (wm == TEXTURE_REPEAT) {
            if (offset < 0) offset += yRes
            else offset %= yRes
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

    new SpectrumMipMap(width, height, resampledImage, dt, ma, wm)
  }

}

sealed trait ImageWrap
object ImageWrap {
  def fromString(str: String): ImageWrap = {
    str match {
      case "repeat" => TEXTURE_REPEAT
      case "clamp" => TEXTURE_CLAMP
      case "black" => TEXTURE_BLACK
      case _ => throw new NotImplementedError("Invalid wrap type " + str)
    }
  }
}
object TEXTURE_REPEAT extends ImageWrap
object TEXTURE_BLACK extends ImageWrap
object TEXTURE_CLAMP extends ImageWrap
