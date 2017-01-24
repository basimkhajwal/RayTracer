package raytracer.cameras

import raytracer.math.Transform

/**
  * Created by Basim on 24/01/2017.
  */
abstract class ProjectiveCamera(
  val camToWorld: Transform,
  val proj: Transform,
  val screenWindow: (Double, Double, Double, Double)
 ) extends Camera {

  val

}
