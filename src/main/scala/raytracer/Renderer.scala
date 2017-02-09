package raytracer

/**
  * Created by Basim on 15/12/2016.
  */
import java.awt.image.BufferedImage

import raytracer.cameras.{OrthographicCamera, PerspectiveCamera}
import raytracer.integrators.{Integrator, Whitted}
import raytracer.math.{Point, Ray, Transform, Vec3}

import scala.util.Random

trait RenderOpts {
  val maxRayDepth: Int = 4
  val pixelSampleCount: Int = 2

  val cameraToWorld: Transform

  val imgWidth: Int
  val imgHeight: Int

  val scene: Scene
  val integrator: Integrator = new Whitted()
}

class Renderer(options: RenderOpts) {

  def render: BufferedImage = {
    val ar = options.imgWidth.toDouble / options.imgHeight
    val cam = new PerspectiveCamera(options.cameraToWorld,
      (-ar, ar, -1, 1), options.imgWidth, options.imgHeight, 0.1, 100, 80)
    val img = new BufferedImage(options.imgWidth, options.imgHeight, BufferedImage.TYPE_INT_RGB)

    for (y <- 0 until options.imgHeight; x <- 0 until options.imgWidth) {
      val lum = options.integrator.traceRay(cam.generateRay(x, y))(options)
      img.setRGB(x, y, lum.clamp.toRGBInt)
    }
    img
  }
}
