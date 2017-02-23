package raytracer

import org.scalameter.Gen
import org.scalameter.api.{Bench, PerformanceTest}
import org.scalameter.picklers.Pickler
import raytracer.math.Mat4

import scala.util.Random

/**
  * Created by Basim on 23/02/2017.
  */
object Benchmarks extends Bench.LocalTime {

  val rand = new Random()

  def randomMatrix: Mat4 = {
    Mat4(new Array[Double](16).map(_ => rand.nextDouble()))
  }

  def generateMatrices(n: Int): List[Mat4] = {
    if (n == 0) Nil else randomMatrix :: generateMatrices(n-1)
  }

  implicit val mat4Pickler: Pickler[Mat4] = new Pickler[Mat4] {
    override def pickle(x: Mat4): Array[Byte] = x.toString.toArray.map(_.toByte)
    override def unpickle(a: Array[Byte], from: Int): (Mat4, Int) = ???
  }

  val matrices: Gen[Mat4] = Gen.enumeration("matrixValue")(generateMatrices(50) :_*)(mat4Pickler)

  performance of "Mat4" in {

    measure method "inverse" in {
      using (matrices) in {
        mat => if (mat.det != 0) mat.inv
      }
    }
  }
}
