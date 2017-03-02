package raytracer.sampling

/**
  * Created by Basim on 02/03/2017.
  */
case class Sample(
 iX: Double, iY: Double,
 oneD: Array[Double], twoD: Array[Double]
) extends CameraSample(iX, iY)

