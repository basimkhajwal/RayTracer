package raytracer.cameras

import raytracer.math.{Mat4, Transform}

/**
  * Created by Basim on 24/01/2017.
  */
object Projection {

  def orthographic(near: Double, far: Double): Transform =
    Transform.scale(1, 1, 1/(far-near)) * Transform.translate(0,0,-near)

  def perspective(fov: Double, near: Double, far: Double): Transform = {
    val persp = Mat4(Array(
      1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0, far / (far - near), -far*near / (far-near),
      0, 0, 1, 0
    ))

    val invTanAng = 1.0 / (math.tan(math.toRadians(fov) / 2.0))
    Transform.scale(invTanAng, invTanAng, 1) * Transform(persp)
  }
}
