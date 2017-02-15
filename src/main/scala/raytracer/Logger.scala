package raytracer

import java.util

/**
  * Created by Basim on 15/02/2017.
  */
trait Logger {
  def log(msg: String): Unit
}

class OutputLogger(val name: String) extends Logger {
  def log(msg: String) = printf("%-15s %s\n", name, msg)
}

object Logger {
  val info = new OutputLogger("INFO")
  val default = new OutputLogger("DEFAULT")
  val warning = new OutputLogger("WARNING")
  val error = new OutputLogger("ERROR")
}
