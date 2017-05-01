package raytracer.sampling

import scala.util.Random

/**
  * Created by Basim on 01/05/2017.
  */
class StratifiedSampler(
  xs: Int, xe: Int, ys: Int, ye: Int,
  val xSamples: Int, val ySamples: Int,
  val jitterSamples: Boolean
)extends Sampler(xs, xe, ys, ye, xSamples*ySamples) {

  override def getSubSampler(idx: Int, count: Int): Sampler = {
    val (nxs, nxe, nys, nye) = computeSubWindow(idx, count)
    new StratifiedSampler(nxs, nxe, nys, nye, xSamples, ySamples, jitterSamples)
  }

  var samplePos = 0
  var xPos = xStart
  var yPos = yStart
  val rand = new Random()

  val imageSamples = new Array[Double](2 * samplesPerPixel)
  val lensSamples = new Array[Double](2 * samplesPerPixel)

  override def isFinished(): Boolean = {
    xPos == xEnd-1 && yPos == yEnd-1 && samplePos == samplesPerPixel
  }

  override def getNextSample(nOneD: Int, nTwoD: Int): Sample = {

    if (samplePos == samplesPerPixel) {
      samplePos = 0
      xPos += 1

      if (xPos == xEnd) {
        xPos = xStart
        yPos += 1
      }

      stratifiedSample2D(imageSamples)
      stratifiedSample2D(lensSamples)
    }

    // TODO - Implement latin hypercube sampling for 1D and 2D sampling (assumed 0 for now)

    samplePos += 1
    Sample(
      imageSamples(samplePos*2), imageSamples(samplePos*2+1),
      lensSamples(samplePos*2), lensSamples(samplePos*2+1),
      Array.emptyDoubleArray, Array.emptyDoubleArray
    )
  }

  val OneMinusEpsilon = 1 - 1e-10

  def stratifiedSample1D(sample: Array[Double]): Unit = {
    val invTot = 1.0 / samplesPerPixel
    var i = 0
    while (i < samplesPerPixel) {
      val delta = if (jitterSamples) rand.nextDouble() else 0.5
      sample(i) = math.min((i + delta) * invTot, OneMinusEpsilon);
      i += 1
    }
  }

  def stratifiedSample2D(sample: Array[Double]): Unit = {
    val dx = 1.0 / xSamples
    val dy = 1.0 / ySamples

    var y = 0
    while (y < ySamples) {

      var x = 0
      while (x < xSamples) {
        val jx = if (jitterSamples) rand.nextDouble() else 0.5
        val jy = if (jitterSamples) rand.nextDouble() else 0.5
        sample((y*xSamples+x)*2) = math.min((x + jx) * dx, OneMinusEpsilon)
        sample((y*xSamples+x)*2+1) = math.min((y + jy) * dy, OneMinusEpsilon)
        x += 1
      }

      y += 1
    }
  }
}
