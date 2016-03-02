package com.github.dunnololda.cli

import scala.collection.mutable

trait Cli extends App {

  //private val log = Logger(this.getClass.getName)

  case class CliArg(short: String, long: String, description: String, has_value: Boolean, required: Boolean = false)

  private var program_description = ""

  def programDescription = program_description

  def programDescription_=(descr: String) {
    program_description = descr
  }

  private val cli_args_short = mutable.HashMap[String, CliArg]()
  private val cli_args_long = mutable.HashMap[String, CliArg]()
  private val cli_args_list = mutable.ArrayBuffer[CliArg]()

  def commandLineArg(short: String, long: String, description: String, has_value: Boolean = false, required: Boolean = false) {
    val new_cli_arg = CliArg(short, long, description, has_value, required)
    cli_args_short += (short -> new_cli_arg)
    cli_args_long += (long -> new_cli_arg)
    cli_args_list += new_cli_arg
  }

  def commandLineArgAndParse(short: String, long: String, description: String, has_value: Boolean = false, required: Boolean = false) {
    commandLineArg(short, long, description, has_value, required)
    parseCommandLineArgs()
  }


  def commandLineArgs(args: (String, String, String, Boolean, Boolean)*) {
    args.foreach {
      case (short: String, long: String, description: String, has_value: Boolean, required: Boolean) =>
        commandLineArg(short, long, description, has_value, required)
    }
  }

  def commandLineArgsAndParse(args: (String, String, String, Boolean, Boolean)*) {
    commandLineArgs(args: _*)
    parseCommandLineArgs()
  }

  def helpMessage: String = {
    val result = new StringBuilder()
    if (program_description != "") result append program_description append "\n"
    result append "Options:" append "\n"
    cli_args_list.foreach {
      case CliArg(short, long, description, has_value, required) =>
        val short_only = (new StringBuilder("-") append short).toString()
        val except_descr = (new StringBuilder(short_only) append " " * (10 - short_only.length) append "--" append long append
          (if (has_value) " arg" else "") append (if (required) " (required)" else "")).toString()
        result append except_descr append " " * (40 - except_descr.length) append description append "\n"
    }

    {
      val short = "help"
      val long = "help"
      val description = "show this usage information"
      val short_only = (new StringBuilder("-") append short).toString()
      val except_descr = (new StringBuilder(short_only) append " " * (10 - short_only.length) append "--" append long append " ").toString()
      result append except_descr append " " * (40 - except_descr.length) append description
    }

    result.toString()
  }

  private def printHelpAndExit() {
    println(helpMessage)
    System.exit(0)
  }

  private def checkPropInMap(m: mutable.HashMap[String, CliArg], prop: String, pos: Int) {
    prop match {
      case "help" => printHelpAndExit()
      case p => m.get(p) match {
        case Some(CliArg(_, long, _, has_value, _)) =>
          if (!has_value) {
            AppProperties.addProperty(long, true, "added command line property")
          } else {
            if (pos >= this.args.length) {
              println("value required for command line property: " + prop)
              throw new Exception("failed to start")
            }
            else {
              def isNextProp(str: String): Boolean = {
                (str.startsWith("--") && cli_args_long.contains(str.replaceFirst("--", ""))) ||
                  (str.startsWith("-") && cli_args_short.contains(str.replaceFirst("-", "")))
              }
              val value = this.args.drop(pos + 1).takeWhile(a => !isNextProp(a)).mkString(" ")
              AppProperties.addProperty(long, value, "added command line property")
            }
          }
        case None =>
          println("unknown command line property: " + prop)
          throw new Exception("failed to start")
      }
    }
  }

  def parseCommandLineArgs() {
    // перебираем переданные в программу аргументы и заполняем проперти
    this.args.zipWithIndex.filter {
      case (arg, pos) => arg.startsWith("-") && arg != "-"
    }.foreach {
      case (arg, pos) =>
        arg.toList match {
          case '-' :: '-' :: prop => checkPropInMap(cli_args_long, prop.mkString, pos)
          case '-' :: prop => checkPropInMap(cli_args_short, prop.mkString, pos)
          case prop =>
            println("unknown command line property: " + prop.mkString)
            throw new Exception("failed to start")
        }
    }

    // ищем аргументы, которые не переданы в программу, и при этом требуются обязательно
    val missed_required_args = cli_args_list.filter {
      case CliArg(short, long, _, _, required) =>
        required && !this.args.contains("--" + long) && !this.args.contains("-" + short) && !AppProperties.containsProperty(long)
    }
    if (missed_required_args.nonEmpty) {
      missed_required_args.foreach {
        case CliArg(_, long, _, _, _) =>
          println("argument required: " + long)
      }
      throw new Exception("failed to start")
    }
  }

  override def main(args: Array[String]) {
    if (System.getProperties.containsKey("notrycatch")) {
      super.main(args)
    } else {
      try {
        super.main(args)
      } catch {
        case e: Exception =>
          println(e.getLocalizedMessage)
          printHelpAndExit()
      }
    }
  }
}
