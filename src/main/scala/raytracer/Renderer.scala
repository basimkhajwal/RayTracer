package raytracer

/**
  * Created by Basim on 15/12/2016.
  */
import java.awt.image.BufferedImage

import raytracer.integrators.{Integrator, Whitted}
import raytracer.math.Vec3

import scala.util.Random

trait RenderOpts {
  val maxRayDepth: Int = 4
  val pixelSampleCount: Int = 2

  val cameraPos: Vec3 = Vec3(0, 0, -2)
  val cameraAngle: Vec3 = Vec3.ZERO

  val imgWidth: Int
  val imgHeight: Int

  val scene: Scene
  val integrator: Integrator = new Whitted()
}

class Renderer(options: RenderOpts) {

  lazy val render: BufferedImage = {
    val hw = options.imgWidth / 2.0
    val hh = options.imgHeight / 2.0
    val dx = 1.0 / hw
    val dy = 1.0 / hh
    val invSampleCount = 1.0 / options.pixelSampleCount
    val img = new BufferedImage(options.imgWidth, options.imgHeight, BufferedImage.TYPE_INT_RGB)

    def randomVariation(dir: Vec3): Vec3 = dir + Vec3(Random.nextDouble()*dx, Random.nextDouble()*dy, 0)

    for (y <- 0 until options.imgHeight; x <- 0 until options.imgWidth) {
      val rayDir = Vec3((x-hw)/hh, (y-hh)/hh , 1)
      val lum = (1 to options.pixelSampleCount)
        .map(_ =>
          options.integrator.traceRay(Ray(options.cameraPos, randomVariation(rayDir).nor))(options) )
        .reduce(_ + _) * invSampleCount

      img.setRGB(x, options.imgHeight-y-1, lum.clamp.toRGBInt)
    }
    img
  }
}
