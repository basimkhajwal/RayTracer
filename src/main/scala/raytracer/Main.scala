package raytracer

import java.awt.image.BufferedImage
import java.awt.{Dimension, Graphics}
import java.io.File
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO
import javax.swing.{JFrame, JPanel}

import raytracer.Constants._
import raytracer.math.{Point, Transform, Vec3}
import raytracer.parsing.{ParamSet, SceneBuilder}

import raytracer.lights._

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

    override val imgWidth: Int = 800
    override val imgHeight: Int = 600
    override val scene: Scene = new Scene(sceneBuilder.getLights, sceneBuilder.getPrimitives)
    override val pixelSampleCount: Int = 1
    override val maxRayDepth: Int = 3
  }

  def main(args: Array[String]) = {
    draw
  }

  def render: BufferedImage = new Renderer(scene).render

  def bench: Unit = {
    val before = System.currentTimeMillis
    render
    println("Time taken: " + (System.currentTimeMillis - before))
  }

  def draw: Unit = {
    val frame = new JFrame("Sphere Render Test")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setResizable(false)
    frame.add(new CustomRenderer(render))
    frame.pack
    frame.setVisible(true)
  }

  def save: Unit = {
    def innerFile(n: Int) = "progress/" + n + ".png"
    def exists(n: Int) = Files.exists(Paths.get(innerFile(n)))
    def saveImage(n: Int, img: BufferedImage) = ImageIO.write(img, "png", new File(innerFile(n)))

    def findFirst(n: Int): Unit = if (exists(n)) findFirst(n+1) else saveImage(n, render)
    findFirst(0)
  }

  class CustomRenderer(val img: BufferedImage) extends JPanel {

    override def getPreferredSize: Dimension = viewportSize

    override def paint(g: Graphics): Unit = {
      super.paint(g)
      g.drawImage(img, 0, 0, viewportSize.width, viewportSize.height, null)
    }
  }
}
