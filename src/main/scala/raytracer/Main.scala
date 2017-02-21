package raytracer

import java.awt.image.BufferedImage
import java.awt.{Dimension, Graphics}
import java.io.File
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO
import javax.swing.{JFrame, JPanel}

import raytracer.Constants._
import raytracer.films.{Film, ImageFilm, ScreenFilm}
import raytracer.math.{Point, Transform, Vec3}
import raytracer.parsing.{ParamSet, SceneBuilder}

/**
  * Created by Basim on 18/12/2016.
  */
object Main {

  val scene = new RenderOpts {

    val sceneBuilder = new SceneBuilder {
      val spacing: Double = 100000

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

    override val cameraToWorld: Transform = Transform.lookAt(Point(3,0,-5), Point(0,-8,0), Vec3(0,1,0)).inverse

    override val scene: Scene = new Scene(sceneBuilder.getLights, sceneBuilder.getPrimitives)
    override val pixelSampleCount: Int = 1
    override val maxRayDepth: Int = 3

    override val film: Film = new ScreenFilm(
      1600,
      1200,
      800,
      600
    )
  }

  def main(args: Array[String]) = {
    new Renderer(scene).render
  }

  def findFirst(n: Int): String = {
    val fName: String = "progress/" + n + ".png"
    if (Files.exists(Paths.get(fName))) findFirst(n+1) else fName
  }
}
