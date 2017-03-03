package raytracer.renderers

import raytracer.Scene

/**
  * Created by Basim on 03/03/2017.
  */
trait Renderer {

  def render(scene: Scene): Unit
}
