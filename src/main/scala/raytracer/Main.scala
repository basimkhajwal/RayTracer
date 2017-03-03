package raytracer

import java.nio.file.{Files, Paths}

import raytracer.cameras.Camera
import raytracer.films.Film
import raytracer.integrators.{Integrator, Whitted}
import raytracer.math.{Point, Vec3}
import raytracer.parsing.{ParamSet, SceneBuilder}
import raytracer.renderers.SamplerRenderer
import raytracer.sampling.{RandomSampler, Sampler}

/**
  * Created by Basim on 18/12/2016.
  */
object Main {

  val sceneBuilder = new SceneBuilder {
    val spacing: Double = 100000
    val intensity = 150

    lookAtTransform(Point(3,0,-5), Point(0,-8,0), Vec3(0,1,0))

    film("image", ParamSet.from("xresolution" -> List(800), "yresolution" -> List(600), "filename" -> List(findFirst(0)), "width" -> List(1600), "height" -> List(1200)))
    camera("perspective", ParamSet.from("fov" -> List(80)))

    worldBegin()
    material("matte", ParamSet.from("kd" -> List(Spectrum.WHITE)))

    transformBegin()
    translateTransform(-3, 5, 0)
    lightSource("point", ParamSet.from("I" -> List(Spectrum(intensity, intensity, intensity))))
    transformEnd()

    transformBegin()
    translateTransform(0, 5, 0)
    lightSource("point", ParamSet.from("I" -> List(Spectrum(intensity, intensity, intensity))))
    transformEnd()

    transformBegin()
    translateTransform(3, 5, 0)
    lightSource("point", ParamSet.from("I" -> List(Spectrum(intensity, intensity, intensity))))
    transformEnd()

    transformBegin()
    translateTransform(2.1, -6, 0)
    shape("sphere", ParamSet.from("radius" -> List(2.0), "kd" -> List(Spectrum(1,0,0))))
    transformEnd()

    transformBegin()
    translateTransform(2.1, -6-spacing-6, 0)
    shape("sphere", ParamSet.from("radius" -> List(spacing)))
    transformEnd()

    transformBegin()
    translateTransform(2.1-spacing-6, -6, 0)
    shape("sphere", ParamSet.from("radius" -> List(spacing)))
    transformEnd()

    transformBegin()
    translateTransform(2.1+spacing+6, -6, 0)
    shape("sphere", ParamSet.from("radius" -> List(spacing)))
    transformEnd()

    transformBegin()
    translateTransform(2.1, -6, spacing+6)
    shape("sphere", ParamSet.from("radius" -> List(spacing)))
    transformEnd()

    transformBegin()
    translateTransform(2.1, -6, -spacing-6)
    shape("sphere", ParamSet.from("radius" -> List(spacing)))
    transformEnd()

    worldEnd()
  }
  val camera: Camera = sceneBuilder.getCamera
  val scene: Scene = new Scene(sceneBuilder.getLights, sceneBuilder.getPrimitives)
  val integrator: Integrator = new Whitted(3)
  val film: Film = sceneBuilder.getFilm
  val sampler: Sampler = new RandomSampler(0, film.xResolution-1, 0, film.yResolution-1, 1)

  def main(args: Array[String]) = {
    new SamplerRenderer(
      sampler,
      camera,
      integrator,
      10
    ).render(scene)
  }

  def findFirst(n: Int): String = {
    val fName: String = "progress/" + n + ".png"
    if (Files.exists(Paths.get(fName))) findFirst(n+1) else fName
  }
}
