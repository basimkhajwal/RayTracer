package raytracer.integrators
import raytracer._
import raytracer.bxdf.BSDF
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
      case Some(isect @ Intersection(dg, _, _)) => {

        val p = dg.p
        val bsdf = isect.getBSDF

        val directLight = scene.lights
          .map(l => {
            val (lightIntensity, wi, lightDist) = l.sample(p)
            val lightValue = lightIntensity * wi.dot(dg.nn) * bsdf(ray.dir, wi, BSDF.ALL_REFLECTION)

            scene intersect(Ray(p, wi), 0, lightDist) match {
              case None => lightValue
              case Some(_) => Spectrum.BLACK
            }
          })
          .foldLeft(Spectrum.BLACK)(_ + _)

        if (depth >= options.maxRayDepth) directLight
        else {
          val newDir = (ray reflect dg.nn).nor
          directLight + bsdf(ray.dir, newDir, BSDF.ALL_REFLECTION) *traceRay(Ray(p, newDir), depth + 1)
        }
      }
    }
  }
}
