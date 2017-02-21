package raytracer

/**
  * Created by Basim on 15/12/2016.
  */
import java.awt.image.BufferedImage

import raytracer.cameras.{OrthographicCamera, PerspectiveCamera}
import raytracer.films.Film
import raytracer.integrators.{Integrator, Whitted}
import raytracer.math.{Point, Ray, Transform, Vec3}

trait RenderOpts {
  val maxRayDepth: Int = 4
  val pixelSampleCount: Int = 2

  val cameraToWorld: Transform

  val scene: Scene
  val integrator: Integrator = new Whitted()

  val film: Film
}

class Renderer(options: RenderOpts) {

  def render: Unit = {
    val width = options.film.xResolution
    val height = options.film.yResolution
    val ar = width.toDouble / height
    val cam = new PerspectiveCamera(options.cameraToWorld,
      (-ar, ar, -1, 1), width, height, 0.1, 100, 80)

    for (y <- 0 until height; x <- 0 until width) {
      val lum = options.integrator.traceRay(cam.generateRay(x, y))(options)

      options.film.applySample(x, y, lum.clamp)
    }

    options.film.saveImage
  }
}
