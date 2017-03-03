package raytracer

/**
  * Created by Basim on 15/02/2017.
  */
trait Logger {
  def log(caller: String, msg: String): Unit
}

class OutputLogger(val name: String) extends Logger {
  def log(caller: String, msg: String) = printf("%-10s %-15s %s\n", name, caller, msg)
}

object Logger {
  val info = new OutputLogger("INFO")
  val default = new OutputLogger("DEFAULT")
  val warning = new OutputLogger("WARNING")
  val error = new OutputLogger("ERROR")
}
