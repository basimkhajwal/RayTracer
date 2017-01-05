package raytracer

/**
  * Created by Basim on 15/12/2016.
  */






case class PointLight(val pos: Vec3, val colour: Spectrum)

trait Intersection
object Miss extends Intersection
case class Hit(val t: Double, val point: Vec3, val normal: Vec3, val colour: Spectrum) extends Intersection
