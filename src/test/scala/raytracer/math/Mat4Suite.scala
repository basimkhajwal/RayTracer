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
}
