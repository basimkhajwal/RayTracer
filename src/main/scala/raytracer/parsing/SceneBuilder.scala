package raytracer.parsing

import raytracer.Logger
import raytracer.cameras.Camera
import raytracer.films.Film
import raytracer.lights.Light
import raytracer.math.{Point, Transform, Vec3}
import raytracer.primitives.Primitive

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

  private lazy val film: Film = SceneFactory.makeFilm(filmName, filmParams)

  private lazy val camera: Camera = SceneFactory.makeCamera(cameraName, cameraToWorld, film, cameraParams)

  /* --------------------- Utility Methods --------------------------- */

  @inline
  private final def currentTransform: Transform = transformStack head

  @inline
  private final def graphicsState: GraphicsState = graphicsStateStack head

  private final def pushTransform: Unit = transformStack ::= transformStack.head

  private final def popTransform: Unit = transformStack = transformStack.tail

  private final def log(msg: String): Unit = Logger.info.log(msg)

  /* --------------------- Public Methods --------------------------- */

  final def getPrimitives: List[Primitive] = primitives

  final def getLights: List[Light] = lights

  final def getFilm: Film = film

  final def getCamera: Camera = camera

  final def worldBegin(): Unit = {
    require(!worldSection, "World begin cannot be nested")
    identityTransform()
    worldSection = true
  }

  final def worldEnd(): Unit = {
    require(worldSection, "Un-matched world end")
    worldSection = false
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
    primitives ::= new Primitive(shape, mat)

    log(s"Added primitive of shape type $name")
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
