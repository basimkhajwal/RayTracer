package raytracer.utils

import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}

/**
  * Created by Basim on 19/03/2017.
  */
class Reporter(val name: String) {

  private val time = new AtomicLong(0)
  private val before = new AtomicLong(0)
  private val count = new AtomicInteger(0)

  def start(): Unit = {
    before.set(System.currentTimeMillis())
    report()
  }

  def report(): Unit = count.incrementAndGet()

  def stop(): Unit = time.addAndGet(System.currentTimeMillis()-before.get())

  def getTime() = time.get()

  def getCount() = count.get()
}

object Reporter {

  val render = new Reporter("Render")

  private val allReporters = List(render)

  def outputReport(): Unit = {
    println()
    println("\t\tReport Results")
    println()
    printf("%-10s %-15s %-15s\n", "Name", "Call Count", "Time Spent(ms)")
    allReporters foreach (r =>
      printf("%-10s %-15s %-15s \n", r.name, r.getCount().toString, r.getTime().toString))
  }
}
