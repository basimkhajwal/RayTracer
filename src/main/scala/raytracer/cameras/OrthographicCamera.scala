package raytracer.cameras

import raytracer.films.Film
import raytracer.math.{Point, Ray, Transform, Vec3}
import raytracer.sampling.{CameraSample, Sample}

/**
  * Created by Basim on 25/01/2017.
  */
class OrthographicCamera(
  camToWorld: Transform, screenWindow: (Double, Double, Double, Double),
  lenR: Double, fd: Double, film: Film
) extends ProjectiveCamera(camToWorld, Projection.orthographic(0, 1), screenWindow, lenR, fd, film) {

  val zDir = Vec3(0, 0, 1)

  override def generateRay(sample: CameraSample): Ray = {
    val start = rasterToCamera(Point(sample.imageX, sample.imageY, 0))
    applyDepthOfField(start, zDir, sample)
  }
}
