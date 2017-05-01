package raytracer.integrators
import raytracer.{Scene, Spectrum}
import raytracer.math.Ray

/**
  * Created by Basim on 01/05/2017.
  */
class PathIntegrator(val maxDepth: Int) extends Integrator {

  override def traceRay(scene: Scene, inRay: Ray): Spectrum = {

    var ray = inRay
    var done = false

    while (!done) {



    }

    ???
  }
}
