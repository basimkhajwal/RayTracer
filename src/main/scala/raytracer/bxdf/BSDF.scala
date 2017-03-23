package raytracer.bxdf

import raytracer.Spectrum
import raytracer.math.{Mat4, Transform, Vec3}
import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 31/01/2017.
  */
final class BSDF (
  val dgShading: DifferentialGeometry,
  val ng: Vec3
) {

  private var bxdfs: List[BxDF] = Nil
  def add(b: BxDF) = bxdfs ::= b

  private val nn: Vec3 = dgShading.nn
  private val sn: Vec3 = dgShading.dpdu.nor
  private val tn: Vec3 = nn cross sn

  private val transformMat: Mat4 = new Mat4(Array(
    sn.x, sn.y, sn.z, 0,
    tn.x, tn.y, tn.z, 0,
    nn.x, nn.y, nn.z, 0,
    0, 0, 0, 0
  ))
  private val worldToLocal = new Transform(transformMat, transformMat.transpose)
  private val localToWorld = worldToLocal.inverse

  def apply(woW: Vec3, wiW: Vec3, flags: Int): Spectrum = {
    val wo = worldToLocal(woW)
    val wi = worldToLocal(wiW)
    var total = Spectrum.BLACK
    var bxdf = bxdfs
    while (bxdf.nonEmpty) {
      if (bxdf.head matches flags) {
        total += bxdf.head(wo, wi)
      }
      bxdf = bxdf.tail
    }
    total
  }

  def sample(woW: Vec3, u1: Double, u2: Double, flags: Int): (Spectrum, Vec3) = {
    val wo = worldToLocal(woW)

    bxdfs.find(_ matches flags) match {
      case None => (Spectrum.BLACK, Vec3.ZERO)
      case Some(b) => {
        val (wi, lum) = b.sample(wo, u1, u2)
        (lum, localToWorld(wi))
      }
    }
  }
}

object BSDF {
  val REFLECTION = 1
  val TRANSMISSION = 2
  val DIFFUSE = 4
  val GLOSSY = 8
  val SPECULAR = 16

  val ALL = DIFFUSE | GLOSSY | SPECULAR
  val ALL_REFLECTION = ALL | REFLECTION
  val ALL_TRANSMISSION = ALL | TRANSMISSION
}


