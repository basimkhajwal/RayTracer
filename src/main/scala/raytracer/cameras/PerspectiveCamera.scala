package raytracer.cameras

import raytracer.films.Film
import raytracer.math.{Point, Ray, Transform, Vec3}

/**
  * Created by Basim on 25/01/2017.
  */
class PerspectiveCamera(
  camToWorld: Transform,
  screenWindow: (Double, Double, Double, Double),
  fov: Double, film: Film
) extends ProjectiveCamera(
  camToWorld,
  Projection.perspective(fov, 1e-2f, 1000), screenWindow, film.xResolution, film.yResolution) {

  val zdir = Vec3(0, 0, 1)

  override def generateRay(imageX: Double, imageY: Double): Ray = {
    val pCamera = rasterToCamera(Point(imageX, imageY, 0))
    cameraToWorld(Ray(Point.ZERO, (pCamera-Point.ZERO).nor))
  }
}

