package raytracer.parsing

import raytracer.Logger
import raytracer.math.{Point, Transform, Vec3}
import raytracer.primitives.Primitive

/**
  * Created by Basim on 27/01/2017.
  */
class SceneBuilder {

  private var transformStack: List[Transform] = Transform.identity :: Nil

  private var graphicsStateStack: List[GraphicsState] = new GraphicsState :: Nil

  private var worldSection = false

  private var primitives: List[Primitive] = Nil

  @inline
  final def currentTransform: Transform = transformStack head

  @inline
  final def graphicsState: GraphicsState = graphicsStateStack head

  private final def pushTransform: Unit = transformStack ::= transformStack.head

  private final def popTransform: Unit = transformStack = transformStack.tail

  private final def log(msg: String): Unit = Logger.info.log(msg)

  /* --------- PUBLIC METHODS ------------------ */

  final def getPrimitives: List[Primitive] = primitives

  final def applyTransform(t: Transform): Unit = setTransform(transformStack.head * t)

  final def setTransform(t: Transform): Unit = transformStack = t :: transformStack.tail

  final def worldBegin(): Unit = {
    worldSection = true
  }

  final def worldEnd(): Unit = {
    worldSection = false
  }

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
}
