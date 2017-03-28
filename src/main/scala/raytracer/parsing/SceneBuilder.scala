package raytracer.parsing

import raytracer.Scene
import raytracer.cameras.Camera
import raytracer.films.Film
import raytracer.filters.Filter
import raytracer.integrators.Integrator
import raytracer.lights.Light
import raytracer.math.{Point, Transform, Vec3}
import raytracer.primitives.{Aggregate, GeometricPrimitive, GridAccelerator, Primitive}
import raytracer.renderers.Renderer
import raytracer.sampling.Sampler
import raytracer.utils.{Logger, Reporter}

/**
  * Created by Basim on 27/01/2017.
  */
class SceneBuilder {

  /* --------------------- STATE --------------------------- */

  private var transformStack: List[Transform] = Transform.identity :: Nil

  private var graphicsStateStack: List[GraphicsState] = new GraphicsState :: Nil

  private var worldSection = false

  private var primitives: List[Primitive] = Nil
  private var lights: List[Light] = Nil

  private var cameraName = "perspective"
  private var cameraParams = new ParamSet()
  private var cameraToWorld: Transform = Transform.identity

  private var filmName = "screen"
  private var filmParams = new ParamSet()

  private var filterName = "box"
  private var filterParams = new ParamSet()

  private var samplerName = "random"
  private var samplerParams = new ParamSet()

  private var integratorName = "whitted"
  private var integratorParams = new ParamSet()

  private var rendererName = "sampler"
  private var rendererParams = new ParamSet()

  private var acceleratorName = "grid"
  private var acceleratorParams = new ParamSet()

  private lazy val film: Film = SceneFactory.makeFilm(filmName, filter, filmParams)

  private lazy val filter: Filter = SceneFactory.makeFilter(filterName, filterParams)

  private lazy val camera: Camera = SceneFactory.makeCamera(cameraName, cameraToWorld, film, cameraParams)

  private lazy val sampler: Sampler = SceneFactory.makeSampler(samplerName, samplerParams, camera)

  private lazy val integrator: Integrator = SceneFactory.makeIntegrator(integratorName, integratorParams)

  private lazy val accelerator: Primitive =
    SceneFactory.makeAccelerator(acceleratorName, primitives.toArray, acceleratorParams)

  private lazy val renderer: Renderer =
    SceneFactory.makeRenderer(rendererName, rendererParams, sampler, camera, integrator)

  /* --------------------- Utility Methods --------------------------- */

  @inline
  private final def currentTransform: Transform = transformStack head

  @inline
  private final def graphicsState: GraphicsState = graphicsStateStack head

  private final def pushTransform: Unit = transformStack ::= transformStack.head

  private final def popTransform: Unit = transformStack = transformStack.tail

  private final def log(msg: String): Unit = Logger.info.log("SceneBuilder", msg)

  /* --------------------- Public Methods --------------------------- */

  final def render(): Unit = {
    renderer.render(new Scene(lights.toArray, accelerator))
  }

  final def getRenderer(): Renderer = renderer

  final def getPrimitives(): List[Primitive] = primitives

  final def getLights(): List[Light] = lights

  final def getFilm(): Film = film

  final def getCamera(): Camera = camera

  final def getSampler(): Sampler = sampler

  final def getFilter(): Filter = filter

  final def worldBegin(): Unit = {
    require(!worldSection, "World begin cannot be nested")
    identityTransform()
    worldSection = true
  }

  final def worldEnd(): Unit = {
    require(worldSection, "Un-matched world end")
    worldSection = false
    log(s"$renderer")
  }

  final def filter(filterType: String, params: ParamSet): Unit = {
    require(!worldSection, "The filter must be defined outside of the world section")
    filterName = filterType
    filterParams = params
    log(s"Set filter to type $filterType")
  }

  final def renderer(rendererType: String, params: ParamSet): Unit = {
    require(!worldSection, "The renderer must be defined outside of the world section")
    rendererName = rendererType
    rendererParams = params
    log(s"Set renderer to type $rendererName")
  }

  final def accelerator(acceleratorType: String, params: ParamSet): Unit = {
    require(!worldSection, "The accelerator must be defined outside of the world section")
    acceleratorName = acceleratorType
    acceleratorParams = params
    log(s"Set accelerator to type $rendererName")
  }

  final def sampler(samplerType: String, params: ParamSet): Unit = {
    require(!worldSection, "The sampler must be defined outside of the world section")
    samplerName = samplerType
    samplerParams = params
    log(s"Set sampler to type $samplerType")
  }

  final def integrator(integratorType: String, params: ParamSet): Unit = {
    require(!worldSection, "The integrator must be defined outside of the world section")
    integratorName = integratorType
    integratorParams = params
    log(s"Set integrator to type $integratorType")
  }

