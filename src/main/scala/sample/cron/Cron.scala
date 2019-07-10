package sample.cron

import java.nio.file.{Files, Paths}

import scala.io.Source
import util.control.Breaks._

object Cron extends App {

  def checkTime(): Unit = {
    var elements: Array[String] = Array()
    var toRun: String = ""
    var time: String = ""
    var day: String = ""
    var timeIn: String = ""
    var config: String = ""
    var hours: String = ""
    var minutes: String = ""

    println("Please input current time in HH:MM format")
    timeIn = scala.io.StdIn.readLine()
    if (!timeIn.contains(":")) {
      println("Use the correct format")
      System.exit(1)
    }
    hours = timeIn.split(":")(0)
    minutes = timeIn.split(":")(1)
    if (hours.toInt > 23 || hours.toInt < 0 || minutes.toInt > 59 || minutes.toInt < 0) {
      println("Input a real time")
      System.exit(1)
    }

    println("Please input the config file path")
    config = scala.io.StdIn.readLine()
    if (!Files.exists(Paths.get(config))) {
      println("File does not exist")
      System.exit(1)
    }


    for (line <- Source.fromFile(config).getLines) {
      breakable {
        elements = line.split(" ")
        toRun = elements(2)
        if (toRun.contains("daily")) {
          time = elements(1) + ":" + elements(0)
          if (hours.toInt > elements(1).toInt) day = "tomorrow"
          else if (hours.toInt == elements(1).toInt && minutes.toInt > elements(0).toInt) day = "tomorrow"
          else day = "today"
        }
        else if (toRun.contains("hourly")) {
          if (hours.toInt == 23) {
            time = "00" + ":" + elements(0)
            day = "tomorrow"
          }
          else if (minutes.toInt <= elements(0).toInt) {
            time = hours + ":" + elements(0)
            day = "today"
          }
          else {
            time = (hours.toInt + 1).toString + ":" + elements(0)
            day = "today"
          }
        }
        else if (toRun.contains("minute")) {
          time = hours + ":" + minutes
          day = "today"
        }
        else if (toRun.contains("times")) {
          if (hours.toInt == elements(1).toInt + 1 && minutes.toInt < elements(0).toInt) {
            time = hours + ":" + minutes
            day = "today"
          }
          else if (hours.toInt == elements(1).toInt && minutes.toInt >= elements(0).toInt) {
            time = hours + ":" + minutes
            day = "today"
          }
          else if ((hours.toInt == elements(1).toInt + 1 && minutes.toInt >= elements(0).toInt) || hours.toInt > elements(1).toInt + 1) {
            time = elements(1) + ":" + elements(0)
            day = "tomorrow"
          }
          else {
            time = elements(1) + ":" + elements(0)
            day = "today"
          }
        }
        else {
          println("This line is not set up correctly in the config")
          break
        }
        if (time.split(":")(0).length == 1) time = "0" + time
        if (time.split(":")(1).length == 1) time = time.substring(0, 3) + "0" + time.substring(3)
        println(time + " " + day + " " + toRun)
      }
    }
  }

  checkTime()

}
