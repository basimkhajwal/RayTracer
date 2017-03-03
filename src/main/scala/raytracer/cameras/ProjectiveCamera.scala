package raytracer.cameras

import raytracer.films.Film
import raytracer.math.Transform

/**
  * Created by Basim on 24/01/2017.
  */
abstract class ProjectiveCamera(
  val cameraToWorld: Transform, val cameraToScreen: Transform,
  val screenWindow: (Double, Double, Double, Double),
  film: Film
  ) extends Camera(film) {

  val screenToRaster: Transform =
    Transform.scale(film.xResolution, film.yResolution, 1) *
    Transform.scale(
      1.0 / (screenWindow._2 - screenWindow._1),
      1.0 / (screenWindow._3 - screenWindow._4), 1) *
    Transform.translate(-screenWindow._1, -screenWindow._4, 0)

  val rasterToScreen: Transform = screenToRaster.inverse
  val rasterToCamera: Transform = cameraToScreen.inverse * rasterToScreen
  val rasterToWorld: Transform = cameraToWorld * rasterToCamera
}
