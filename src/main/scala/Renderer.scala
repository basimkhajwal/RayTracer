/**
  * Created by Basim on 15/12/2016.
  */
import java.awt.{Dimension, Graphics}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{JFrame, JPanel}

object Renderer {

  def renderSphere: BufferedImage = {
    val sz:Int = 1000
    val hsz: Double = sz/2.0
    val img = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_RGB)

    val start = Vec3(0, 0, -3)

    for (y <- 0 until sz; x <- 0 until sz) {
      val rayDir = Vec3((x-hsz)/hsz, (y-hsz)/hsz, 1).nor
      val lum = traceRay(Ray(start, rayDir), 0).clamp

      img.setRGB(x, y, lum.toRGBInt)
    }

    img
  }

  val lights = PointLight(Vec3(-1, 1, -10), Spectrum(1, 0.7, 0.7)) :: Nil

  def traceRay(ray: Ray, depth: Int): Spectrum = {
    if (depth >= 3) Spectrum.BLACK
    else
      intersect(ray) match {
        case Miss => Spectrum.BLACK
        case Hit(t, p, n) => {
          lights.filter(l => {
            val lightRay = Ray(p, (l.pos - p).nor)
            val lightT = l.pos.dist(p)

            intersect(lightRay) match {
              case Miss => true
              case Hit(newT, _, _) => newT > lightT
            }
          }).foldLeft(Spectrum.BLACK)((spect: Spectrum, l) => spect + l.colour * (l.pos - p).nor.dot(n))
        }
      }
  }

  val spheres = Sphere(1.5, Vec3.ZERO) :: Nil

  def intersect(ray: Ray): Intersection = {

    val intersections = spheres
      .map(s => s.intersect(ray))
      .filterNot(_ == Miss)

    if (intersections.isEmpty) Miss
    else intersections.minBy { case Hit(t, _, _) => t }
  }

  def saveImage(fileName: String, img: BufferedImage): Unit = {
    ImageIO.write(img, "png", new File(fileName))
  }

  def main(args: Array[String]): Unit = {
    draw
  }

  def bench: Unit = {
    val before = System.currentTimeMillis
    renderSphere
    println("Time taken: " + (System.currentTimeMillis - before))
  }

  def draw: Unit = {
    val frame = new JFrame("Sphere Render Test")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setResizable(false)
    frame.add(new CustomRenderer(renderSphere))
    frame.pack
    frame.setVisible(true)
  }

  class CustomRenderer(val img: BufferedImage) extends JPanel {

    override def getPreferredSize: Dimension = new Dimension(img.getWidth, img.getHeight)

    override def paint(g: Graphics): Unit = {
      super.paint(g)
      g.drawImage(img, 0, 0, img.getWidth, img.getHeight, null)
    }
  }
}
