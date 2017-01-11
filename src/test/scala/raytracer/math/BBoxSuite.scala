package raytracer.math

import org.scalatest.FunSuite

import scala.util.Random

/**
  * Created by Basim on 11/01/2017.
  */
class BBoxSuite extends FunSuite {

  test ("Correctly identifies points inside bounds") {

    val bounds = BBox(Point.ZERO, Point.ONE)
    val rand = new Random

    for (i <- 0 to 20) {

      /* Test points inside the bounding box */
      assert(bounds contains Point(rand.nextDouble, rand.nextDouble, rand.nextDouble))

      /* Test points outside the bounding box */
      assert(!bounds.contains(Point(2, rand.nextDouble, rand.nextDouble)))
      assert(!bounds.contains(Point(rand.nextDouble, 2, rand.nextDouble)))
      assert(!bounds.contains(Point(rand.nextDouble, rand.nextDouble, 2)))
    }

  }
}
