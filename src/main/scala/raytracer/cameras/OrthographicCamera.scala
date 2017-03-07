package raytracer.cameras

import raytracer.films.Film
import raytracer.math.{Point, Ray, Transform, Vec3}
import raytracer.sampling.{CameraSample, Sample}

/**
  * Created by Basim on 25/01/2017.
  */
class OrthographicCamera(
  camToWorld: Transform,
  screenWindow: (Double, Double, Double, Double),
  film: Film
) extends ProjectiveCamera(camToWorld, Projection.orthographic(0, 1), screenWindow, film) {

  val zdir = Vec3(0, 0, 1)

  override def generateRay(sample: CameraSample): Ray = {
    cameraToWorld(Ray(rasterToCamera(Point(sample.imageX, sample.imageY, 0)), zdir))
  }
}
