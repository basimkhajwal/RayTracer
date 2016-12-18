/**
  * Created by Basim on 15/12/2016.
  */
import java.awt.{Dimension, Graphics}
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO
import javax.swing.{JFrame, JPanel}

object Renderer {

  val lights = PointLight(Vec3(0, 0.5, -0.5), Spectrum.WHITE*0.6) ::
    PointLight(Vec3(-1, -0.5, 1), Spectrum.WHITE * 0.6) ::
    Nil

  val bigWidth: Double = 1e5
  val room:Double = 5
  val spheres =
    Sphere(1, Vec3(3, 0, 4), Spectrum(0.8, 0.8, 0.8)) ::
      //Sphere(0.5, Vec3(1, 0, 0)) ::
      Sphere(bigWidth, Vec3(-room-bigWidth, 0, 0), Spectrum(0.2, 0.3, 0.8)) :: // L
      Sphere(bigWidth, Vec3(room+bigWidth, 0, 0), Spectrum(0.2, 0.8, 0.3)) :: // R
      Sphere(bigWidth, Vec3(0, 0, room+bigWidth), Spectrum(0.7, 0.2, 0.3)) :: // F
      Sphere(bigWidth, Vec3(0, 0, -room-bigWidth), Spectrum.WHITE) :: // B
      Sphere(bigWidth, Vec3(0, room+bigWidth, 0), Spectrum(0.4, 0.4, 0.4)) :: // U
      Sphere(bigWidth, Vec3(0, -room-bigWidth, 0), Spectrum.WHITE * 0.2) :: // D
      Nil

  val scene = new Scene(lights, spheres)

  def traceRay(ray: Ray, depth: Int): Spectrum = {
    scene intersect ray match {
      case Miss => Spectrum.BLACK
      case Hit(_, p, n, c) => {

        val directLight = lights.filter(l => {
          val lightT = l.pos.dist(p)
          scene intersect Ray(p, (l.pos - p).nor) match {
            case Miss => true
            case Hit(newT, _, _, _) => newT > lightT
          }
        }).map(l => l.colour * (l.pos - p).nor.dot(n)).sum

        if (depth == 4) c * directLight
        else c * (directLight + 0.2 * traceRay(Ray(p, ray reflect n), depth + 1))
      }
    }
  }

  def render: BufferedImage = {
    val sz:Int = 600
    val hsz: Double = sz/2.0
    val img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_RGB)

    val start = Vec3(0, 0, -2) //'(-2, 0, -1)

    for (y <- 0 until sz; x <- 0 until sz) {
      val rayDir = Vec3((x-hsz)/hsz, (y-hsz)/hsz, 1).nor
      val lum = traceRay(Ray(start, rayDir), 0).clamp

      img.setRGB(x, sz-y-1, lum.toRGBInt)
    }

    img
  }

  def main(args: Array[String]) = draw

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

    override def getPreferredSize: Dimension = new Dimension(img.getWidth, img.getHeight)

    override def paint(g: Graphics): Unit = {
      super.paint(g)
      g.drawImage(img, 0, 0, img.getWidth, img.getHeight, null)
    }
  }
}
