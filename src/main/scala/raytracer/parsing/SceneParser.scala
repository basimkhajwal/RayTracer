package raytracer.parsing

import raytracer.math.{Mat4, Point, Transform, Vec3}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.util.{Success, Try}

/**
  * Created by Basim on 17/01/2017.
  */
class SceneParser(sceneFile: String) {

  private val validParameters = List("float", "integer", "string", "bool", "vector", "point", "rgb")

  private var lexerStack: List[Lexer] = new Lexer(sceneFile) :: Nil

  private var transformStack: List[Transform] = Transform.identity :: Nil

  private val namedTransforms: mutable.Map[String, Transform] = mutable.Map()

  @inline
  private def tokens = lexerStack head

  @inline
  private def ctm: Transform = transformStack head

  private def applyTransform(t: Transform): Unit = setTransform(transformStack.head * t)

  private def setTransform(t: Transform): Unit = transformStack = t :: transformStack.tail

  private def pushTransform: Unit = transformStack ::= transformStack.head

  private def popTransform: Unit = transformStack = transformStack.tail

  @tailrec
  private def nextToken(): Option[String] = {
    if (lexerStack isEmpty) None
    else {
      tokens next match {
        case None => {
          lexerStack = lexerStack.tail
          nextToken()
        }
        case t => t
      }
    }
  }

  private def getTokens(n: Int): List[String] = {
    var i = 0
    val tk = ListBuffer[String]()
    while (i < n) {
      nextToken match {
        case Some(t) => tk append t
        case None => throwError(s"Error expected $n tokens but only got $i")
      }
      i += 1
    }
    tk toList
  }

  private def getNumbers(n: Int): List[Double] = {
    var i = 0
    val ns = ListBuffer[Double]()
    var bracketed = false

    while (i < n) {
      tokens.peek() match {
        case Some("[") => {
          bracketed = true
          nextToken()
        }
        case _ =>
      }
      nextToken match {
        case Some(t) => Try(ns append t.toDouble).getOrElse(throwError(s"Invalid numerical value $t"))
        case None => throwError(s"Error expected $n tokens but only got $i")
      }
      i += 1
    }
    if (bracketed) {
      nextToken() match {
        case Some("]") =>
        case _ => throwError("Bracketed statements must be properly closed!")
      }
    }
    ns toList
  }

  private def throwError(msg: String): Nothing = {
    val currentLexer = lexerStack.head
    throw new RuntimeException(
      "Error parsing " + currentLexer.fileName + " at line " + currentLexer.currentLine
      + ":\t" + msg
    )
  }

  sealed trait ParamT
  case class PFloat(x: Array[Double]) extends ParamT
  case class PInteger(x: Array[Int]) extends ParamT
  case class PString(str: Array[String]) extends ParamT
  case class PBool(b: Array[Boolean]) extends ParamT
  case class PVector(v: Array[Vec3]) extends ParamT
  case class PPoint(p: Array[Point]) extends ParamT

  type Params = Map[String, ParamT]

  private def isValidHeader(paramHeader: String): Option[(String, String)] = {
    val splitParts = paramHeader.split(' ')
    if (splitParts.length != 2) None
    else {
      val paramType = splitParts(0).toLowerCase.trim
      val paramName = splitParts(1).toLowerCase.trim

      if (validParameters contains paramType) Some(paramType, paramName)
      else throwError("Unrecognised type " + paramType)
    }
  }

  private def parseParam(paramType: String, paramValues: ListBuffer[String]): ParamT = {

    def checkedMap[T:ClassTag](err: String => String, f: String => T): Array[T] = {
      paramValues.map(value => Try(f(value)) match {
        case Success(out: T) => out
        case _ => throwError(err(value))
      }).toArray[T]
    }

    paramType match {
      case "float" => PFloat(checkedMap(_ + " is not a valid float", _.toDouble))
      case "integer" => PInteger(checkedMap(_ + " is not a valid integer", _.toDouble.toInt))
      case "string" => PString(paramValues toArray)

      case "bool" => PBool(checkedMap(_ + " is not a valid boolean", _ match {
        case "true" => true
        case "false" => false
        case _ => ???
      }))

      case "vector" => {
        val elements = checkedMap(_ + "is not a valid vector part", _.toDouble)
        if (elements.size % 3 != 0) throwError("Invalid parts, 3 doubles per vector required")

        PVector(elements grouped(3) map(p => Vec3(p(0), p(1), p(2))) toArray)
      }

      case "point" => {
        val elements = checkedMap(_ + "is not a valid point part", _.toDouble)
        if (elements.size % 3 != 0) throwError("Invalid parts, 3 doubles per point required")

        PPoint(elements grouped(3) map(p => Point(p(0), p(1), p(2))) toArray)
      }

      case _ => throwError("Unimplemented type " + paramType)
    }
  }

