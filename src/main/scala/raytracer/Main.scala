package raytracer

import java.nio.file.{Files, Paths}

import raytracer.cameras.Camera
import raytracer.films.{Film, ScreenFilm}
import raytracer.integrators.{Integrator, Whitted}
import raytracer.math.{Point, Vec3}
import raytracer.parsing.{ParamSet, SceneBuilder, SceneParser}

/**
  * Created by Basim on 18/12/2016.
  */
object Main {

  val scene = new RenderOpts {

    val sceneBuilder = new SceneBuilder {
      val spacing: Double = 100000

      identityTransform()
      lookAtTransform(Point(3,0,-5), Point(0,-8,0), Vec3(0,1,0))

      film("screen", ParamSet.from("xresolution" -> List(800.0), "yresolution" -> List(600), "width" -> List(1600), "height" -> List(1200)))
      camera("perspective", ParamSet.from("fov" -> List(80)))
      identityTransform()

      worldBegin()
      material("matte", ParamSet.from("kd" -> List(Spectrum.WHITE)))

      transformBegin()
        translateTransform(-3, 5, 0)
        lightSource("point", ParamSet.from("I" -> List(Spectrum.WHITE)))
      transformEnd()

      transformBegin()
        translateTransform(0, 5, 0)
        lightSource("point", ParamSet.from("I" -> List(Spectrum.WHITE)))
      transformEnd()

      transformBegin()
        translateTransform(3, 5, 0)
        lightSource("point", ParamSet.from("I" -> List(Spectrum.WHITE)))
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

    override val camera: Camera = sceneBuilder.getCamera
    override val scene: Scene = new Scene(sceneBuilder.getLights, sceneBuilder.getPrimitives)
    override val maxRayDepth: Int = 3

    override val integrator: Integrator = new Whitted
    override val film: Film = sceneBuilder.getFilm
  }

  val fileScene = new RenderOpts {
    override val integrator: Integrator = new Whitted

    val sceneParser: SceneParser = new SceneParser("scenes/teapot.txt")
    sceneParser.parse

    override val film: Film = sceneParser.getFilm
    override val maxRayDepth: Int = 3
    override val scene: Scene = new Scene(sceneParser.getLights, sceneParser.getPrimitives)
    override val camera: Camera = sceneParser.getCamera
  }

  def main(args: Array[String]) = {
    new Renderer(fileScene).render
  }

  def findFirst(n: Int): String = {
    val fName: String = "progress/" + n + ".png"
    if (Files.exists(Paths.get(fName))) findFirst(n+1) else fName
  }
}
