package raytracer.primitives
import raytracer.bxdf.BSDF
import raytracer.math.{BBox, Point, Ray, Vec3}
import raytracer.shapes.DifferentialGeometry

/**
  * Created by Basim on 17/03/2017.
  */
class GridAccelerator(val primitives: Array[Primitive]) extends Primitive {

  override val worldBound = primitives.foldLeft(BBox.empty)((a, b) => a.union(b.worldBound))

  private def clamp(x: Int, min: Int, max: Int) = if (x < min) min else if (x > max) max else x

  val delta = worldBound.pMax - worldBound.pMin
  val nVoxels = {
    val maxAxis = worldBound.maximumExtent()
    val voxelsPerUnitDist = 3 * math.pow(primitives.length, 3) / delta(maxAxis)

    Array(0,1,2) map (a => clamp((delta(a) * voxelsPerUnitDist).toInt, 1, 64))
  }
  val width = Vec3(delta(0)/nVoxels(0), delta(1)/nVoxels(1), delta(2)/nVoxels(2))
  val invWidth = Vec3(1/width(0), 1/width(1), 1/width(2))
  val voxels = new Array[Voxel](nVoxels(0) * nVoxels(1) * nVoxels(2))

  private def addPrimitive(p: Primitive): Unit = {
    val bb = p.worldBound
    val vmin = Array(0,1,2) map (a => posToVoxel(bb.pMin, a))
    val vmax = Array(0,1,2) map (a => posToVoxel(bb.pMax, a))

    var x = 0
    var y = 0
    var z = vmin(2)

    while (z <= vmax(2)) {
      y = vmin(1)
      while (y <= vmax(1)) {
        x = vmin(0)
        while (x <= vmax(0)) {
          val o = z*nVoxels(1)*nVoxels(0) + y*nVoxels(0) + x
          if (voxels(o) == null) voxels(o) = new Voxel()
          voxels(o).addPrimitive(p)
          x += 1
        }
        y += 1
      }
      z += 1
    }
  }

  private def posToVoxel(p: Point, axis: Int): Int = {
    val ext = (p(axis)- worldBound.pMin(axis))*invWidth(axis)
    clamp(ext.toInt, 0, nVoxels(axis)-1)
  }

  private def voxelToPos(p: Int, axis: Int): Double = worldBound.pMin(axis) + p * width(axis)

  val nextCrossing = new Array[Double](3)
  val deltaT = new Array[Double](3)
  val step = new Array[Int](3)
  val out = new Array[Int](3)
  val pos = new Array[Int](3)

  override def intersect(ray: Ray): Option[Intersection] = {
    val intersectionTime = worldBound.checkIntersect(ray).orNull
    if (intersectionTime == null) return None

    val gridIntersect = ray.start + ray.dir * intersectionTime._1

    var axis = 0
    while (axis < 3) {
      pos(axis) = posToVoxel(gridIntersect, axis)

      if (ray.dir(axis) >= 0) {
        nextCrossing(axis) =
          intersectionTime._1 +
          (voxelToPos(pos(axis)+1, axis) - gridIntersect(axis)) / ray.dir(axis)
        deltaT(axis) = width(axis) / ray.dir(axis)
        step(axis) = 1
        out(axis) = nVoxels(axis)
      } else {
        nextCrossing(axis) =
          intersectionTime._1 +
            (voxelToPos(pos(axis), axis) - gridIntersect(axis)) / ray.dir(axis)
        deltaT(axis) = -width(axis) / ray.dir(axis)
        step(axis) = -1
        out(axis) = -1
      }

      axis += 1
    }

    var minT = Double.PositiveInfinity
    var minIsect: Intersection = null
    var done = false

    while (!done) {
      val voxel = voxels(pos(2)*nVoxels(0)*nVoxels(1) + pos(1)*nVoxels(0) + pos(0))
      if (voxel != null) {
        val isect = voxel.intersect(ray).orNull
        if (isect != null && isect.time < minT) {
          minT = isect.time
          minIsect = isect
        }
      }

      val stepAxis =
        if (nextCrossing(0) < nextCrossing(1)) {
          if (nextCrossing(0) < nextCrossing(2)) 0 else 2
        } else {
          if (nextCrossing(1) < nextCrossing(2)) 1 else 2
        }

      // TODO: Finish checking against the limits for voxels
    }

    Option(minIsect)
  }

  // Should never be called
  override def getBSDF(dg: DifferentialGeometry): BSDF = ???
}

class Voxel {
  private var primitives: List[Primitive] = Nil

  def addPrimitive(p: Primitive) = primitives ::= p

  def intersect(ray: Ray): Option[Intersection] = {
    var i = primitives
    var minT = Double.PositiveInfinity
    var minIsect: Intersection = null

    while (!i.isEmpty) {
      val isect = i.head.intersect(ray).orNull
      if (isect != null && isect.time < minT) {
        minT = isect.time
        minIsect = isect
      }

      i = i.tail
    }

    Option(minIsect)
  }
}