package raytracer.math

import raytracer.Constants._

/**
  * Created by Basim on 05/01/2017.
  */
case class Ray(start: Point, dir: Vec3, depth: Int) {
  private val m = dir.mag2
  require(m > 1-EPSILON && m<1+EPSILON, s"Found $m with $start + $dir")
}
