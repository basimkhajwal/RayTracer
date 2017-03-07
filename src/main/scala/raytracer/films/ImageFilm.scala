package raytracer.films
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import raytracer.Spectrum
import raytracer.sampling.CameraSample

/**
  * Created by Basim on 20/02/2017.
  */
class ImageFilm(fileName: String, xRes: Int, yRes: Int) extends Film(xRes, yRes) {

  val image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

  override def applySample(sample: CameraSample, l: Spectrum): Unit = {
    image.setRGB(sample.imageX.toInt, sample.imageY.toInt, l.toRGBInt)
  }

  override def saveImage: Unit = {
    ImageIO.write(image, "png", new File(fileName))
  }
}
