package raytracer.parsing

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

  def getPrimitives: List[Primitive] = primitives

  @inline
  final def currentTransform: Transform = transformStack head

  @inline
  final def graphicsState: GraphicsState = graphicsStateStack head

  private final def pushTransform: Unit = transformStack ::= transformStack.head

  private final def popTransform: Unit = transformStack = transformStack.tail

  /* --------- PUBLIC METHODS ------------------ */

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
  }

  final def transformEnd(): Unit = {
    assert(transformStack.size > 1, "Unmatched transform end!")
    popTransform
  }

  final def attributeBegin(): Unit = {
    transformBegin()
    graphicsStateStack ::= new GraphicsState
  }

  final def attributeEnd(): Unit = {
    assert(graphicsStateStack.size > 1, "Unmatched attribute end!")
    transformEnd()
  }

  final def identityTransform(): Unit = {
    setTransform(Transform.identity)
  }

  final def translateTransform(x: Double, y: Double, z: Double): Unit = {
    applyTransform(Transform.translate(x, y, z))
  }

  final def rotateTransform(angle: Double, x: Double, y: Double, z: Double) = {
    applyTransform(Transform.rotate(angle, Vec3(x, y, z)))
  }

  final def lookAtTransform(from: Point, to: Point, up: Vec3) = {
    applyTransform(Transform.lookAt(from, to, up))
  }

  final def coordinateSystem(name: String): Unit = {
    graphicsState.namedTransforms.put(name, currentTransform)
  }

  final def coordSysTranform(name: String): Unit = {
    require(graphicsState.namedTransforms.isDefinedAt(name), s"Tranform $name is not defined")
    setTransform(graphicsState.namedTransforms(name))
  }

  final def shape(name: String, params: ParamSet): Unit = {
    val shape = SceneFactory.makeShape(name, currentTransform, params)
    val mat = graphicsState.createMaterial(params)
    primitives ::= new Primitive(shape, mat)
  }


  final def texture(name: String, textureType: String, textureClass: String, params: ParamSet): Unit = {

    val textureParams = graphicsState.getTextureParams(params, params)

    if (textureType == "float") {
      val tex = SceneFactory.makeFloatTexture(textureClass, textureParams)
      graphicsState.floatTextures(name) = tex

    } else {
      require(textureClass == "spectrum" || textureClass == "color", s"Unknown texture type $textureType")

      val tex = SceneFactory.makeSpectrumTexture(textureClass, textureParams)
      graphicsState.spectrumTextures(name) = tex
    }
  }

  final def material(name: String, params: ParamSet): Unit = {
    graphicsState.material = name
    graphicsState.namedMaterial = ""
    graphicsState.materialParams = params
  }

  final def namedMaterial(name: String): Unit = {
    graphicsState.namedMaterial = name
  }

  final def makeNamedMaterial(name: String, params: ParamSet): Unit = {

    val tp = graphicsState.getTextureParams(params)

    val matType: String = tp.getOne("type")
      .getOrElse(throw new IllegalArgumentException("Parameter string type required in named material"))

    graphicsState.namedMaterials(name) = SceneFactory.makeMaterial(matType, tp)
  }
}
