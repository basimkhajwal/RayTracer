package raytracer.math

import org.scalatest.FunSuite

/**
  * Created by Basim on 09/01/2017.
  */
class TransformSuite extends FunSuite {

  test ("Translate works correctly"){

    val start = Vec3(1, 2, 3)
    val transform = Transform.translate(2, 3, 4)

    epsilonEquals(transform(start), Vec3(3, 5, 7))
  }
}
