package raytracer.integrators

import raytracer.bxdf.BSDF
import raytracer.math.Ray
import raytracer.primitives.Intersection
import raytracer.{Scene, Spectrum}

/**
  * Created by Basim on 05/01/2017.
  */
trait Integrator {
  def traceRay(scene: Scene, ray: Ray): Spectrum
}

object Integrator {

  def specularReflect(scene: Scene, ray: Ray, isect: Intersection, integrator: Integrator): Spectrum = {
    val dg = isect.dg
    val bsdf = isect.getBSDF()
    val (col, wi) = bsdf.sample(-ray.dir, 0, 0, BSDF.REFLECTION | BSDF.SPECULAR)

    if (col.isBlack()) col
    else col.clamp * integrator.traceRay(scene, Ray(dg.p, wi.nor, ray.depth+1)) * wi.dot(dg.nn).abs
  }
}
