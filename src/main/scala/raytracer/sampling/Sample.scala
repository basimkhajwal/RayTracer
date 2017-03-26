package raytracer.sampling

/**
  * Created by Basim on 02/03/2017.
  */
class Sample(
 iX: Double, iY: Double, lX: Double, lY: Double,
 val oneD: Array[Double], val twoD: Array[Double]
) extends CameraSample(iX, iY, lX, lY)

object Sample {
  def apply(iX: Double, iY: Double, lX: Double, lY: Double,
    oneD: Array[Double], twoD: Array[Double]
  ): Sample = new Sample(iX, iY, lX, lY, oneD, twoD)
}

