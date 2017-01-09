package raytracer.math

import org.scalatest.FunSuite

/**
  * Created by Basim on 09/01/2017.
  */
class Mat4Suite extends FunSuite {

  test("Determinant of 0 matrix is 0") {
    val mat = new Mat4((0 to 15).map(_ => 0.0).toArray)
    assert(mat.det == 0)
  }

  test("Determinant of another matrix is 0") {
    val mat = new Mat4((1 to 16).map(_.toDouble).toArray)
    assert(mat.det == 0)
  }

  test("Determinant of another matrix is correct") {
    val mat = new Mat4(List(4, 2, 2, 4, 2, 7, 6, 5, 3, 8, 9, 19, 88, 6, 12, 6).map(_.toDouble).toArray)
    assert(mat.det == -3048)
  }

  test ("Inverse correctly computed") {
    val epsilon = 0.0001
    def epsilonEquals(a: Double, b: Double): Unit = {
      assert(a > b-epsilon)
      assert(a < b+epsilon)
    }

    val mat = new Mat4(List(4, 2, 2, 4, 2, 7, 6, 5, 3, 8, 9, 19, 88, 6, 12, 6).map(_.toDouble).toArray)
    val expected = Array(0.2007874, -0.0157480, -0.0393700, 0.003937, 1.9015748, 0.1351706, -0.41207349, -0.07545931, -2.6299212, 0.098425, 0.496062, 0.1003937, 0.4133858, -0.1010498, -0.00262467, -0.016404)
    val matInv = mat.inv.data

    matInv.zip(expected) foreach (v => epsilonEquals(v._1, v._2))
  }
}
