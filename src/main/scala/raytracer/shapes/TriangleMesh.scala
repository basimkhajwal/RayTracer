package raytracer.shapes
import raytracer.math._

/**
  * Created by Basim on 12/02/2017.
  */
class TriangleMesh(
  val indices: Array[Int],
  val points: Array[Point],
  o2w: Transform,
  val normals: Array[Vec3],
  val uvs: Array[Double]
) extends Shape {

  val hasNormals = normals != null
  val hasUVS = uvs != null

  override val objectToWorld: Transform = o2w
  override val worldToObject: Transform = objectToWorld.inverse

  val triangles = (0 until (indices.length/3)) map (Triangle(this, _)) toArray

  override val objectBounds: BBox = triangles.foldLeft(BBox.empty)((a,b) => a.union(b.objectBounds))

  override def intersect(ray: Ray): Option[(DifferentialGeometry, Double)] = {

    var minT = Double.PositiveInfinity
    var minIsect: (DifferentialGeometry, Double) = null

    var i = 0
    while (i < triangles.length) {
      val isect = triangles(i).intersect(ray).orNull
      if (isect != null && isect._2 < minT) {
        minT = isect._2
        minIsect = isect
      }
      i += 1
    }

    Option(minIsect)
  }
}
