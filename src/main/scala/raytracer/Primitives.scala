package raytracer

import raytracer.math.{Point, Vec3}

/**
  * Created by Basim on 15/12/2016.
  */
case class PointLight(val pos: Point, val colour: Spectrum)

case class Intersection(val t: Double, val point: Point, val normal: Vec3, val colour: Spectrum)
