package raytracer.shapes

import raytracer.math._
import raytracer.Constants.EPSILON

/**
  * Created by Basim on 05/01/2017.
  */
case class Sphere(r: Double, o2w: Transform) extends Shape {

  override val objectToWorld: Transform = o2w
  override val worldToObject: Transform = o2w.inverse

  override val objectBounds: BBox = {
    val offset = Vec3(r, r, r)
    BBox(Point.ZERO+(-offset), Point.ZERO+offset)
  }

  override def intersect(worldRay: Ray): Option[(DifferentialGeometry, Double)] = {

    val ray = worldToObject(worldRay)

    // Solve the quadratic equation for t
    val dx = ray.start.x
    val dy = ray.start.y //(ray.start.y-centre.y)
    val dz = ray.start.z // (ray.start.z-centre.z)

    val a = 1 // Assuming ray direction is normalized
    val b = 2 * (ray.dir.x*dx + ray.dir.y*dy + ray.dir.z*dz)
    val c = dx*dx + dy*dy + dz*dz - r*r

    val det2 = b*b - 4*a*c

    if (det2 < 0) return None

    val det = Math.sqrt(det2)
    val t1 = ((-b) + det) / (2*a)
    val t2 = ((-b) - det) / (2*a)

    if (t1 <= 0 && t2 <= 0) return None

    val t =  if (t2 <= 0) t1 else t2 //if (t1 < 0) t2 else if (t2 < 0) t1 else Math.min(t1, t2)
    val point = ray.start + ray.dir*t
    val normal = (point-Point.ZERO).nor
    val surfacePoint = point + normal*EPSILON

    Some((DifferentialGeometry(objectToWorld(surfacePoint), normal, 0, 0, this), t))
  }

}
