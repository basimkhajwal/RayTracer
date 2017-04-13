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
    val mask = col.clamp * wi.dot(dg.nn).abs

    if (mask.isBlack()) mask
    else mask * integrator.traceRay(scene, Ray(dg.p, wi.nor, ray.depth+1))
  }

  def specularTransmit(scene: Scene, ray: Ray, isect: Intersection, integrator: Integrator): Spectrum = {
    val dg = isect.dg
    val bsdf = isect.getBSDF()
    val (col, wi) = bsdf.sample(-ray.dir, 0, 0, BSDF.TRANSMISSION | BSDF.SPECULAR)
    val mask = col.clamp * wi.dot(dg.nn).abs

    if (mask.isBlack()) mask
    else mask * integrator.traceRay(scene, Ray(dg.p + wi.nor * 1e-7, wi.nor, ray.depth+1))
  }
}
