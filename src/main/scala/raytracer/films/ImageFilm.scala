package raytracer.films
import java.io.File
import javax.imageio.ImageIO

import raytracer.filters.Filter

/**
  * Created by Basim on 20/02/2017.
  */
class ImageFilm(
  filter: Filter, xRes: Int, yRes: Int,
  cw: (Double, Double, Double, Double), fileName: String
) extends Film(filter, xRes, yRes, cw) {

  override def saveImage: Unit = {
    ImageIO.write(getBufferedImage(), "png", new File(fileName))
  }
}
