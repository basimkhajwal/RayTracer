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

}
