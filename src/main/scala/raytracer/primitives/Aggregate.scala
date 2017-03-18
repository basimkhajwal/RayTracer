package raytracer.primitives
import raytracer.bxdf.BSDF
import raytracer.math.{BBox, Ray}
import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 18/03/2017.
  */
class Aggregate(val primitives: Array[Primitive]) extends Primitive {

  override val worldBound: BBox = primitives.foldLeft(BBox.empty)((a, b) => a.union(b.worldBound))

  override def intersect(ray: Ray): Option[Intersection] = {

    var minT = Double.PositiveInfinity
    var minIsect: Intersection = null

    var i = 0
    while (i < primitives.length) {

      val isect = primitives(i).intersect(ray).orNull

      if (isect != null && isect.time < minT) {
        minT = isect.time
        minIsect = isect
      }

      i += 1
    }

    Option(minIsect)
  }

  override def getBSDF(dg: DifferentialGeometry): BSDF = ???
}
