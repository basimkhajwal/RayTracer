/**
  * Created by Basim on 15/12/2016.
  */

import Constants._

trait SceneObject {
  def intersect(ray: Ray): Intersection
}

case class Sphere(val r: Double, val centre: Vec3, val colour: Spectrum) extends SceneObject {

  override def intersect(ray: Ray): Intersection = {

    // Solve the quadratic equation for t
    val dx = (ray.start.x-centre.x)
    val dy = (ray.start.y-centre.y)
    val dz = (ray.start.z-centre.z)

    val a = 1 // Assuming ray direction is normalized
    val b = 2 * (ray.dir.x*dx + ray.dir.y*dy + ray.dir.z*dz)
    val c = dx*dx + dy*dy + dz*dz - r*r

    val det2 = b*b - 4*a*c

    if (det2 < 0) return Miss

    val det = Math.sqrt(det2)
    val t1 = ((-b) + det) / (2*a)
    val t2 = ((-b) - det) / (2*a)

    if (t1 <= 0 && t2 <= 0) return Miss

    val t =  if (t2 <= 0) t1 else t2 //if (t1 < 0) t2 else if (t2 < 0) t1 else Math.min(t1, t2)
    val point = ray.start + ray.dir*t
    val normal = (point - centre).nor

    Hit(t, point + normal * EPSILON, normal, colour)
  }
}
