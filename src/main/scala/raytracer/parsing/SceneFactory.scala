package raytracer.parsing

import raytracer._
import raytracer.cameras.{Camera, OrthographicCamera, PerspectiveCamera}
import raytracer.films.{Film, ImageFilm, ScreenFilm}
import raytracer.integrators.{Integrator, Whitted}
import raytracer.lights.{Light, PointLight}
import raytracer.materials.{Material, MatteMaterial}
import raytracer.math.{Point, Transform}
import raytracer.primitives.{Aggregate, GridAccelerator, Primitive}
import raytracer.renderers.{Renderer, SamplerRenderer}
import raytracer.sampling.{RandomSampler, Sampler}
import raytracer.shapes.{Shape, Sphere, Triangle, TriangleMesh}
import raytracer.textures.{ConstantTexture, Texture}

/**
  * Created by Basim on 12/02/2017.
  */
object SceneFactory {

  private def reportUnused[T](tp: TextureParams)(b: => T): T = {
    val returnValue = b
    tp.reportUnused
    returnValue
  }

  private def reportUnused[T](ps: ParamSet)(b: => T): T = {
    val returnValue = b
    ps.reportUnused
    returnValue
  }

  def makeAccelerator(accelType: String, primitives: Array[Primitive], params: ParamSet): Primitive = reportUnused(params) {

    accelType match {

      case "none" => new Aggregate(primitives)

      case "grid" => new GridAccelerator(primitives)

      case _ => throw new IllegalArgumentException(s"Un-implemented accelerator type $accelType")
    }
  }

  def makeIntegrator(integratorType: String, params: ParamSet): Integrator = reportUnused(params) {
    integratorType match {

      case "whitted" => {
        val maxDepth = params.getOneOr[Int]("maxdepth", 3)
        new Whitted(maxDepth)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented integrator type $integratorType")
    }
  }

  def makeSampler(samplerType: String, params: ParamSet, camera: Camera): Sampler = reportUnused(params) {
    samplerType match {

      case "random" => {
        val samplesPerPixel = params.getOneOr[Int]("pixelsamples", 4)
        val (xs, xe, ys, ye) = camera.film.getSampleExtent()

        new RandomSampler(xs, xe, ys, ye, samplesPerPixel)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented sampler type $samplerType")
    }
  }

  def makeRenderer(
    rendererType: String, params: ParamSet,
    sampler: Sampler, camera: Camera, integrator: Integrator
  ): Renderer = reportUnused(params) {
    rendererType match {

      case "sampler" => {
        val taskCount = params.getOneOr[Int]("taskcount", 10)
        new SamplerRenderer(sampler, camera, integrator, taskCount)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented renderer type $rendererType")
    }
  }

  def makeFilm(filmType: String, params: ParamSet): Film = reportUnused(params) {
    val xRes = params.getOneOr[Double]("xresolution", params.getOneOr[Int]("xresolution", 640)).toInt
    val yRes = params.getOneOr[Double]("yresolution", params.getOneOr[Int]("yresolution", 480)).toInt

    filmType match {

      case "image" => {
        val fName = params.getOneOr[String]("filename", "default.png")

        new ImageFilm(fName, xRes, yRes)
      }

      case "screen" => {
        val width = params.getOneOr[Double]("width", params.getOneOr[Int]("width", xRes)).toInt
        val height = params.getOneOr[Double]("height", params.getOneOr[Int]("height", yRes)).toInt

        new ScreenFilm(xRes, yRes, width, height)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented film type $filmType")
    }
  }

  def makeCamera(camType: String, camToWorld: Transform, film: Film, params: ParamSet): Camera = reportUnused(params) {

    val aspRatio: Double = params.getOneOr[Double]("frameaspectratio", film.xResolution.toDouble/film.yResolution)

    val sw: Array[Double] = params.getOr[Double]("screenwindow",
      if (aspRatio > 1) Array(-aspRatio, aspRatio, -1, 1) else Array(-1, 1, -1/aspRatio, 1/aspRatio))
      .toArray

    camType match {

      case "perspective" => {

        val fov: Double = params.getOneOr[Double]("fov", params.getOneOr[Int]("fov", 90))

        new PerspectiveCamera(camToWorld, (sw(0), sw(1), sw(2), sw(3)), fov, film)
      }

      case "orthographic" => {
        new OrthographicCamera(camToWorld, (sw(0), sw(1), sw(2), sw(3)), film)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented camera type $camType")
    }
  }

  def makeLightSource(lightType: String, lightToWorld: Transform, params: ParamSet): Light = reportUnused(params) {
    lightType match {

      case "point" => {
        val intensity = params.getOneOr[Spectrum]("i", Spectrum.WHITE)
        val scale = params.getOneOr[Spectrum]("scale", Spectrum.WHITE)
        val from = params.getOneOr[Point]("from", Point.ZERO)
        val l2w = Transform.translate(from.x, from.y, from.z) * lightToWorld

        PointLight(l2w, intensity * scale)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented light source type $lightType")
    }
  }

  def makeMaterial(matType: String, textureParams: TextureParams): Material = reportUnused(textureParams){
    matType match {

      case "matte" => {
        new MatteMaterial(textureParams.getSpectrumTexture("kd", Spectrum(0.5, 0.5, 0.5)))
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented material type $matType")
    }
  }

  def makeSpectrumTexture(textureClass: String, params: TextureParams): Texture[Spectrum] = reportUnused(params){
    textureClass match {
      case "constant" => {
        new ConstantTexture[Spectrum](params.getOneOr("value", Spectrum(0.5, 0.5, 0.5)))
      }
      case _ => throw new IllegalArgumentException(s"Unknown spectrum texture class $textureClass")
    }
  }

  def makeFloatTexture(textureClass: String, params: TextureParams): Texture[Double] = reportUnused(params){
    textureClass match {
      case "constant" => {
        new ConstantTexture[Double](params.getOneOr("value", 1.0))
      }
      case _ => throw new IllegalArgumentException(s"Unknown float texture class $textureClass")
    }
  }

  def makeShape(name: String, objToWorld: Transform, params: ParamSet): Shape = reportUnused(params){
    name match {

      case "sphere" => {
        val radius = params.getOneOr[Double]("radius", 1)

        Sphere(radius, objToWorld)
      }

      case "trianglemesh" => {

        val indices = params.get[Int]("indices")
          .getOrElse(throw new IllegalArgumentException("Indices parameter required for triangle mesh"))

        val points = params.get[Point]("P")
          .getOrElse(throw new IllegalArgumentException("Point (P) parameter required for triangle mesh"))

        require(indices.length % 3 == 0, "Indices must specify 3 points for each triangle")

        TriangleMesh(indices.toArray, points.toArray, objToWorld)
      }

      case _ => throw new IllegalArgumentException(s"Unimplemented shape type $name")
    }
  }
}
