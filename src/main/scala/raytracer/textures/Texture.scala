package raytracer.textures

import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 09/02/2017.
  */
trait Texture[T] {
  def apply(dg: DifferentialGeometry): T
}

case class ConstantTexture[T](value: T) extends Texture[T] {
  override def apply(dg: DifferentialGeometry) = value
}