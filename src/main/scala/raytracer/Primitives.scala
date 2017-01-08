package raytracer

import raytracer.math.Vec3

/**
  * Created by Basim on 15/12/2016.
  */
case class PointLight(val pos: Vec3, val colour: Spectrum)

case class Intersection(val t: Double, val point: Vec3, val normal: Vec3, val colour: Spectrum)
