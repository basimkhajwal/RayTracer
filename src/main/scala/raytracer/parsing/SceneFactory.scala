package raytracer.parsing

import raytracer._
import raytracer.cameras.{Camera, OrthographicCamera, PerspectiveCamera}
import raytracer.films.{Film, ImageFilm, ScreenFilm}
import raytracer.filters._
import raytracer.integrators.{Integrator, Whitted}
import raytracer.lights.{Light, PointLight, SpotLight}
import raytracer.materials._
import raytracer.math._
import raytracer.primitives.{Aggregate, GridAccelerator, Primitive}
import raytracer.renderers.{Renderer, SamplerRenderer}
import raytracer.sampling.{RandomSampler, Sampler}
import raytracer.shapes.{Shape, Sphere, Triangle, TriangleMesh}
import raytracer.textures._

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
        val (xs, xe, ys, ye) = camera.film.sampleExtent

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

  def makeFilter(filterType: String, params: ParamSet): Filter = reportUnused(params) {

    val defaultWidth = if (filterType == "box") 0.5 else 2
    val xWidth = params.getOneOr[Double]("xwidth", defaultWidth)
    val yWidth = params.getOneOr[Double]("ywidth", defaultWidth)

    filterType match {

      case "box" => new BoxFilter(xWidth, yWidth)

      case "triangle" => new TriangleFilter(xWidth, yWidth)

      case "mitchell" => {
        val B = params.getOneOr[Double]("B", 1/3.0)
        val C = params.getOneOr[Double]("C", 1/3.0)

        new MitchellFilter(xWidth, yWidth, B, C)
      }

      case "sinc" => {
        val tau = params.getOneOr[Double]("tau", params.getOneOr[Int]("tau", 3))

        new LanczosFilter(xWidth, yWidth, tau)
      }

      case "gaussian" => {
        val alpha = params.getOneOr[Double]("alpha", 2)
        new GaussianFilter(xWidth, yWidth, alpha)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented filter type $filterType")
    }
  }

  def makeFilm(filmType: String, filter: Filter, params: ParamSet): Film = reportUnused(params) {
    val xRes = params.getOneOr[Double]("xresolution", params.getOneOr[Int]("xresolution", 640)).toInt
    val yRes = params.getOneOr[Double]("yresolution", params.getOneOr[Int]("yresolution", 480)).toInt

    val cropWindowArr = params.get[Double]("cropwindow").map(_.toArray).orNull

    val cropWindow: (Double, Double, Double, Double) =
      if (cropWindowArr == null) (0, 1, 0, 1)
      else {
        assert(cropWindowArr.size == 4, "Four values needed for crop window!")
        (cropWindowArr(0), cropWindowArr(1), cropWindowArr(2), cropWindowArr(3))
      }

    filmType match {

      case "image" => {
        val fName = params.getOneOr[String]("filename", "default.png")

        new ImageFilm(filter, xRes, yRes, cropWindow, fName)
      }

      case "screen" => {
        val width = params.getOneOr[Double]("width", params.getOneOr[Int]("width", xRes)).toInt
        val height = params.getOneOr[Double]("height", params.getOneOr[Int]("height", yRes)).toInt

        new ScreenFilm(filter, xRes, yRes, cropWindow, width, height)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented film type $filmType")
    }
  }

  def makeCamera(camType: String, camToWorld: Transform, film: Film, params: ParamSet): Camera = reportUnused(params) {

    val aspRatio: Double = params.getOneOr[Double]("frameaspectratio", film.xResolution.toDouble/film.yResolution)

    val sw: Array[Double] = params.getOr[Double]("screenwindow",
      if (aspRatio > 1) Array(-aspRatio, aspRatio, -1, 1) else Array(-1, 1, -1/aspRatio, 1/aspRatio))
      .toArray

    val lensRadius = params.getOneOr[Double]("lensradius", 0)
    val focalDistance = params.getOneOr[Double]("focaldistance", 1e30)

    camType match {

      case "perspective" => {

        val fov: Double = params.getOneOr[Double]("fov", params.getOneOr[Int]("fov", 90))

        new PerspectiveCamera(camToWorld, (sw(0), sw(1), sw(2), sw(3)), fov, lensRadius, focalDistance, film)
      }

      case "orthographic" => {
        new OrthographicCamera(camToWorld, (sw(0), sw(1), sw(2), sw(3)), lensRadius, focalDistance, film)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented camera type $camType")
    }
  }

  def makeLightSource(lightType: String, lightToWorld: Transform, params: ParamSet): Light = reportUnused(params) {
    val scale = params.getOneOr[Spectrum]("scale", Spectrum.WHITE)

    lightType match {

      case "point" => {
        val intensity = params.getOneOr[Spectrum]("i", Spectrum.WHITE)
        val from = params.getOneOr[Point]("from", Point.ZERO)
        val l2w = Transform.translate(from.x, from.y, from.z) * lightToWorld

        PointLight(l2w, intensity * scale)
      }

      case "spot" => {
        val intensity = params.getOneOr[Spectrum]("i", Spectrum.WHITE)
        val from = params.getOneOr[Point]("from", Point.ZERO)
        val to = params.getOneOr[Point]("to", Point(0,0,1))
        val coneangle = params.getOneOr[Double]("coneangle", params.getOneOr[Int]("coneangle", 30))
        val conedelta = params.getOneOr[Double]("conedeltaangle", params.getOneOr[Int]("conedeltaangle", 5))
        val dir = (to - from).nor
        val coords = Vec3.createCoordinateSystem(dir)
        val du = coords._1
        val dv = coords._2

        val dirToZ =
          Transform(Mat4(Array(
            du.x,  du.y,  du.z, 0,
            dv.x,  dv.y,  dv.z, 0,
            dir.x, dir.y, dir.z, 0,
            0, 0, 0, 1
          )))

        val l2w = lightToWorld * Transform.translate(from.x, from.y, from.z) * dirToZ.inverse
        new SpotLight(l2w, intensity * scale, coneangle, coneangle-conedelta)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented light source type $lightType")
    }
  }

  def makeMaterial(matType: String, textureParams: TextureParams): Material = reportUnused(textureParams){
    matType match {

      case "matte" => {
        new MatteMaterial(textureParams.getSpectrumTexture("kd", Spectrum(0.5, 0.5, 0.5)))
      }

      case "mirror" => {
        new MirrorMaterial(textureParams.getSpectrumTexture("r", Spectrum.WHITE))
      }

      case "metal" => {
        new MetalMaterial(
          textureParams.getSpectrumTexture("eta", MetalMaterial.copperN),
          textureParams.getSpectrumTexture("k", MetalMaterial.copperK),
          textureParams.getFloatTexture("roughness", 0.05)
        )
      }

      case "plastic" => {
        new PlasticMaterial(
          textureParams.getSpectrumTexture("kd", Spectrum(0.25, 0.25, 0.25)),
          textureParams.getSpectrumTexture("ks", Spectrum(0.25, 0.25, 0.25)),
          textureParams.getFloatTexture("roughness", 0.1)
        )
      }

      case "glass" => {
        new GlassMaterial(
          textureParams.getSpectrumTexture("kr", Spectrum.WHITE),
          textureParams.getSpectrumTexture("kd", Spectrum.WHITE),
          textureParams.getFloatTexture("index", 1.5)
        )
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented material type $matType")
    }
  }

  def make2DMapping(mapType: String, t2w: Transform, params: TextureParams): TextureMapping2D = {
    mapType match {

      case "uv" => {
        val su = params.getOneOr("uscale", 1.0)
        val sv = params.getOneOr("vscale", 1.0)
        val du = params.getOneOr("udelta", 0.0)
        val dv = params.getOneOr("vdelta", 0.0)

        new UVMapping2D(su, sv, du, dv)
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented film type $mapType")
    }
  }

  def makeSpectrumTexture(textureClass: String, t2w: Transform, params: TextureParams): Texture[Spectrum] = reportUnused(params){
    textureClass match {
      case "constant" => {
        new ConstantTexture[Spectrum](params.getOneOr("value", Spectrum(0.5, 0.5, 0.5)))
      }

      case "checkerboard" => {
        new Checkerboard[Spectrum](
          make2DMapping(params.getOneOr("mapping", "uv"), t2w, params),
          params.getSpectrumTexture("tex1", Spectrum.BLACK),
          params.getSpectrumTexture("tex2", Spectrum.WHITE),
          AAMethod.fromString(params.getOneOr[String]("aamode", "closedform"))
        )
      }

      case "imagemap" => {
        val mapping = make2DMapping(params.getOneOr("mapping", "uv"), t2w, params)
        val texInfo = TextureInfo(
          params.getOne[String]("filename").getOrElse(throw new NotImplementedError("File name is required")),
          params.getOneOr("trilinear", false),
          params.getOneOr("maxanisotropy", 8.0),
          ImageWrap.fromString(params.getOneOr("wrap", "repeat")),
          params.getOneOr("scale", 1.0),
          params.getOneOr("gamma", 1.0)
        )
        new ImageSpectrumTexture(mapping, texInfo)
      }
      case _ => throw new IllegalArgumentException(s"Unknown spectrum texture class $textureClass")
    }
  }

  def makeFloatTexture(textureClass: String, t2w: Transform, params: TextureParams): Texture[Double] = reportUnused(params){
    textureClass match {
      case "constant" => {
        new ConstantTexture[Double](params.getOneOr("value", 1.0))
      }

      case "checkerboard" => {
        new Checkerboard[Double](
          make2DMapping(params.getOneOr("mapping", "uv"), t2w, params),
          params.getFloatTexture("tex1", 0),
          params.getFloatTexture("tex2", 1),
          AAMethod.fromString(params.getOneOr[String]("aamode", "closedform"))
        )
      }

      case "imagemap" => {
        val mapping = make2DMapping(params.getOneOr("mapping", "uv"), t2w, params)
        val texInfo = TextureInfo(
          params.getOne[String]("filename").getOrElse(throw new NotImplementedError("File name is required")),
          params.getOneOr("trilinear", false),
          params.getOneOr("maxanisotropy", 8.0),
          ImageWrap.fromString(params.getOneOr("wrap", "repeat")),
          params.getOneOr("scale", 1.0),
          params.getOneOr("gamma", 1.0)
        )
        new ImageFloatTexture(mapping, texInfo)
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

        val normals = params.get[Normal]("N").map(_.toArray).orNull
        val uvs = params.get[Double]("uv").map(_.toArray).orNull

        if (normals != null)
          require(normals.length == indices.length, s"Incorrect number of normals ${normals.length}")

        new TriangleMesh(indices.toArray, points.toArray, objToWorld, normals, uvs)
      }

      case _ => throw new IllegalArgumentException(s"Unimplemented shape type $name")
    }
  }
}
