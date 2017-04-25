package raytracer.textures

import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 23/04/2017.
  */
trait TextureMapping2D {
  def map(dg: DifferentialGeometry): TexMap
}

class UVMapping2D(
  val su: Double, val sv: Double, val du: Double, val dv: Double
) extends TextureMapping2D {
  override def map(dg: DifferentialGeometry): TexMap = {
    TexMap(su*dg.u + du, sv*dg.v + dv)
  }
}

case class TexMap(s: Double, t: Double)
