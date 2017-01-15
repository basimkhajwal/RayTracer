package raytracer

import org.scalatest.Assertions._

/**
  * Created by Basim on 09/01/2017.
  */
package object math {

  val EPSILON = 0.00001

  private def epEq(a: Double, b: Double): Boolean = a > b-EPSILON && a < b+EPSILON

  def epsilonEquals(a: Double, b: Double): Unit = {
    assert(epEq(a, b), s"$a and $b are not equal")
  }
  def epsilonEquals(a: Vec3, b: Vec3): Unit = {
    assert((0 to 2).map(i => epEq(a(i), b(i))).reduce(_ && _), s"$a and $b are not equal")
  }
  def epsilonEquals(a: Point, b: Point): Unit = {
    assert((0 to 2).map(i => epEq(a(i), b(i))).reduce(_ && _), s"$a and $b are not equal")
  }
}
