package raytracer.cameras

import raytracer.films.Film
import raytracer.math.{Point, Ray, Transform, Vec3}
import raytracer.sampling.{CameraSample, Sample, SamplingTransform}

/**
  * Created by Basim on 24/01/2017.
  */
abstract class ProjectiveCamera(
  val cameraToWorld: Transform, val cameraToScreen: Transform,
  val screenWindow: (Double, Double, Double, Double),
  val lensRadius: Double,
  val focalDistance: Double,
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

  def applyDepthOfField(start: Point, dir: Vec3, sample: CameraSample): Ray = {

    if (lensRadius == 0) return cameraToWorld(Ray(start, dir, 0))

    val focalTime = focalDistance / dir.z
    val focalPoint = start + dir * focalTime

    val (lensU, lensV) = SamplingTransform.concentricSampleDisk(sample.lensU, sample.lensV)
    val lensStart = Point(lensU * lensRadius, lensV * lensRadius, 0)

    cameraToWorld(
      Ray(lensStart, (focalPoint - lensStart).nor, 0)
    )
  }
}
