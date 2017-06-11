package raytracer.integrators
import raytracer.bxdf.BSDF
import raytracer.{Scene, Spectrum}
import raytracer.math.Ray

/**
  * Created by Basim on 01/05/2017.
  */
class PathIntegrator(val maxDepth: Int) extends Integrator {

  override def traceRay(scene: Scene, inRay: Ray): Spectrum = {

    var ray = inRay
    var done = false
    var lightMask = Spectrum.WHITE
    var totalLight = Spectrum.BLACK

    while (!done) {
      val isect = scene.intersect(ray).orNull

      if (isect != null) {
        val dg = isect.dg

        // Emitter sample
        val bsdf = isect.getBSDF()
        val n = bsdf.dgShading.nn
        val p = bsdf.dgShading.p
        var i = 0

        while (i < scene.lights.length) {
          val (lightIntensity, wi, lightDist) = scene.lights(i).sample(p)
          val lightValue = lightIntensity * Math.abs(wi.dot(n)) * bsdf(-ray.dir, wi, BSDF.ALL_REFLECTION)

          val lightCheck = scene.intersect(Ray(p + wi*1e-5, wi, 0)).orNull
          if (lightCheck == null || lightCheck.time >= lightDist) {
            totalLight += lightValue
          }

          i += 1
        }

        // Reflect
        val (col, wi) = bsdf.sample(-ray.dir, 0, 0, BSDF.ALL_REFLECTION)
        lightMask *= (col * wi.dot(dg.nn).abs).clamp()

        ray = Ray(dg.p + wi*1e-9, wi, ray.depth+1)
        if (lightMask.isBlack(0.001)) {
          done = true
        }
      } else {
        done = true
      }
    }

    totalLight
  }
}
