package raytracer.math

/**
  * Created by Basim on 08/01/2017.
  */
class Mat4(val data: Array[Double]) {
  require(data.length == 16, "All 16 elements must be specified")

  def det: Double = {
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
