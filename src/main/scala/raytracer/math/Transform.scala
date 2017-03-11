package raytracer.math

/**
  * Created by Basim on 09/01/2017.
  */
case class Transform(mat: Mat4, matInv: Mat4) {

  lazy val inverse = Transform(matInv, mat)

  def apply(vec: Vec3): Vec3 = mat * vec

  def apply(point: Point): Point = mat * point

  def apply(ray: Ray): Ray = Ray(this(ray.start), this(ray.dir).nor, ray.depth)

  def apply(bbox: BBox): BBox = (0 to 7).map(p => this(bbox(p))).foldLeft(bbox)(_.union(_))

  def *(that: Transform): Transform = Transform(mat * that.mat, that.matInv * matInv)
}

object Transform {

  def apply(mat: Mat4): Transform = Transform(mat, mat.inv)

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
        0, 0, sz, 0,
        0, 0, 0, 1
      )),
      Mat4(Array(
        1/sx, 0, 0, 0,
        0, 1/sy, 0, 0,
        0, 0, 1/sz, 0,
        0, 0, 0, 1
      ))
    )
  }

  def rotate(angle: Double, axis: Vec3): Transform = {
    val a = axis.nor
    val s = math.sin(math.toRadians(angle))
    val c = math.cos(math.toRadians(angle))
    val data = new Array[Double](16)

    data(0) = a.x * a.x + (1.0 - a.x * a.x) * c
    data(1) = a.x * a.y * (1.0 - c) - a.z * s
    data(2) = a.x * a.z * (1.0 - c) + a.y * s
    data(3) = 0

    data(4) = a.x * a.y * (1.0 - c) + a.z * s
    data(5) = a.y * a.y + (1.0 - a.y * a.y) * c
    data(6) = a.y * a.z * (1.0 - c) - a.x * s
    data(7) = 0

    data(8) = a.x * a.z * (1.0 - c) - a.y * s
    data(9) = a.y * a.z * (1.0 - c) + a.x * s
    data(10) = a.z * a.z + (1.0 - a.z * a.z) * c
    data(11) = 0

    data(12) = 0
    data(13) = 0
    data(14) = 0
    data(15) = 1

    val m = Mat4(data)
    Transform(m, m.transpose)

    /* Un-optimised form

    // Rotate into xy plane
    val t1 = Transform.rotateY(math.toDegrees(math.atan2(a.z, a.x)))

    // Rotate onto x axis
    val l1 = math.sqrt(a.x*a.x + a.z*a.z)
    val t2 = Transform.rotateZ(360-math.toDegrees(math.atan2(a.y, l1)))
    val t3 = Transform.rotateX(angle)

    t1.inverse * t2.inverse * t3 * t2 * t1
    * */
  }

  def rotate(rx: Double, ry: Double, rz: Double): Transform = rotateX(rx) * rotateY(ry) * rotateZ(rz)

  def rotateX(t: Double): Transform = {
    val c = math.cos(math.toRadians(t))
    val s = math.sin(math.toRadians(t))
    val m = Mat4(Array(
      1, 0, 0, 0,
      0, c, -s, 0,
      0, s, c, 0,
      0, 0, 0, 1
    ))
    Transform(m, m.transpose)
  }

  def rotateY(t: Double): Transform = {
    val c = math.cos(math.toRadians(t))
    val s = math.sin(math.toRadians(t))
    val m = Mat4(Array(
      c, 0, s, 0,
      0, 1, 0, 0,
      -s, 0, c, 0,
      0, 0, 0, 1
    ))
    Transform(m, m.transpose)
  }

  def rotateZ(t: Double): Transform = {
    val c = math.cos(math.toRadians(t))
    val s = math.sin(math.toRadians(t))
    val m = Mat4(Array(
      c, -s, 0, 0,
      s, c, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1
    ))
    Transform(m, m.transpose)
  }

  def lookAt(pos: Point, look: Point, up: Vec3): Transform = {
    val dir = (look - pos).nor
    require(dir.cross(up.nor).mag2 != 0, "Up and viewing direction are parallel!")
    val left = up.nor.cross(dir).nor
    val newUp = dir.cross(left)

    val camToWorld = Mat4(Array(
      left.x, newUp.x, dir.x, pos.x,
      left.y, newUp.y, dir.y, pos.y,
      left.z, newUp.z, dir.z, pos.z,
      0     , 0      , 0    , 1
    ))
    Transform(camToWorld.inv, camToWorld)
  }
}
