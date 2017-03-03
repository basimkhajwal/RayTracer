package raytracer.renderers
import raytracer.Scene
import raytracer.cameras.Camera
import raytracer.integrators.Integrator
import raytracer.sampling.Sampler

/**
  * Created by Basim on 03/03/2017.
  */
class SamplerRenderer(
  val sampler: Sampler,
  val camera: Camera,
  val integrator: Integrator
) extends Renderer {

  override def render(scene: Scene): Unit = ???
}
