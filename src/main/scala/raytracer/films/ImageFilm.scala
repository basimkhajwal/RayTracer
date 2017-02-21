package raytracer.films
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import raytracer.Spectrum

/**
  * Created by Basim on 20/02/2017.
  */
class ImageFilm(fileName: String, xRes: Int, yRes: Int) extends Film(xRes, yRes) {

  val image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

  override def applySample(imageX: Int, imageY: Int, l: Spectrum): Unit = {
    image.setRGB(imageX, imageY, l.toRGBInt)
  }

  override def saveImage: Unit = {
    ImageIO.write(image, "png", new File(fileName))
  }
}
