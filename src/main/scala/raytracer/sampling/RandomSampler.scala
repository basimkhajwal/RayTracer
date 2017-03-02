package raytracer.sampling

import scala.util.Random

/**
  * Created by Basim on 02/03/2017.
  */
class RandomSampler(
  xs: Int, xe: Int, ys: Int, ye: Int, ns: Int
) extends Sampler(xs, xe, ys, ye, ns) {

  var samplePos = 0
  var xPos = xStart
  var yPos = yStart
  val rand = new Random()

  override def isFinished(): Boolean = {
    xPos == xe && yPos == yEnd && samplePos == samplesPerPixel
  }

  override def getNextSample(nOneD: Int, nTwoD: Int): Sample = {
    if (samplePos == samplesPerPixel) {
      samplePos = 0
      xPos += 1

      if (xPos == xEnd) {
        xPos = xStart
        yPos += 1
      }
    }

    val imageX: Double = xPos + rand.nextDouble()
    val imageY: Double = yPos + rand.nextDouble()
    val oneD = if (nOneD == 0) Array.emptyDoubleArray else new Array[Double](nOneD)
    val twoD = if (nTwoD == 0) Array.emptyDoubleArray else new Array[Double](nOneD)

    var i = 0
    while (i < nOneD || i < nTwoD) {
      if (i < nOneD) oneD(i) = rand.nextDouble()
      if (i < nTwoD) twoD(i) = rand.nextDouble()
      i += 1
    }

    samplePos += 1
    Sample(imageX, imageY, oneD, twoD)
  }
}