  final def camera(camType: String, params: ParamSet): Unit = {
    require(!worldSection, "Camera must be defined outside of the world section")
    cameraName = camType
    cameraParams = params
    cameraToWorld = currentTransform.inverse
    graphicsState.namedTransforms.put("camera", cameraToWorld)

    log(s"Set camera to $camType")
  }

  final def film(filmType: String, params: ParamSet): Unit = {
    require(!worldSection, "Films must be defined outside of the world section")
    filmName = filmType
    filmParams = params
    log(s"Set film to $filmType")
  }

  //<editor-fold desc="Transformation Methods">

  final def applyTransform(t: Transform): Unit = setTransform(transformStack.head * t)

  final def setTransform(t: Transform): Unit = transformStack = t :: transformStack.tail

  final def transformBegin(): Unit = {
    pushTransform
    log(s"Began transform")
  }

  final def transformEnd(): Unit = {
    assert(transformStack.size > 1, "Unmatched transform end!")
    popTransform
    log(s"Ended transform")
  }

  final def attributeBegin(): Unit = {
    log(s"Began attribute")
    transformBegin()
    graphicsStateStack ::= graphicsState.makeCopy()
  }

  final def attributeEnd(): Unit = {
    assert(graphicsStateStack.size > 1, "Unmatched attribute end!")
    graphicsStateStack = graphicsStateStack.tail
    transformEnd()
    log(s"Ended attribute")
  }

  final def identityTransform(): Unit = {
    setTransform(Transform.identity)
    log(s"Set transform to identity")
  }

  final def translateTransform(x: Double, y: Double, z: Double): Unit = {
    applyTransform(Transform.translate(x, y, z))
    log(s"Applied translation by ($x, $y, $z)")
  }

  final def rotateTransform(angle: Double, x: Double, y: Double, z: Double) = {
    applyTransform(Transform.rotate(angle, Vec3(x, y, z)))
    log(s"Applied rotation around ($x, $y, $z) of angle $angle")
  }

  final def lookAtTransform(from: Point, to: Point, up: Vec3) = {
    applyTransform(Transform.lookAt(from, to, up))
    log(s"Applied LookAt from $from to $to")
  }

  final def coordinateSystem(name: String): Unit = {
    graphicsState.namedTransforms.put(name, currentTransform)
    log(s"Added coordinate system transform $name")
  }

  final def coordSysTranform(name: String): Unit = {
    require(graphicsState.namedTransforms.isDefinedAt(name), s"Tranform $name is not defined")
    setTransform(graphicsState.namedTransforms(name))
    log(s"Set coordinate system transform to $name")
  }
  //</editor-fold>

  //<editor-fold desc="Public World Methods">

  final def lightSource(name: String, params: ParamSet): Unit = {
    require(worldSection, "Light sources can only be defined in the world section")

    val newLight = SceneFactory.makeLightSource(name, currentTransform, params)
    lights ::= newLight

    log(s"Added light of type $name")
  }

  final def shape(name: String, params: ParamSet): Unit = {
    require(worldSection, "Shapes can only be defined in the world section")

    val shape = SceneFactory.makeShape(name, currentTransform, params)
    val mat = graphicsState.createMaterial(params)
    primitives ::= new GeometricPrimitive(shape, mat)

    Reporter.primitive.report()
  }

  final def texture(name: String, textureType: String, textureClass: String, params: ParamSet): Unit = {
    require(worldSection, "Textures can only be defined in the world section")

    val textureParams = graphicsState.getTextureParams(params, params)

    if (textureType == "float") {
      val tex = SceneFactory.makeFloatTexture(textureClass, textureParams)
      graphicsState.floatTextures(name) = tex
    } else {
      require(textureClass == "spectrum" || textureClass == "color", s"Unknown texture type $textureType")

      val tex = SceneFactory.makeSpectrumTexture(textureClass, textureParams)
      graphicsState.spectrumTextures(name) = tex
    }

    log(s"Created texture '$textureType $textureClass' named $name ")
  }

  final def material(name: String, params: ParamSet): Unit = {
    require(worldSection, "Materials can only be defined in the world section")

    graphicsState.material = name
    graphicsState.namedMaterial = ""
    graphicsState.materialParams = params
    log(s"Set material to $name")
  }

  final def namedMaterial(name: String): Unit = {
    require(worldSection, "Materials can only be defined in the world section")
    graphicsState.namedMaterial = name
    log(s"Set named material to $name")
  }

  final def makeNamedMaterial(name: String, params: ParamSet): Unit = {
    require(worldSection, "Materials can only be defined in the world section")

    val tp = graphicsState.getTextureParams(params)

    val matType: String = tp.getOne("type")
      .getOrElse(throw new IllegalArgumentException("Parameter string type required in named material"))

    graphicsState.namedMaterials(name) = SceneFactory.makeMaterial(matType, tp)
    log(s"Made named material $name")
  }
  //</editor-fold>
}
