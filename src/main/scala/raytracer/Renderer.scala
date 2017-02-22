package raytracer

/**
  * Created by Basim on 15/12/2016.
  */
import java.awt.image.BufferedImage

import raytracer.cameras.{Camera, OrthographicCamera, PerspectiveCamera}
import raytracer.films.Film
import raytracer.integrators.{Integrator, Whitted}
import raytracer.math.{Point, Ray, Transform, Vec3}

trait RenderOpts {
  val maxRayDepth: Int = 1
  val pixelSampleCount: Int = 2

  val scene: Scene
  val integrator: Integrator
  val film: Film
  val camera: Camera
}

class Renderer(options: RenderOpts) {

  def render: Unit = {
    val width = options.film.xResolution
    val height = options.film.yResolution

    for (y <- 0 until height; x <- 0 until width) {
      val lum = options.integrator.traceRay(options.camera.generateRay(x, y))(options)

      options.film.applySample(x, y, lum.clamp)
    }

    options.film.saveImage
  }
}
