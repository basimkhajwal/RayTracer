package raytracer.renderers
import raytracer.{Logger, Scene}
import raytracer.cameras.Camera
import raytracer.integrators.Integrator
import raytracer.sampling.Sampler

/**
  * Created by Basim on 03/03/2017.
  */
class SamplerRenderer(
  sampler: Sampler,
  camera: Camera,
  integrator: Integrator,
  taskCount: Int
) extends Renderer {

  override def render(scene: Scene): Unit = {

    val before = System.currentTimeMillis()
    val subTasks = new Array[Thread](taskCount)
    var i = 0
    while (i < taskCount) {
      subTasks(i) = new Thread(new SamplerRendererTask(sampler, camera, integrator, scene, i, taskCount))
      subTasks(i).start()
      i += 1
    }

    i = 0
    while (i < taskCount) {
      subTasks(i).join()
      i += 1
    }

    Logger.info.log("Renderer", "Finish in " + (System.currentTimeMillis()-before) + "ms")

    camera.film.saveImage()
  }
}

class SamplerRendererTask(
  mainSampler: Sampler,
  camera: Camera,
  integrator: Integrator,
  scene: Scene,
  taskIdx: Int,
  taskCount: Int
) extends Runnable {
  val sampler = mainSampler.getSubSampler(taskIdx, taskCount)

  override def run(): Unit = {

    while (!sampler.isFinished()) {
      val sample = sampler.getNextSample(0,0)
      val ray = camera.generateRay(sample)
      val li = integrator.traceRay(scene, ray).clamp

      camera.film.applySample(sample, li)
    }
  }
}
