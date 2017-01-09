package raytracer.math

/**
  * Created by Basim on 09/01/2017.
  */
class Transform(val mat: Mat4, _matInv: => Mat4) {

  def this(mat: Mat4) = this(mat, mat.inv)

  lazy val matInv = _matInv

  def apply(point: Vec3, isVector: Boolean = false): Vec3 = mat.multiply(point, if (isVector) 0 else 1)

  def undo(point: Vec3, isVector: Boolean = false): Vec3 = matInv.multiply(point, if (isVector) 0 else )
}

object Transform {

  def translate(delta: Vec3) = translate(delta.x, delta.y, delta.z)
  def translate(dx: Double, dy: Double, dz: Double): Transform = {
    new Transform(
      new Mat4(Array(
        1, 0, 0, dx,
        0, 1, 0, dy,
        0, 0, 1, dz,
        0, 0, 1, 1
      )),
      new Mat4(Array(
        1, 0, 0, -dx,
        0, 1, 0, -dy,
        0, 0, 1, -dz,
        0, 0, 1, 1
      ))
    )
  }

  def scale(sf: Vec3) = scale(sf.x, sf.y, sf.z)
  def scale(sx: Double, sy: Double, sz: Double): Transform = {
    new Transform(
      new Mat4(Array(
        sx, 0, 0, 0,
        0, sy, 0, 0,
        0, 0, sy, 0,
        0, 0, 1, 1
      )),
      new Mat4(Array(
        1/sx, 0, 0, 0,
        0, 1/sy, 0, 0,
        0, 0, 1/sy, 0,
        0, 0, 1, 1
      ))
    )
  }

}
