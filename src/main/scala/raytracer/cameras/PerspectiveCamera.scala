package raytracer.cameras

import raytracer.films.Film
import raytracer.math.{Point, Ray, Transform, Vec3}
import raytracer.sampling.CameraSample

/**
  * Created by Basim on 25/01/2017.
  */
class PerspectiveCamera(
  camToWorld: Transform,
  screenWindow: (Double, Double, Double, Double),
  fov: Double, lenR: Double, fd: Double,
  film: Film
) extends ProjectiveCamera(
  camToWorld, Projection.perspective(fov, 1e-2f, 1000), screenWindow, lenR, fd, film) {

  override def generateRay(sample: CameraSample): Ray = {
    val pCamera = rasterToCamera(Point(sample.imageX, sample.imageY, 0))
    applyDepthOfField(Point.ZERO, (pCamera-Point.ZERO).nor, sample)
  }
}

