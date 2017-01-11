package raytracer.math

import raytracer.Constants._

/**
  * Created by Basim on 05/01/2017.
  */
case class Ray(val start: Point, val dir: Vec3) {
  private val m = dir.mag2
  assert(m > 1-EPSILON && m<1+EPSILON)

  def reflect(normal: Vec3): Vec3 = {
    val cosTheta = (-dir).dot(normal)
    dir + normal*(2*cosTheta)
  }
}
