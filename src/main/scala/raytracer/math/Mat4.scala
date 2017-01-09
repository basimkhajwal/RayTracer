package raytracer.math

/**
  * Created by Basim on 08/01/2017.
  */
class Mat4(val data: Array[Double]) {
  require(data.length == 16, "All 16 elements must be specified")

  def det: Double = {

    @inline
    def computeBottomDet(c1: Int, c2: Int): Double = data(8+c1)*data(12+c2)-data(8+c2)*data(12+c1)

    // cache 2x2 determinants
    val ls = (0 to 2).map(c1 => (0 to 3).map(c2 => computeBottomDet(c1, c2)).toArray).toArray

    data(0) * (data(5)*ls(2)(3) - data(6)*ls(1)(3) + data(7)*ls(1)(2))
    - data(1) * (data(4)*ls(2)(3) - data(6)*ls(0)(3) + data(7)*ls(0)(2))
    + data(2) * (data(4)*ls(1)(3) - data(5)*ls(0)(3) + data(7)*ls(0)(1))
    - data(3) * (data(4)*ls(1)(2) - data(5)*ls(0)(2) + data(6)*ls(0)(1))
  }

  def apply(r: Int, c: Int): Double = {
    require(r >= 0 && r < 4, "Row index out of bounds")
    require(c >= 0 && c < 4, "Column index out of bounds")
    data(r*4+c)
  }

  def update(r: Int, c: Int, v: Double): Unit = {
    require(r >= 0 && r < 4, "Row index out of bounds")
    require(c >= 0 && c < 4, "Column index out of bounds")
    data(r*4+c) = v
  }
}
