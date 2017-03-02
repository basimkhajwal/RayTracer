package raytracer.sampling

/**
  * Created by Basim on 02/03/2017.
  */
abstract class Sampler(
  val xStart: Int, val xEnd: Int,
  val yStart: Int, val yEnd: Int,
  val samplesPerPixel: Int
) {

  def getNextSample(nOneD: Int, nTwoD: Int): Sample

  def isFinished(): Boolean
}
