package raytracer

import raytracer.math.Vec3
import raytracer.sampling.SamplingTransform

import scala.math._

/**
  * Created by Basim on 02/04/2017.
  */
package object bxdf {

  @inline
  def cosTheta(w: Vec3): Double = w.z

  @inline
  def absCosTheta(w: Vec3): Double = w.z.abs

   @inline
  def sinTheta2(w: Vec3): Double = max(0, 1 - cosTheta(w)*cosTheta(w))

  @inline
  def sinTheta(w: Vec3): Double = sqrt(sinTheta2(w))

  @inline
  def clamp(x: Double, a: Double, b: Double): Double = max(a, min(b, x))

  @inline
  def cosPhi(w: Vec3): Double = {
    val sintheta = sinTheta(w)
    if (sintheta == 0) 1 else clamp(w.x / sintheta, -1, 1)
  }

  @inline
  def sinPhi(w: Vec3): Double = {
    val sintheta = sinTheta(w)
    if (sintheta == 0) 0 else clamp(w.y / sintheta, -1, 1)
  }

  @inline
  def sameHemisphere(w: Vec3, wp: Vec3): Boolean = w.z * wp.z > 0

  def cosineSampleHemisphere(u1: Double, u2: Double): Vec3 = {
    val (x, y) = SamplingTransform.concentricSampleDisk(u1, u2)
    val z = sqrt(max(0, 1 - x*x - y*y))

    Vec3(x, y, z)
  }
}
