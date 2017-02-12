package raytracer.parsing

import scala.collection.mutable
import scala.util.Try

/**
  * Created by Basim on 28/01/2017.
  */
class ParamSet {
  private val allParams: mutable.Map[String, Seq[Any]] = mutable.Map.empty

  def add[T](paramName: String, paramValue: Seq[T]): Unit = {
    require(paramValue.length > 0, "Empty parameter value passed!")
    allParams += (paramName.toLowerCase -> paramValue)
  }

  def get[T](paramName: String): Option[Seq[T]] = {
    allParams.get(paramName.toLowerCase).flatMap(p => Try(p.asInstanceOf[Seq[T]]).toOption)
  }

  def getOr[T](paramName: String, default: Seq[T]): Seq[T] = get(paramName) getOrElse default

  def getOne[T](paramName: String): Option[T] = get(paramName) map (_.head)

  def getOneOr[T](paramName: String, default: T): T = getOne(paramName) getOrElse default
}

object ParamSet {

  def from(xs: (String, Seq[Any])*): ParamSet = new ParamSet {
    xs foreach { x => add(x._1, x._2) }
  }

}