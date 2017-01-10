package raytracer.integrators

import raytracer.math.Ray
import raytracer.{RenderOpts, Spectrum}

/**
  * Created by Basim on 05/01/2017.
  */
trait Integrator {
  def traceRay(ray: Ray)(implicit options: RenderOpts): Spectrum
}
