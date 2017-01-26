package raytracer.integrators
import raytracer._
import raytracer.math.Ray

/**
  * Created by Basim on 05/01/2017.
  */
class Whitted extends Integrator{

  override def traceRay(ray: Ray)(implicit options: RenderOpts) = traceRay(ray, 0)

  def traceRay(ray: Ray, depth: Int)(implicit options: RenderOpts) : Spectrum = {
    val scene = options.scene
    scene intersect ray match {
      case None => Spectrum.BLACK
      case Some(Intersection(_, p, n, c)) => {

        val directLight =
          scene.lights.filter(l => {
            val lightT = l.pos.dist(p)
            scene intersect Ray(p, (l.pos - p).nor) match {
              case None => true
              case Some(Intersection(newT, _, _, _)) => newT > lightT
            }
          })
            .map(l => l.colour * (l.pos - p).nor.dot(n))
            .foldLeft(Spectrum.BLACK)(_ + _)

        if (depth == options.maxRayDepth) c * directLight
        else c * (directLight + 0.2 * traceRay(Ray(p, (ray reflect n).nor), depth + 1))
      }
    }
  }
}
