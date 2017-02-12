package raytracer.parsing

import scala.collection.mutable
import scala.reflect.ClassTag

/**
  * Created by Basim on 28/01/2017.
  */
class ParamSet {
  private val allParams: mutable.Map[String, Seq[Any]] = mutable.Map.empty

  def add[T](paramName: String, paramValue: Seq[T]): Unit = {
    require(paramValue.length > 0, "Empty parameter value passed!")
    allParams += (paramName.toLowerCase -> paramValue)
  }

  final def optionTry[T: ClassTag](block: Seq[Any]): Option[Seq[T]] = {
    try {
      val values = block.map(_ match { case e: T => e })
      Some(values)
    } catch { case e: RuntimeException => {
      println(e.toString)
      None
    } }
  }

  def get[T : ClassTag](paramName: String): Option[Seq[T]] = {
    allParams.get(paramName.toLowerCase).flatMap(p => optionTry[T](p))
  }

  def getOr[T: ClassTag](paramName: String, default: Seq[T]): Seq[T] = get[T](paramName) getOrElse default

  def getOne[T: ClassTag](paramName: String): Option[T] = get[T](paramName) map (_.head)

  def getOneOr[T: ClassTag](paramName: String, default: T): T = getOne[T](paramName) getOrElse default
}

object ParamSet {

  def from(xs: (String, Seq[Any])*): ParamSet = new ParamSet {
    xs foreach { x => add(x._1, x._2) }
  }

}