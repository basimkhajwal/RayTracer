package raytracer.parsing

import java.util.Stack

import raytracer.math.{Point, Vec3}

import scala.annotation.tailrec

/**
  * Created by Basim on 17/01/2017.
  */
class SceneParser(sceneFile: String) {

  val lexerStack: Stack[Lexer] = new Stack[Lexer]()
  lexerStack push new Lexer(sceneFile)

  @inline
  private def tokens = lexerStack peek

  private def nextToken(): Option[String] = {
    if (lexerStack isEmpty) None
    else {
      val t = tokens next
      if (t.isDefined) t
      else {
        lexerStack pop()
        nextToken()
      }
    }
  }

  private def throwError(msg: String): Nothing = {
    val currentLexer = lexerStack peek
    throw new RuntimeException(
      "Error parsing " + currentLexer.fileName + " at line " + currentLexer.currentLine
      + "\n:" + msg
    )
  }

  sealed trait ParamT
  case class PFloat(x: Double) extends ParamT
  case class PInteger(x: Int) extends ParamT
  case class PString(str: String) extends ParamT
  case class PBool(b: Boolean) extends ParamT
  case class PVector(v: Vec3) extends ParamT
  case class PPoint(p: Point) extends ParamT

  type Params = Map[String, ParamT]

  private val validParameters = List("float", "integer", "string", "bool", "vector", "point", "rgb")

  private def isValidHeader(paramHeader: String): Option[(String, String)] = {
    val splitParts = paramHeader split(' ')
    if (splitParts.length != 2) None
    else {
      val paramType = splitParts(0).toLowerCase.trim
      val paramName = splitParts(1).toLowerCase.trim

      if (validParameters contains paramType) Some(paramType, paramName)
      else throwError("Unrecognised type " + paramType)
    }
  }

  private def parseParam(paramType: String): ParamT = paramType match {
    case "float" => {

    }
    case _ => throwError("Unimplmented type " + paramType)
  }

  @tailrec
  private def parseParams(acc: Params = Map.empty): Params = {
    tokens.peek() match {
      case None => acc
      case Some(paramHeader) => isValidHeader(paramHeader) match {
        case None => acc
        case Some(param) => parseParams(acc + param)
      }
    }
  }
}
