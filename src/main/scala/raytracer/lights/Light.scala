package raytracer.lights

import raytracer.Spectrum
import raytracer.math.{Point, Vec3}

/**
  * Created by Basim on 18/02/2017.
  */
trait Light {
  def sample(point: Point): (Spectrum, Vec3)
}
