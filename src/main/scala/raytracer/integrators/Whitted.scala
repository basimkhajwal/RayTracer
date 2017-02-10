package raytracer.integrators
import raytracer._
import raytracer.math.Ray
import raytracer.primitives.Intersection

/**
  * Created by Basim on 05/01/2017.
  */
class Whitted extends Integrator{

  override def traceRay(ray: Ray)(implicit options: RenderOpts) = traceRay(ray, 0)

  def traceRay(ray: Ray, depth: Int)(implicit options: RenderOpts) : Spectrum = {
    val scene = options.scene
    scene intersect ray match {
      case None => Spectrum.BLACK
      case Some(Intersection(dg, _, _)) => {

        val p = dg.p

        val directLight =
          scene.lights.filter(l => {
            val lightT = l.pos.dist(p)
            scene intersect Ray(p, (l.pos - p).nor) match {
              case None => true
              case Some(Intersection(_, _, newT)) => newT > lightT
            }
          })
            .map(l => l.colour * (l.pos - p).nor.dot(dg.nn))
            .foldLeft(Spectrum.BLACK)(_ + _)

        if (depth == options.maxRayDepth) directLight
        else directLight + 0.2 * traceRay(Ray(p, (ray reflect dg.nn).nor), depth + 1)
      }
    }
  }
}
