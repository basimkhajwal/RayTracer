package raytracer.cameras

import raytracer.math.Transform

/**
  * Created by Basim on 24/01/2017.
  */
object Projection {

  def orthographic(near: Double, far: Double): Transform =
    Transform.scale(1, 1, 1/(far-near)) * Transform.translate(0,0,-near)

  def perspective(fov: Double, near: Double, far: Double): Transform = {
    ???
  }
}
