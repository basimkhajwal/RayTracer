package raytracer.math

/**
  * Created by Basim on 09/01/2017.
  */
case class Transform(mat: Mat4, matInv: Mat4) {

  lazy val inverse = Transform(matInv, mat)

  lazy val swapsHandedness: Boolean = mat.swapsHandedness

  def apply(vec: Vec3): Vec3 = {
    Vec3(
      mat.data(0)*vec(0)+mat.data(1)*vec(1)+mat.data(2)*vec(2),
      mat.data(4)*vec(0)+mat.data(5)*vec(1)+mat.data(6)*vec(2),
      mat.data(8)*vec(0)+mat.data(9)*vec(1)+mat.data(10)*vec(2)
    )
  }

  def apply(point: Point): Point = {
    Point(
      mat.data(0)*point(0)+mat.data(1)*point(1)+mat.data(2)*point(2)+mat.data(3),
      mat.data(4)*point(0)+mat.data(5)*point(1)+mat.data(6)*point(2)+mat.data(7),
      mat.data(8)*point(0)+mat.data(9)*point(1)+mat.data(10)*point(2)+mat.data(11)
    )
  }

  def apply(n: Normal): Normal = {
    val m = matInv.data
    Normal(
      m(0)*n.x + m(4)*n.y + m(8)*n.z,
      m(1)*n.x + m(5)*n.y + m(9)*n.z,
      m(2)*n.x + m(6)*n.y + m(10)*n.z
    )
  }

  def apply(ray: Ray): Ray = Ray(this(ray.start), this(ray.dir).nor, ray.depth)

  def apply(bbox: BBox): BBox = {
    var i = 0
    var bounds = BBox.empty
    while (i < 8) {
      bounds = bounds.union(this(bbox(i)))
      i += 1
    }
    bounds
  }

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
