package raytracer.textures

import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 24/04/2017.
  */
class Checkerboard[T](
  val mapping: TextureMapping2D,
  val tex1: Texture[T], val tex2: Texture[T],
  val aaMethod: AAMethod
) extends Texture[T]{

  override def apply(dg: DifferentialGeometry): T = {
    val texMap = mapping.map(dg)

    if ((texMap.s.toInt + texMap.t.toInt) % 2 == 0) tex1(dg)
    else tex2(dg)
    // TODO: Use ray differentials to do anti-aliasing
  }

}

sealed trait AAMethod
object AAMethod {
  def fromString(str: String): AAMethod = {
    str.toLowerCase() match {
      case "none" => None
      case "closedform" => ClosedForm
      case _ => throw new NotImplementedError("Undefined anti-aliasing method " + str)
    }
  }
}
object None extends AAMethod
object ClosedForm extends AAMethod
