package raytracer.cameras

import raytracer.films.Film
import raytracer.math.{Point, Ray, Transform, Vec3}

/**
  * Created by Basim on 25/01/2017.
  */
class OrthographicCamera(
  camToWorld: Transform,
  screenWindow: (Double, Double, Double, Double),
  film: Film
) extends ProjectiveCamera(
  camToWorld,
  Projection.orthographic(0, 1), screenWindow, film.xResolution, film.yResolution) {

  val zdir = Vec3(0, 0, 1)

  override def generateRay(imageX: Double, imageY: Double): Ray = {
    cameraToWorld(Ray(rasterToCamera(Point(imageX, imageY, 0)), zdir))
  }
}
