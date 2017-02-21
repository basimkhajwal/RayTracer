package raytracer.films
import java.awt.{Dimension, Graphics}
import java.awt.image.BufferedImage
import javax.swing.{JFrame, JPanel}

import raytracer.Spectrum

/**
  * Created by Basim on 20/02/2017.
  */
class ScreenFilm(xRes: Int, yRes: Int, val width: Int, val height: Int) extends Film(xRes, yRes) {

  val image: BufferedImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

  override def applySample(imageX: Int, imageY: Int, l: Spectrum): Unit = {
    image.setRGB(imageX, imageY, l.toRGBInt)
  }

  override def saveImage: Unit = {
    val frame = new JFrame("Sphere Render Test")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setResizable(false)
    frame.add(new CustomRenderer(image, new Dimension(width, height)))
    frame.pack
    frame.setVisible(true)
  }

  class CustomRenderer(val img: BufferedImage, val viewportSize: Dimension) extends JPanel {

    override def getPreferredSize: Dimension = viewportSize

    override def paint(g: Graphics): Unit = {
      super.paint(g)
      g.drawImage(img, 0, 0, viewportSize.width, viewportSize.height, null)
    }
  }
}
