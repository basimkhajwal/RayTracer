package raytracer.textures

import java.io.File
import javax.imageio.ImageIO

import raytracer.Spectrum
import raytracer.shapes.DifferentialGeometry
import raytracer.textures.MipMap._

/**
  * Created by Basim on 23/04/2017.
  */
class ImageTexture[T : Manifest](
  val mapping: TextureMapping2D, val fileName: String,
  val doTrilinear: Boolean, val maxAniso: Double, val wrapMode: ImageWrap,
  val scale: Double, val gamma: Double,
  convertIn: (Spectrum, Double, Double) => T
)(implicit evidence: T => MipMappable[T]) extends Texture[T]{

 def this(
   m: TextureMapping2D, t: TextureInfo,
   convertIn: (Spectrum, Double, Double) => T)
 (implicit evidence: T => MipMappable[T]) = {
    this(m, t.fileName, t.doTrilinear, t.maxAniso, t.wrapMode, t.scale, t.gamma, convertIn)
  }

  private def getMipMap(): MipMap[T] = {
    val img = ImageIO.read(new File(fileName))
    val width = img.getWidth()
    val height = img.getHeight()
    val imgData = new Array[T](width * height)

    var y = 0
    while (y < height) {
      var x = 0
      while (x < width) {
        imgData(y*width+x) = convertIn(Spectrum.fromRGBInt(img.getRGB(x, y)), scale, gamma)
        x += 1
      }
      y += 1
    }

    new MipMap[T](width, height, imgData, doTrilinear, maxAniso, wrapMode)
  }

  val mipMap: MipMap[T] = getMipMap()

  override def apply(dg: DifferentialGeometry): T = {
    val texMap = mapping.map(dg)
    mipMap.lookUp(texMap.s, texMap.t, 0, 0, 0, 0) // TODO: Implement differentials
  }
}

/*
* Note: There will be a bug in the texture caching when different mapping schemes relate to the
* same image details, fix this later
* */
object ImageTexture {

  private var floatImages = Map[TextureInfo, ImageTexture[Double]]()
  private var spectrumImages = Map[TextureInfo, ImageTexture[Spectrum]]()

  def convertSpectrum(s: Spectrum, scale: Double, gamma: Double): Spectrum = (s * scale).pow(gamma)

  def convertFloat(s: Spectrum, scale: Double, gamma: Double): Double = math.pow(scale * s.getY(), gamma)

  def createFloatImage(mapping: TextureMapping2D, textureInfo: TextureInfo): ImageTexture[Double] = {
    if (floatImages.contains(textureInfo)) floatImages(textureInfo)
    else {
      val img = new ImageTexture[Double](mapping, textureInfo, convertFloat)
      floatImages += (textureInfo -> img)
      img
    }
  }

  def createSpectrumImage(mapping: TextureMapping2D, textureInfo: TextureInfo): ImageTexture[Spectrum] = {
    if (spectrumImages.contains(textureInfo)) spectrumImages(textureInfo)
    else {
      val img = new ImageTexture[Spectrum](mapping, textureInfo, convertSpectrum)
      spectrumImages += (textureInfo -> img)
      img
    }
  }
}

case class TextureInfo (
  fileName: String, doTrilinear: Boolean, maxAniso: Double,
  wrapMode: ImageWrap, scale: Double, gamma: Double
)

