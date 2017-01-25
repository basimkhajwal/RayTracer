package raytracer.cameras

import raytracer.math.{Point, Ray, Transform, Vec3}

/**
  * Created by Basim on 25/01/2017.
  */
class OrthographicCamera(
  camToWorld: Transform,
  screenWindow: (Double, Double, Double, Double),
  xRes: Int, yRes: Int,
  znear: Double, zfar: Double
) extends ProjectiveCamera(
  camToWorld,
  Projection.orthographic(znear, zfar), screenWindow, xRes, yRes) {

  val zdir = Vec3(0, 0, 1)

  override def generateRay(imageX: Double, imageY: Double): Ray = {
    //val ray = Ray(Point(imageX, imageY, 0), zdir)
    cameraToWorld(Ray(rasterToCamera(Point(imageX, imageY, 0)), zdir))
  }
}
