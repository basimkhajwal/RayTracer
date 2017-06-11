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
    val (col, wi, pdf) = bsdf.sample(-ray.dir, 0, 0, BSDF.REFLECTION | BSDF.SPECULAR)

    if (pdf < 1e-9) return Spectrum.BLACK

    val mask = col * (wi.dot(dg.nn).abs / pdf)
    if (mask.isBlack()) mask
    else mask.clamp() * integrator.traceRay(scene, Ray(dg.p + wi*1e-9, wi, ray.depth+1))
  }

  def specularTransmit(scene: Scene, ray: Ray, isect: Intersection, integrator: Integrator): Spectrum = {
    val dg = isect.dg
    val bsdf = isect.getBSDF()
    val (col, wi, pdf) = bsdf.sample(-ray.dir, 0, 0, BSDF.TRANSMISSION | BSDF.SPECULAR)
    val mask = col * (wi.dot(dg.nn).abs / pdf)

    if (pdf < 1e-9) return Spectrum.BLACK

    if (mask.isBlack()) mask
    else mask.clamp() * integrator.traceRay(scene, Ray(dg.p + wi*1e-9, wi, ray.depth+1))
  }
}
