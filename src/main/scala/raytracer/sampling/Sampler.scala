package raytracer.sampling

import raytracer.utils.Logger

/**
  * Created by Basim on 02/03/2017.
  */
abstract class Sampler(
  val xStart: Int, val xEnd: Int,
  val yStart: Int, val yEnd: Int,
  val samplesPerPixel: Int
) {
  Logger.info.log("Sampler", s"Created sampler $xStart $xEnd $yStart $yEnd - $samplesPerPixel")

  def getSubSampler(idx: Int, count: Int): Sampler

  protected final def computeSubWindow(idx: Int, count: Int): (Int, Int, Int, Int) = {
    val dx = xEnd - xStart
    val dy = yEnd - yStart
    var nx = count
    var ny = 1
    while (nx % 2 == 0 && 2*dx*ny < dy*nx) {
      nx >>= 1
      ny <<= 1
    }
    val x = idx % nx
    val y = idx / nx
    (
      xStart + (x*dx)/nx, xStart + ((x+1)*dx)/nx,
      yStart + (y*dy)/ny, yStart + ((y+1)*dy)/ny
    )
  }

  def getNextSample(nOneD: Int, nTwoD: Int): Sample

  def isFinished(): Boolean
}
