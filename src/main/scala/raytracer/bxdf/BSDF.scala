package raytracer.bxdf

import raytracer.Spectrum
import raytracer.math.{Mat4, Normal, Transform, Vec3}
import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 31/01/2017.
  */
final class BSDF (
  val dgShading: DifferentialGeometry,
  val ng: Normal,
  val eta: Double
) {
  def this(dgShading: DifferentialGeometry, ng: Normal) = this(dgShading, ng, 1)

  private var bxdfs: List[BxDF] = Nil
  def add(b: BxDF) = bxdfs ::= b

  private val nn: Normal = dgShading.nn
  private val sn: Vec3 = dgShading.dpdu.nor
  private val tn: Vec3 = nn cross sn

  private def worldToLocal(v: Vec3): Vec3 = Vec3(v dot sn, v dot tn, v dot nn)

  private def localToWorld(v: Vec3): Vec3 = {
    Vec3(
      sn.x * v.x + tn.x * v.y + nn.x * v.z,
      sn.y * v.x + tn.y * v.y + nn.y * v.z,
      sn.z * v.x + tn.z * v.y + nn.z * v.z
    )
  }

  def apply(woW: Vec3, wiW: Vec3, inFlags: Int): Spectrum = {
    val wo = worldToLocal(woW).nor
    val wi = worldToLocal(wiW).nor

    val flags = inFlags & (if ((wiW dot ng) * (woW dot ng) > 0) ~BSDF.TRANSMISSION else ~BSDF.REFLECTION)

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

  def pdf(woW: Vec3, wiW: Vec3, flags: Int): Double = {
    if (bxdfs.isEmpty) return 0

    val wo = worldToLocal(woW)
    val wi = worldToLocal(wiW)
    var totalPdf = 0.0
    var matchingComps = 0
    var bxdf = bxdfs

    while (bxdf.nonEmpty) {
      if (bxdfs.head matches flags) {
        matchingComps += 1
        totalPdf += bxdfs.head.pdf(wo, wi)
      }
      bxdf = bxdf.tail
    }

    if (matchingComps > 0) totalPdf / matchingComps else 0
  }

  def sample(woW: Vec3, u1: Double, u2: Double, flags: Int): (Spectrum, Vec3, Double) = {
    val wo = worldToLocal(woW).nor

    var numMatching = 0
    var bxdf = bxdfs

    while (bxdf.nonEmpty) {
      if (bxdf.head matches flags) numMatching += 1
      bxdf = bxdf.tail
    }

    var selectedBxdf = (math.random() * numMatching).toInt
    var wi: Vec3 = null
    var lum: Spectrum = null
    var sampledBxdf: BxDF = null
    var samplePdf = 0.0
    bxdf = bxdfs

    while (selectedBxdf >= 0 && bxdf.nonEmpty) {
      if (bxdf.head matches flags) {
        if (selectedBxdf == 0) {
          val sample = bxdf.head.sample(wo, u1, u2)
          wi = sample._1
          lum = sample._2
          samplePdf = sample._3
          sampledBxdf = bxdf.head
        }
        selectedBxdf -= 1
      }
      bxdf = bxdf.tail
    }

    if (numMatching == 0 || wi.mag2 == 0 || samplePdf == 0) {
      return (Spectrum.BLACK, Vec3.ZERO, 0)
    }

    val wiW = localToWorld(wi).nor

    if ((flags & BSDF.SPECULAR) == 0) {
      (apply(woW, wiW, flags), wiW, pdf(woW, wiW, flags))
    } else {
      (lum, wiW, samplePdf)
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


