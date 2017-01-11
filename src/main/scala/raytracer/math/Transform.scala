package raytracer.math

/**
  * Created by Basim on 09/01/2017.
  */
class Transform(val mat: Mat4, _matInv: => Mat4) {

  def this(mat: Mat4) = this(mat, mat.inv)

  lazy val matInv = _matInv

  def apply(point: Vec3, isVector: Boolean = false): Vec3 = mat.multiply(point, if (isVector) 0 else 1)

  def apply(bbox: BBox): BBox = (0 to 7).map(bbox(_)).foldLeft(bbox)(_.union(_))
}

object Transform {

  def apply(mat: Mat4, matInv: => Mat4): Transform = new Transform(mat, matInv)

  val identity = Transform(Mat4.identity, Mat4.identity)

  def translate(delta: Vec3): Transform = translate(delta.x, delta.y, delta.z)
  def translate(dx: Double, dy: Double, dz: Double): Transform = {
    Transform(
      Mat4(Array(
        1, 0, 0, dx,
        0, 1, 0, dy,
        0, 0, 1, dz,
        0, 0, 1, 1
      )),
      Mat4(Array(
        1, 0, 0, -dx,
        0, 1, 0, -dy,
        0, 0, 1, -dz,
        0, 0, 1, 1
      ))
    )
  }

  def scale(sf: Vec3): Transform = scale(sf.x, sf.y, sf.z)
  def scale(sx: Double, sy: Double, sz: Double): Transform = {
    Transform(
      Mat4(Array(
        sx, 0, 0, 0,
        0, sy, 0, 0,
        0, 0, sy, 0,
        0, 0, 1, 1
      )),
      Mat4(Array(
        1/sx, 0, 0, 0,
        0, 1/sy, 0, 0,
        0, 0, 1/sy, 0,
        0, 0, 1, 1
      ))
    )
  }

}
