package raytracer.textures

import java.io.File
import javax.imageio.ImageIO

import raytracer.Spectrum
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.MipMap._

/**
  * Created by Basim on 23/04/2017.
  */
class ImageSpectrumTexture(
  val mapping: TextureMapping2D, val fileName: String,
  val doTrilinear: Boolean, val maxAniso: Double, val wrapMode: ImageWrap,
  val scale: Double, val gamma: Double
) extends Texture[Spectrum]{

  def this(m: TextureMapping2D, t: TextureInfo) = {
    this(m, t.fileName, t.doTrilinear, t.maxAniso, t.wrapMode, t.scale, t.gamma)
  }

  def convertIn(s: Spectrum): Spectrum = (s * scale).pow(gamma)

  private def getMipMap(): SpectrumMipMap = {
    val img = ImageIO.read(new File(fileName))
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

    MipMap.createSpectrum(width, height, imgData, doTrilinear, maxAniso, wrapMode)
  }

  val mipMap = getMipMap()

  override def apply(dg: DifferentialGeometry): Spectrum = {
    val texMap = mapping.map(dg)
    mipMap.lookUp(texMap.s, texMap.t, 0, 0, 0, 0) // TODO: Implement differentials
  }
}

/*
* Note: There will be a bug in the texture caching when different mapping schemes relate to the
* same image details, fix this later
* */
object ImageTexture {

  //private var floatImages = Map[TextureInfo, ImageTexture[Double]]()
  private var spectrumImages = Map[TextureInfo, ImageSpectrumTexture]()

  //def convertFloat(s: Spectrum, scale: Double, gamma: Double): Double = math.pow(scale * s.getY(), gamma)

  /*def createFloatImage(mapping: TextureMapping2D, textureInfo: TextureInfo): ImageTexture[Double] = {
    if (floatImages.contains(textureInfo)) floatImages(textureInfo)
    else {
      val img = create[Double](mapping, textureInfo, convertFloat)
      floatImages += (textureInfo -> img)
      img
    }
  }*/

  def createSpectrumImage(mapping: TextureMapping2D, textureInfo: TextureInfo): ImageSpectrumTexture = {
    if (spectrumImages.contains(textureInfo)) spectrumImages(textureInfo)
    else {
      val img = new ImageSpectrumTexture(mapping, textureInfo)
      spectrumImages += (textureInfo -> img)
      img
    }
  }
}

case class TextureInfo (
  fileName: String, doTrilinear: Boolean, maxAniso: Double,
  wrapMode: ImageWrap, scale: Double, gamma: Double
)

