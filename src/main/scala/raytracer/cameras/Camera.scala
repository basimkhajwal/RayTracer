package raytracer.cameras

import raytracer.films.Film
import raytracer.math.Ray
import raytracer.sampling.CameraSample

/**
  * Created by Basim on 24/01/2017.
  */
abstract class Camera(val film: Film) {
  def generateRay(sample: CameraSample): Ray
}
