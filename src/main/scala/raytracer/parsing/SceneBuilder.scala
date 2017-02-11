package raytracer.parsing

import raytracer.{ConstantTexture, Material, Spectrum, Texture}
import raytracer.math.{Point, Transform, Vec3}
import raytracer.shapes.{Shape, Sphere, Triangle}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by Basim on 27/01/2017.
  */
class SceneBuilder {

  private var transformStack: List[Transform] = Transform.identity :: Nil

  private var graphicsStateStack: List[GraphicsState] = new GraphicsState :: Nil

  private var worldSection = false

  private val shapes = new ListBuffer[Shape]()

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
    name.toLowerCase match {

      case "sphere" => {
        val radius = params.getOneOr[Int]("radius", 1)

        shapes append Sphere(radius, currentTransform(Point.ZERO))
      }

      case "trianglemesh" => {

        val indices = params.get[Int]("indices")
          .getOrElse(throw new IllegalArgumentException("Indices parameter required for triangle mesh"))

        val points = params.get[Point]("P")
          .getOrElse(throw new IllegalArgumentException("Point (P) parameter required for triangle mesh"))

        require(indices.length % 3 == 0, "Indices must specify 3 points for each triangle")

        indices grouped(3) foreach { t =>
          shapes append Triangle(points(t(0)), points(t(1)), points(t(2)), Spectrum.WHITE)
        }
      }

      case _ => throw new IllegalArgumentException(s"Shape type $name not recognised")
    }
  }

  private final def makeSpectrumTexture(textureClass: String, params: TextureParams): Texture[Spectrum] = {
    textureClass match {
      case "constant" => {
        new ConstantTexture[Spectrum](params.getOneOr("value", Spectrum.WHITE))
      }
      case _ => throw new IllegalArgumentException(s"Unknown spectrum texture class $textureClass")
    }
  }

  private final def makeFloatTexture(textureClass: String, params: TextureParams): Texture[Double] = {
    textureClass match {
      case "constant" => {
        new ConstantTexture[Double](params.getOneOr("value", 1.0))
      }
      case _ => throw new IllegalArgumentException(s"Unknown float texture class $textureClass")
    }
  }

  final def texture(name: String, textureType: String, textureClass: String, params: ParamSet): Unit = {

    val textureParams = TextureParams(params, params, graphicsState.floatTextures, graphicsState.spectrumTextures)

    if (textureType == "float") {
      val tex = makeFloatTexture(textureClass, textureParams)
      graphicsState.floatTextures(name) = tex

    } else {
      require(textureClass == "spectrum" || textureClass == "color", s"Unknown texture type $textureType")

      val tex = makeSpectrumTexture(textureClass, textureParams)
      graphicsState.spectrumTextures(name) = tex
    }
  }

  final def material(name: String, params: ParamSet): Unit = {

  }
}
