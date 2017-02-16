package raytracer

import java.awt.image.BufferedImage
import java.awt.{Dimension, Graphics}
import java.io.File
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO
import javax.swing.{JFrame, JPanel}

import raytracer.Constants._
import raytracer.integrators.Integrator
import raytracer.math.{Point, Transform, Vec3}
import raytracer.parsing.{ParamSet, SceneBuilder}
import raytracer.primitives.Primitive
import raytracer.shapes.{Shape, Sphere, Triangle}

/**
  * Created by Basim on 18/12/2016.
  */
object Main {

  val scene1 = new RenderOpts {
    val lights =
      PointLight(Point(0, 0.5, -0.5), Spectrum.WHITE*0.6) ::
      PointLight(Point(-1, -0.5, 1), Spectrum.WHITE * 0.6) ::
      Nil

    val bigWidth: Double = 1e5
    val room:Double = 5
    val spheres = Nil
      /*Sphere(1, Point(3, 0, 4), Spectrum(0.8, 0.8, 0.8)) ::
      //Sphere(0.5, Point(1, 0, 2), Spectrum.WHITE) ::
      Triangle(Point(-1, 0, 2), Point(0, 0, 2), Point(-1, 1, 2), Spectrum(0.5, 0.3, 0.9)) ::
      Triangle(Point(0, 1, 2), Point(0, 0, 2), Point(-1, 1, 2), Spectrum(0.3, 0.9, 0.5)) ::
      Sphere(bigWidth, Point(-room-bigWidth, 0, 0), Spectrum(0.2, 0.3, 0.8)) :: // L
      Sphere(bigWidth, Point(room+bigWidth, 0, 0), Spectrum(0.2, 0.8, 0.3)) :: // R
      Sphere(bigWidth, Point(0, 0, room+bigWidth), Spectrum(0.7, 0.2, 0.3)) :: // F
      Sphere(bigWidth, Point(0, 0, -room-bigWidth), Spectrum.WHITE) :: // B
      Sphere(bigWidth, Point(0, room+bigWidth, 0), Spectrum(0.4, 0.4, 0.4)) :: // U
      Sphere(bigWidth, Point(0, -room-bigWidth, 0), Spectrum.WHITE * 0.2) :: // D
      Nil*/

    override val imgWidth: Int = 400
    override val imgHeight: Int = 300
    override val scene: Scene = new Scene(lights, spheres)
    override val pixelSampleCount: Int = 2
    override val maxRayDepth: Int = 3

    override val cameraToWorld: Transform = Transform.translate(0,0,-2)
  }

  val scene2 = new RenderOpts {

    val lights: List[PointLight] =
      PointLight(Point(-3, 5, 0), Spectrum.WHITE) ::
      PointLight(Point(0, 5, 0), Spectrum.WHITE) ::
      PointLight(Point(3, 5, 0), Spectrum.WHITE) ::
      Nil

    val bigWidth: Double = 1e5
    val hroom:Double = 100000
    val vroom:Double = 8
    val shapes = new SceneBuilder {
      worldBegin()
      material("matte", ParamSet.from("kd" -> List(Spectrum.WHITE)))

      transformBegin()
        translateTransform(2.1, -6, 0)
        shape("sphere", ParamSet.from("radius" -> List(2.0), "kd" -> List(Spectrum(1,0,0))))
      transformEnd()

      transformBegin()
        translateTransform(2.1, -6-hroom-6, 0)
        shape("sphere", ParamSet.from("radius" -> List(hroom)))
      transformEnd()

      transformBegin()
        translateTransform(2.1-hroom-6, -6, 0)
        shape("sphere", ParamSet.from("radius" -> List(hroom)))
      transformEnd()

      transformBegin()
        translateTransform(2.1+hroom+6, -6, 0)
        shape("sphere", ParamSet.from("radius" -> List(hroom)))
      transformEnd()

      transformBegin()
        translateTransform(2.1, -6, hroom+6)
        shape("sphere", ParamSet.from("radius" -> List(hroom)))
      transformEnd()

      transformBegin()
        translateTransform(2.1, -6, -hroom-6)
        shape("sphere", ParamSet.from("radius" -> List(hroom)))
      transformEnd()

      worldEnd()
    }.getPrimitives

    override val cameraToWorld: Transform = Transform.lookAt(Point(3,0,-5), Point(0,-8,0), Vec3(0,1,0)).inverse

    override val imgWidth: Int = 800
    override val imgHeight: Int = 600
    override val scene: Scene = new Scene(lights, shapes)
    override val pixelSampleCount: Int = 2
    override val maxRayDepth: Int = 3
  }

  def main(args: Array[String]) = {
    Logger.info.log("Running the program")
    draw
  }

  def render: BufferedImage = new Renderer(scene2).render

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
