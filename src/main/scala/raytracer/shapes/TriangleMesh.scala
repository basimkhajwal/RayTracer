package raytracer.shapes
import raytracer.math._

/**
  * Created by Basim on 12/02/2017.
  */
class TriangleMesh(
  val indices: Array[Int], val points: Array[Point],
  o2w: Transform,
  val normals: Array[Normal], val uvs: Array[Double]
) extends Shape {

  // Transform mesh vertices to world space
  {
    var i = 0
    while (i < points.length) {
      points(i) = o2w(points(i))
      i += 1
    }
  }

  val hasNormals = normals != null
  val hasUVS = uvs != null

  override val objectToWorld: Transform = Transform.identity
  override val worldToObject: Transform = Transform.identity

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
