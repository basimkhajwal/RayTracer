package raytracer.math

/**
  * Created by Basim on 08/01/2017.
  */
case class Mat4(val data: Array[Double]) {
  require(data.length == 16, "All 16 elements must be specified")

  lazy val inv: Mat4 = {

    val inv: Array[Double] = new Array[Double](16)

    inv(0) = data(5)  * data(10) * data(15) - data(5)  * data(11) * data(14) -
             data(9)  * data(6)  * data(15) + data(9)  * data(7)  * data(14) +
             data(13) * data(6)  * data(11) - data(13) * data(7)  * data(10)

    inv(4) = -data(4)  * data(10) * data(15) + data(4)  * data(11) * data(14) +
              data(8)  * data(6)  * data(15) - data(8)  * data(7)  * data(14) -
              data(12) * data(6)  * data(11) + data(12) * data(7)  * data(10)

    inv(8) = data(4)  * data(9) * data(15) - data(4)  * data(11) * data(13) -
             data(8)  * data(5) * data(15) + data(8)  * data(7) * data(13) +
             data(12) * data(5) * data(11) - data(12) * data(7) * data(9)

    inv(12) = -data(4)  * data(9) * data(14) + data(4)  * data(10) * data(13) +
               data(8)  * data(5) * data(14) - data(8)  * data(6) * data(13) -
               data(12) * data(5) * data(10) + data(12) * data(6) * data(9)

    inv(1) = -data(1)  * data(10) * data(15) + data(1)  * data(11) * data(14) +
              data(9)  * data(2) * data(15) - data(9)  * data(3) * data(14) -
              data(13) * data(2) * data(11) + data(13) * data(3) * data(10)

    inv(5) = data(0)  * data(10) * data(15) - data(0)  * data(11) * data(14) -
             data(8)  * data(2) * data(15) + data(8)  * data(3) * data(14) +
             data(12) * data(2) * data(11) - data(12) * data(3) * data(10)

    inv(9) = -data(0)  * data(9) * data(15) + data(0)  * data(11) * data(13) +
              data(8)  * data(1) * data(15) - data(8)  * data(3) * data(13) -
              data(12) * data(1) * data(11) + data(12) * data(3) * data(9)

    inv(13) = data(0)  * data(9) * data(14) - data(0)  * data(10) * data(13) -
              data(8)  * data(1) * data(14) + data(8)  * data(2) * data(13) +
              data(12) * data(1) * data(10) - data(12) * data(2) * data(9)

    inv(2) = data(1)  * data(6) * data(15) - data(1)  * data(7) * data(14) -
             data(5)  * data(2) * data(15) + data(5)  * data(3) * data(14) +
             data(13) * data(2) * data(7) - data(13) * data(3) * data(6)

    inv(6) = -data(0)  * data(6) * data(15) + data(0)  * data(7) * data(14) +
              data(4)  * data(2) * data(15) - data(4)  * data(3) * data(14) -
              data(12) * data(2) * data(7) + data(12) * data(3) * data(6)

    inv(10) = data(0)  * data(5) * data(15) - data(0)  * data(7) * data(13) -
              data(4)  * data(1) * data(15) + data(4)  * data(3) * data(13) +
              data(12) * data(1) * data(7) - data(12) * data(3) * data(5)

    inv(14) = -data(0)  * data(5) * data(14) + data(0)  * data(6) * data(13) +
               data(4)  * data(1) * data(14) - data(4)  * data(2) * data(13) -
               data(12) * data(1) * data(6) + data(12) * data(2) * data(5)

    inv(3) = -data(1) * data(6) * data(11) + data(1) * data(7) * data(10) +
              data(5) * data(2) * data(11) - data(5) * data(3) * data(10) -
              data(9) * data(2) * data(7) + data(9) * data(3) * data(6)

    inv(7) = data(0) * data(6) * data(11) - data(0) * data(7) * data(10) -
             data(4) * data(2) * data(11) + data(4) * data(3) * data(10) +
             data(8) * data(2) * data(7) - data(8) * data(3) * data(6)

    inv(11) = -data(0) * data(5) * data(11) + data(0) * data(7) * data(9) +
               data(4) * data(1) * data(11) - data(4) * data(3) * data(9) -
               data(8) * data(1) * data(7) + data(8) * data(3) * data(5)

    inv(15) = data(0) * data(5) * data(10) - data(0) * data(6) * data(9) -
              data(4) * data(1) * data(10) + data(4) * data(2) * data(9) +
              data(8) * data(1) * data(6) - data(8) * data(2) * data(5)

    val det = data(0) * inv(0) + data(1) * inv(4) + data(2) * inv(8) + data(3) * inv(12)
    require(det != 0, "Determinant of matrix cannot be 0 for inverse")

    Mat4( inv map (_ / det))
  }

  lazy val det: Double = {
    data(12) * data(9)  * data(6)  * data(3)   -  data(8) * data(13) * data(6)  * data(3)   -
    data(12) * data(5)  * data(10) * data(3)   +  data(4) * data(13) * data(10) * data(3)   +
    data(8)  * data(5)  * data(14) * data(3)   -  data(4) * data(9)  * data(14) * data(3)   -
    data(12) * data(9)  * data(2)  * data(7)   +  data(8) * data(13) * data(2)  * data(7)   +
    data(12) * data(1)  * data(10) * data(7)   -  data(0) * data(13) * data(10) * data(7)   -
    data(8)  * data(1)  * data(14) * data(7)   +  data(0) * data(9)  * data(14) * data(7)   +
    data(12) * data(5)  * data(2)  * data(11)  -  data(4) * data(13) * data(2)  * data(11)  -
    data(12) * data(1)  * data(6)  * data(11)  +  data(0) * data(13) * data(6)  * data(11)  +
    data(4)  * data(1)  * data(14) * data(11)  -  data(0) * data(5)  * data(14) * data(11)  -
    data(8)  * data(5)  * data(2)  * data(15)  +  data(4) * data(9)  * data(2)  * data(15)  +
    data(8)  * data(1)  * data(6)  * data(15)  -  data(0) * data(9)  * data(6)  * data(15)  -
    data(4)  * data(1)  * data(10) * data(15)  +  data(0) * data(5)  * data(10) * data(15)
  }

  lazy val transpose =
    Mat4(
      Array(0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15) map (data(_))
    )

  @inline
  final def apply(r: Int, c: Int): Double = {
    require(r >= 0 && r < 4, "Row index out of bounds")
    require(c >= 0 && c < 4, "Column index out of bounds")
    data(r*4+c)
  }

  def *(vec: Vec3): Vec3 = {
    Vec3(
      data(0)*vec(0)+data(1)*vec(1)+data(2)*vec(2),
      data(4)*vec(0)+data(5)*vec(1)+data(6)*vec(2),
      data(8)*vec(0)+data(9)*vec(1)+data(10)*vec(2)
    )
  }

  def *(point: Point): Point = {
    Point(
      data(0)*point(0)+data(1)*point(1)+data(2)*point(2)+data(3),
      data(4)*point(0)+data(5)*point(1)+data(6)*point(2)+data(7),
      data(8)*point(0)+data(9)*point(1)+data(10)*point(2)+data(11)
    )
  }

  def *(that: Mat4): Mat4 = Mat4(
      (
        for (r <- 0 to 3; c <- 0 to 3) yield (0 to 3).map(i => this(r, i)*that(i, c)).sum
      ).toArray)

  def *(sf: Double): Mat4 = Mat4(data map (_ * sf))
  def /(sf: Double): Mat4 = Mat4(data map (_ / sf))
}

object Mat4 {

  val identity = new Mat4(Array(
    1, 0, 0, 0,
    0, 1, 0, 0,
    0, 0, 1, 0,
    0, 0, 0, 1.0
  )) {
    override lazy val inv = this
    override lazy val det = 1
    override def *(that: Mat4) = that
    override def *(that: Point) = that
    override def *(that: Vec3) = that
  }
}
