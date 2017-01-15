package raytracer.math

import org.scalatest.FunSuite

/**
  * Created by Basim on 15/01/2017.
  */
class Vec3Suite extends FunSuite {

  test("Cross product works correctly") {
    epsilonEquals(Vec3(0,1,0) cross Vec3(1,0,0), Vec3(0, 0, -1))
    epsilonEquals(Vec3(1,0,0) cross Vec3(0,1,0), Vec3(0, 0, 1))
    epsilonEquals(Vec3(1,1,1) cross Vec3(1,2,3), Vec3(1, -2, 1))
    epsilonEquals(Vec3(1,1,1) cross Vec3(1,2,3), Vec3(1, -2, 1))
    epsilonEquals(Vec3(-3.5,-2,0.1) cross Vec3(1.1,9.2,3), Vec3(-6.92, 10.61, -30))
  }

}
