package raytracer.films
import java.awt.{Dimension, Graphics}
import java.awt.image.BufferedImage
import javax.swing.{JFrame, JPanel}

import raytracer.filters.Filter

/**
  * Created by Basim on 20/02/2017.
  */
class ScreenFilm(
  filter: Filter, xRes: Int, yRes: Int,
  cw: (Double, Double, Double, Double),
  val width: Int, val height: Int)
  extends Film(filter, xRes, yRes, cw) {

  override def saveImage(): Unit = {
    val frame = new JFrame("Ray Tracer")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setResizable(false)
    frame.add(new CustomRenderer(getBufferedImage(), new Dimension(width, height)))
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
