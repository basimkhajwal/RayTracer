package raytracer

import org.scalatest.Assertions._

/**
  * Created by Basim on 09/01/2017.
  */
package object math {

  val EPSILON = 0.0001
  def epsilonEquals(a: Double, b: Double): Unit = {
    assert(a > b-EPSILON)
    assert(a < b+EPSILON)
  }
  def epsilonEquals(a: Vec3, b: Vec3): Unit = {
    for (i <- 0 to 2) epsilonEquals(a(i), b(i))
  }
}