  private def getParamValues: ListBuffer[String] = {
    nextToken() match {
      case Some("[") => getParamValues(ListBuffer())
      case Some(single) => ListBuffer(single)
      case _ => throwError("Parameter list must begin with [")
    }
  }

  @tailrec
  private def getParamValues(acc: ListBuffer[String]): ListBuffer[String] = {
    nextToken() match {
      case None => throwError("Parameter list must end in a ]")
      case Some("]")  => acc
      case Some(token) => {
        acc append token
        getParamValues(acc)
      }
    }
  }

  @tailrec
  private def parseParams(acc: Params = Map.empty): Params = {
    tokens.peek() match {
      case None => acc
      case Some(paramHeader) => isValidHeader(paramHeader) match {
        case None => acc
        case Some((pType, pName)) => {
          nextToken()
          parseParams(acc +
            (pName -> parseParam(pType, getParamValues)))
        }
      }
    }
  }

  final def parse: Unit = {
    var t: Option[String] = None
    while ({
      t = nextToken()
      t.isDefined
    }) {
      t.get.toLowerCase match {
        case "worldbegin" => parseWorld

        case "include" => parseInclude

        case token if parseTransform(token) =>

        case _ =>
      }
    }
  }

  final def parseWorld: Unit = {
    var t: Option[String] = None
    var done = false

    setTransform(Transform.identity)

    while ({
    t = nextToken()
    !done && t.isDefined
    }) t.get.toLowerCase match {

      case "worldend" => done = true

      case "include" => parseInclude

      case "attributebegin" => {
        pushTransform
      }

      case "attributeend" => {
        popTransform
      }

      case "shape" => {
        val shapeType = nextToken().getOrElse(throwError("Shape type not specified"))
        parseShape(shapeType, parseParams())
      }

      case token if parseTransform(token) =>

      case token => throwError(s"Token $token not recognised")
    }
  }

  private def parseInclude: Unit = {
    nextToken match {
      case None => throwError("Include directive requires a file name to be specified")
      case Some(file) => lexerStack ::= new Lexer(file)
    }
  }

  private def parseShape(shape: String, params: Params): Unit = shape match {

    case "sphere" => {

    }

    case _ => throwError("Shape type " + shape + " not implemented")
  }

  def parseTransform(token: String): Boolean = token.toLowerCase match {

    case "identity" => {
      setTransform(Transform.identity)
      true
    }

    case "translate" => {
      val ps = getNumbers(3)
      applyTransform(Transform.translate(ps(0), ps(1), ps(2)))
      true
    }

    case "rotate" => {
      val ps = getNumbers(4)
      applyTransform(Transform.rotate(ps(0), Vec3(ps(1), ps(2), ps(3))))
      true
    }

    case "lookat" => {
      val ps = getNumbers(9)
      applyTransform(Transform.lookAt(
        Point(ps(0), ps(1), ps(2)),
        Point(ps(3), ps(4), ps(5)),
        Vec3(ps(6), ps(7), ps(8))))
      true
    }

    case "transform" => {
      val ps = getNumbers(16)
      setTransform(Transform(Mat4(ps toArray).transpose))
      true
    }

    case "concattransform" => {
      val ps = getNumbers(16)
      applyTransform(Transform(Mat4(ps toArray).transpose))
      true
    }

    case "coordinatesystem" => {
      nextToken() match {
        case Some(name) => namedTransforms.put(name, ctm)
        case None => throwError("Name needed for coordinate system")
      }
      true
    }

    case "coordsystransform" => {
      nextToken() match {
        case Some(name) => namedTransforms.get(name) match {
          case Some(t) => setTransform(t)
          case None => throwError(s"Coordinate system $name not defined")
        }
        case None => throwError("Name not specified for coordinate system transform")
      }
      true
    }

    case "transformbegin" => {
      pushTransform
      true
    }

    case "transformend" => {
      popTransform
      true
    }

    case _ => false
  }
}
