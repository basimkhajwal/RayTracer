package raytracer.math

import org.scalatest.FunSuite

import scala.util.Random

/**
  * Created by Basim on 09/01/2017.
  */
class TransformSuite extends FunSuite {

  test("Identity works correctly") {
    val rand = new Random
    for (i <- 0 to 20) {
      val vec = Vec3(rand.nextInt, rand.nextInt, rand.nextInt)
      val point = Vec3(rand.nextInt, rand.nextInt, rand.nextInt)
      epsilonEquals(Transform.identity(vec), vec)
      epsilonEquals(Transform.identity(point), point)
    }
  }

  test ("Translate works correctly"){
    val start = Vec3(1, 2, 3)
    val t1 = Transform.translate(2, 3, 4)
    val t2 = Transform.translate(1, 0, -9)

    epsilonEquals(t1(start), Vec3(3, 5, 7))
    epsilonEquals(t2(start), Vec3(2, 2, -6))
  }

  test ("Scale works correctly"){
    val start = Vec3(1, 2, 3)
    val t1 = Transform.scale(2, 3, 4)
    val t2 = Transform.scale(1, 2, -0.5)

    epsilonEquals(t1(start), Vec3(2, 6, 12))
    epsilonEquals(t2(start), Vec3(1, 4, -1.5))
  }

  test ("Inverse of translate works correctly"){
    val start = Vec3(1, 2, 3)
    val t1 = Transform.translate(2, 3, 4)
    val t2 = Transform.translate(1, 0, -9)

    epsilonEquals(start, t1.inverse(t1(start)))
    epsilonEquals(start, t2.inverse(t2(start)))
  }

  test ("Inverse of scale works correctly"){
    val start = Vec3(1, 2, 3)
    val t1 = Transform.scale(2, 3, 4)
    val t2 = Transform.scale(1, 2, -0.5)

    epsilonEquals(start, t1.inverse(t1(start)))
    epsilonEquals(start, t2.inverse(t2(start)))
  }

}
