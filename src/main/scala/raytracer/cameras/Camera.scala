package raytracer.cameras

import raytracer.math.Ray

/**
  * Created by Basim on 24/01/2017.
  */
trait Camera {
  def generateRay(imageX: Double, imageY: Double): Ray
}
