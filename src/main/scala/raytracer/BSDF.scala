package raytracer

import org.apache.commons.lang3.builder.Diff
import raytracer.math.Vec3
import raytracer.shapes.DifferentialGeometry

import scala.collection.mutable.ListBuffer

/**
  * Created by Basim on 31/01/2017.
  */
final class BSDF (
  val dg: DifferentialGeometry,
  val ng: Vec3
) {

  private val bxdfs = new ListBuffer[BxDF]

  def add(b: BxDF) = bxdfs append b
}

trait BxDF {
  def matches(flags: Int): Boolean = (flags & bsdfType) == bsdfType

  val bsdfType: Int

  def apply(wo: Vec3, wi: Vec3): Spectrum
}

object BSDFType {
  val REFLECTION = 1
  val TRANSMISSION = 2
  val DIFFUSE = 4
  val GLOSSY = 8
  val SPECULAR = 16
}

class Lambertian(reflectance: Spectrum) extends BxDF {

  override val bsdfType: Int = BSDFType.REFLECTION | BSDFType.DIFFUSE

  override def apply(wo: Vec3, wi: Vec3): Spectrum = reflectance * (1 / Math.PI)
}


