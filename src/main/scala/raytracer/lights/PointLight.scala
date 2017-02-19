package raytracer.lights
import raytracer.Spectrum
import raytracer.math.{Point, Transform, Vec3}

/**
  * Created by Basim on 18/02/2017.
  */
case class PointLight(lightToWorld: Transform, intensity: Spectrum) extends Light {

  val lightPoint = lightToWorld(Point.ZERO)

  override def sample(point: Point): (Spectrum, Vec3, Double) = {
    val lightVec = lightPoint - point
    (intensity, lightVec.nor, lightVec.mag)
  }
}
