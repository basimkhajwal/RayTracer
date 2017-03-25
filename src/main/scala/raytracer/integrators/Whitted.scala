package raytracer.integrators
import raytracer._
import raytracer.bxdf.BSDF
import raytracer.math.Ray
import raytracer.primitives.Intersection

/**
  * Created by Basim on 05/01/2017.
  */
class Whitted(maxRayDepth: Int) extends Integrator{

  override def traceRay(scene: Scene, ray: Ray): Spectrum = {

    val isect = scene.intersect(ray).orNull
    if (isect == null) return Spectrum.BLACK

    val bsdf = isect.getBSDF()
    val p = bsdf.dgShading.p
    val n = bsdf.dgShading.nn

    var directLight = Spectrum.BLACK
    var i = 0

    while (i < scene.lights.length) {
      val (lightIntensity, wi, lightDist) = scene.lights(i).sample(p)
      val lightValue = lightIntensity * Math.abs(wi.dot(n)) * bsdf(-ray.dir, wi, BSDF.ALL_REFLECTION)

      val lightCheck = scene.intersect(Ray(p, wi, 0)).orNull
      if (lightCheck == null || lightCheck.time >= lightDist) {
        directLight += lightValue
      }

      i += 1
    }

    if (ray.depth >= maxRayDepth) directLight
    else directLight + Integrator.specularReflect(scene, ray, isect, this)
  }
}
