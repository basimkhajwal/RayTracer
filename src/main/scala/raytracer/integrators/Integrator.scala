package raytracer.integrators

import raytracer.{Ray, RenderOpts, Spectrum}

/**
  * Created by Basim on 05/01/2017.
  */
trait Integrator {
  def traceRay(ray: Ray)(implicit options: RenderOpts): Spectrum
}
