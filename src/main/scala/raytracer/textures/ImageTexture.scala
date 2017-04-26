package raytracer.textures

import java.io.File
import javax.imageio.ImageIO

import raytracer.Spectrum
import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 23/04/2017.
  */
class ImageSpectrumTexture(mapping: TextureMapping2D, texInfo: TextureInfo) extends Texture[Spectrum]{

  def convertIn(s: Spectrum): Spectrum = (s * texInfo.scale).pow(texInfo.gamma)

  private def getMipMap(): SpectrumMipMap = {

    if (ImageTexture.spectrumCache contains texInfo) {
      return ImageTexture.spectrumCache(texInfo)
    }

    val img = ImageIO.read(new File(texInfo.fileName))
    val width = img.getWidth()
    val height = img.getHeight()
    val imgData = new Array[Spectrum](width * height)

    var y = 0
    while (y < height) {
      var x = 0
      while (x < width) {
        imgData(y*width+x) = convertIn(Spectrum.fromRGBInt(img.getRGB(x, y)))
        x += 1
      }
      y += 1
    }

    val m = MipMap.createSpectrum(width, height, imgData, texInfo.doTrilinear, texInfo.maxAniso, texInfo.wrapMode)
    ImageTexture.spectrumCache += texInfo -> m
    m
  }

  val mipMap = getMipMap()

  override def apply(dg: DifferentialGeometry): Spectrum = {
    val texMap = mapping.map(dg)
    mipMap.lookUp(texMap.s, texMap.t, 0, 0, 0, 0) // TODO: Implement differentials
  }
}

class ImageFloatTexture(mapping: TextureMapping2D, texInfo: TextureInfo) extends Texture[Double]{

  def convertIn(s: Spectrum): Double = math.pow(s.getY() * texInfo.scale, texInfo.gamma)

  private def getMipMap(): FloatMipMap = {

    if (ImageTexture.floatCache contains texInfo) {
      return ImageTexture.floatCache(texInfo)
    }

    val img = ImageIO.read(new File(texInfo.fileName))
    val width = img.getWidth()
    val height = img.getHeight()
    val imgData = new Array[Double](width * height)

    var y = 0
    while (y < height) {
      var x = 0
      while (x < width) {
        imgData(y*width+x) = convertIn(Spectrum.fromRGBInt(img.getRGB(x, y)))
        x += 1
      }
      y += 1
    }

    val m = MipMap.createFloat(width, height, imgData, texInfo.doTrilinear, texInfo.maxAniso, texInfo.wrapMode)
    ImageTexture.floatCache += texInfo -> m
    m
  }

  val mipMap = getMipMap()

  override def apply(dg: DifferentialGeometry): Double = {
    val texMap = mapping.map(dg)
    mipMap.lookUp(texMap.s, texMap.t, 0, 0, 0, 0) // TODO: Implement differentials
  }
}
object ImageTexture {
  private[textures] var spectrumCache = Map[TextureInfo, SpectrumMipMap]()
  private[textures] var floatCache = Map[TextureInfo, FloatMipMap]()
}

case class TextureInfo (
  fileName: String, doTrilinear: Boolean, maxAniso: Double,
  wrapMode: ImageWrap, scale: Double, gamma: Double
)
