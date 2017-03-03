package raytracer.integrators

import raytracer.math.Ray
import raytracer.{Scene, Spectrum}

/**
  * Created by Basim on 05/01/2017.
  */
trait Integrator {
  def traceRay(scene: Scene, ray: Ray): Spectrum
}
