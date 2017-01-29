package raytracer.parsing

import raytracer.math.{Mat4, Point, Transform, Vec3}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.util.{Success, Try}

/**
  * Created by Basim on 17/01/2017.
  */
class SceneParser(sceneFile: String) extends SceneBuilder {

  private val validParameters = List("float", "integer", "string", "bool", "vector", "point", "rgb")

  private var lexerStack: List[Lexer] = new Lexer(sceneFile) :: Nil

  @inline
  private def tokens = lexerStack head

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

  private def catchError(f: => Unit): Unit = {
    try(f) catch {
      case e: AssertionError => throwError("Assertion failed:\t" + e.getMessage)
      case e: IllegalArgumentException => throwError("Invalid argument:\t" + e.getMessage)
      case e => throwError(e.toString)
    }
  }

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

  private def parseParameter(params: ParamSet, paramName: String, paramType: String, paramValues: ListBuffer[String]): Unit = {

    def checkedMap[T:ClassTag](err: String => String, f: String => T): Seq[T] = {
      paramValues.map(value => Try(f(value)) match {
        case Success(out: T) => out
        case _ => throwError(err(value))
      })
    }

    def mapAndAdd[T:ClassTag](err: String => String, f: String => T): Unit = {
      params.add(paramName, checkedMap(err, f))
    }

    paramType match {
      case "float" => mapAndAdd(_ + " is not a valid float", _.toDouble)
      case "integer" => mapAndAdd(_ + " is not a valid integer", _.toDouble.toInt)
      case "string" => mapAndAdd[String](_, _)

      case "bool" => mapAndAdd(_ + " is not a valid boolean", _ match {
        case "true" => true
        case "false" => false
        case _ => ???
      })

      case "vector" => {
        val elements = checkedMap(_ + "is not a valid vector part", _.toDouble)
        if (elements.length % 3 != 0) throwError("Invalid parts, 3 doubles per vector required")
        params.add(paramName, elements.grouped(3).map(p => Vec3(p(0), p(1), p(2))).toSeq)
      }

      case "point" => {
        val elements = checkedMap(_ + "is not a valid point part", _.toDouble)
        if (elements.length % 3 != 0) throwError("Invalid parts, 3 doubles per point required")
        params.add(paramName, elements.grouped(3).map(p => Point(p(0), p(1), p(2))).toSeq)
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
  private final def getParamValues(acc: ListBuffer[String]): ListBuffer[String] = {
    nextToken() match {
      case None => throwError("Parameter list must end in a ]")
      case Some("]")  => acc
      case Some(token) => {
        acc append token
        getParamValues(acc)
      }
    }
  }

  private def parseParams(): ParamSet = {
    var done = false
    val params = new ParamSet

    while (!done) {
      done = true
      for {
        paramHeader <- tokens.peek()
        (pType, pName) <- isValidHeader(paramHeader)
      } {
        nextToken()
        done = false
        parseParameter(params, pType, pName, getParamValues)
      }
    }

    params
  }

  def parse: Unit = {
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

  private final def parseWorld: Unit = {
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

      }

      case "attributeend" => {

      }

      case "shape" => {
        val shapeType = nextToken().getOrElse(throwError("Shape type not specified"))
        shape(shapeType, parseParams())
      }

      case token if parseTransform(token) =>

      case token => throwError(s"Identifier $token not recognised")
    }
  }

  private def parseInclude: Unit = {
    nextToken match {
      case None => throwError("Include directive requires a file name to be specified")
      case Some(file) => lexerStack ::= new Lexer(file)
    }
  }

  def parseTransform(token: String): Boolean = {
    var matched = true

    token.toLowerCase match {

      case "identity" => identityTransform()

      case "translate" => {
        val ps = getNumbers(3)
        translateTransform(ps(0), ps(1), ps(2))
      }

      case "rotate" => {
        val ps = getNumbers(4)
        rotateTransform(ps(0), ps(1), ps(2), ps(3))
      }

      case "lookat" => {
        val ps = getNumbers(9)
        lookAtTransform(
          Point(ps(0), ps(1), ps(2)),
          Point(ps(3), ps(4), ps(5)),
          Vec3(ps(6), ps(7), ps(8)))
      }

      case "transform" => {
        val ps = getNumbers(16)
        setTransform(Transform(Mat4(ps toArray).transpose))
      }

      case "concattransform" => {
        val ps = getNumbers(16)
        applyTransform(Transform(Mat4(ps toArray).transpose))
      }

      case "coordinatesystem" => {
        nextToken() match {
          case Some(name) => coordinateSystem(name)
          case None => throwError("Name needed for coordinate system")
        }
      }

      case "coordsystransform" => {
        nextToken() match {
          case Some(name) => catchError { coordSysTranform(name) }
          case None => throwError("Name not specified for coordinate system transform")
        }
      }

      case "transformbegin" => transformBegin()

      case "transformend" => transformEnd()

      case _ => matched = false
    }

    matched
  }

}
