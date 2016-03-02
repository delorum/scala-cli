package com.github.dunnololda.cli

import java.util.Properties
import java.io._
import com.github.dunnololda.mysimplelogger.MySimpleLogger

import collection.mutable.ArrayBuffer

object AppProperties {
  private val log = MySimpleLogger(this.getClass.getName)

  private val _properties:ArrayBuffer[String] = {
    def _pew(name:String):List[String] = {
      val system_property = System.getProperty(name)
      if(system_property == null || "" == system_property) List()
      else system_property.split(",").map(_.trim()).toList
    }
    val res = ArrayBuffer[String]() ++= _pew("app.properties") ++= _pew("jnlp.app.properties") ++= _pew("appproperties") ++= _pew("jnlp.appproperties")
    /*if (res.length == 0) res += "app.properties"*/
    res
  }
  def properties:Seq[String] = _properties

  private val props:ArrayBuffer[Properties] = {
    ArrayBuffer[Properties]() ++= _properties.map(load) += load("maven.properties") += System.getProperties
  }

  def init(): Unit = {

  }

  private def load(property_filename:String):Properties = {
    /*val current = new java.io.File( "." ).getCanonicalPath()
    log.info("Current dir:"+current)*/

    val p = new Properties
    try {
      val fis = new FileInputStream(property_filename)
      p.load(fis)
      fis.close()
      log.info("loaded property file "+property_filename)
    } catch {
      case ex:Exception =>
        try {
          val is = this.getClass.getClassLoader.getResourceAsStream(property_filename)
          p.load(is)
          is.close()
          log.info("loaded property file "+property_filename+" from jar")
        } catch {
          case ex2:Exception =>
            log.error("error while loading property file "+property_filename+": "+ex.getLocalizedMessage)
        }
    }
    p
  }

  def reloadProperties() {
    log.info("reloading properties")
    props.clear()
    props ++= _properties.map(load) += load("maven.properties") += System.getProperties
  }

  def addProperty(key:String, value:Any, description:String) {
    log.info(description+" : "+key+" -> "+value)
    props.head.put(key, value.toString)
  }

  def containsProperty(key:String):Boolean = getProperty(key).nonEmpty

  def addPropertyFile(filename:String, description:String) {
    log.info(description+" : "+filename)
    _properties += filename
    props += load(filename)
  }

  // maybe add methods to remove property file and/or property

  private def getProperty(key:String):Option[String] = {
    def _getProperty(key:String, props_list:Seq[Properties]):Option[String] = {
      if(props_list.nonEmpty) {
        props_list.head.getProperty(key) match {
          case p:String =>
            log.debug("read property "+key+": "+p)
            Some(p.trim)
          case _ => _getProperty(key, props_list.tail)
        }
      } else {
        log.warn("failed to find property "+key)
        None
      }
    }
    _getProperty(key, props)
  }

  private def defaultValue[A](key:String, default:A) = {
    log.info("default value for property "+key+" is "+(if("".equals(default.toString)) "empty string" else default))
    props.head.put(key, default.toString)
    default
  }

  private val formula_parser = new FormulaParser()
  private def parsedProperty[A : Manifest](key:String, p:String):A = {
    manifest[A].toString match {
      case manifest_type @ ("Int" | "Long" | "Float" | "Double") =>
        val result = formula_parser.calculate(p)
        formula_parser.constants += (key -> result)
        manifest_type match {
          case "Int" => result.toInt.asInstanceOf[A]
          case "Long" => result.toLong.asInstanceOf[A]
          case "Float" => result.toFloat.asInstanceOf[A]
          case _ => result.asInstanceOf[A]  // I believe its 'Double' here =)
        }
      case "Boolean" =>
        if(p.equalsIgnoreCase("yes")  || p.equalsIgnoreCase("1") ||
          p.equalsIgnoreCase("true") || p.equalsIgnoreCase("on")) true.asInstanceOf[A]
        else if(p.equalsIgnoreCase("no")    || p.equalsIgnoreCase("0") ||
          p.equalsIgnoreCase("false") || p.equalsIgnoreCase("off")) false.asInstanceOf[A]
        else {
          throw new Exception("supported boolean properties are: yes/no, 1/0, true/false, on/off")
        }
      case _ => p.asInstanceOf[A] // assuming A is String here. If not - we throw exception
    }
  }
  def property[A : Manifest](key:String, default: => A):A = {
    getProperty(key) match {
      case Some(p) =>
        try {parsedProperty(key, p)}
        catch {
          case e:Exception =>
            log.warn("failed to use property ("+key+" : "+p+") as "+manifest[A]+": "+e)
            defaultValue(key, default)
        }
      case _ => defaultValue(key, default)
    }
  }

  def optProperty[A : Manifest](key:String):Option[A] = {
    getProperty(key) match {
      case Some(p) =>
        try {Some(parsedProperty(key, p))}
        catch {
          case e:Exception =>
            log.warn("failed to use property ("+key+" : "+p+") as "+manifest[A]+": "+e)
            None
        }
      case _ => None
    }
  }

  def reqProperty[A : Manifest](key:String):A = {
    getProperty(key) match {
      case Some(p) =>
        try {parsedProperty(key, p)}
        catch {
          case e:Exception =>
            log.warn("failed to use property ("+key+" : "+p+") as "+manifest[A]+": "+e)
            throw new Exception("property "+key+" required but wasn't found")
        }
      case _ =>
        throw new Exception("property "+key+" required but wasn't found")
    }
  }

  def property[A : Manifest](key:String, default: => A, condition:(A => (Boolean,  String))):A = {
    getProperty(key) match {
      case Some(p) =>
        try {
          val value = parsedProperty(key, p)
          val (is_value_accepted, reason) = condition(value)
          if(!is_value_accepted) {
            log.warn("value "+value+" is unaccepted: "+reason+"; using default")
            defaultValue(key, default)
          } else value
        }
        catch {
          case e:Exception =>
            log.warn("failed to use property ("+key+" : "+p+") as "+manifest[A]+": "+e)
            defaultValue(key, default)
        }
      case _ => defaultValue(key, default)
    }
  }

  def stringProperty(key:String)  = property(key, "")
  def intProperty(key:String)     = property(key, 0)
  def longProperty(key:String)    = property(key, 0L)
  def floatProperty(key:String)   = property(key, 0.0f)
  def booleanProperty(key:String) = property(key, false)

  def optStringProperty(key:String)  = optProperty[String](key)
  def optIntProperty(key:String)     = optProperty[Int](key)
  def optLongProperty(key:String)    = optProperty[Long](key)
  def optFloatProperty(key:String)   = optProperty[Float](key)
  def optBooleanProperty(key:String) = optProperty[Boolean](key)

  def reqStringProperty(key:String)  = reqProperty[String](key)
  def reqIntProperty(key:String)     = reqProperty[Int](key)
  def reqLongProperty(key:String)    = reqProperty[Long](key)
  def reqFloatProperty(key:String)   = reqProperty[Float](key)
  def reqBooleanProperty(key:String) = reqProperty[Boolean](key)

  def stringProperty(key:String, condition:(String => (Boolean,  String))) = property(key, "", (value:String) => (true, ""))
  def intProperty(key:String, condition:(Int => (Boolean,  String))) = property(key, 0, (value:Int) => (true, ""))
  def longProperty(key:String, condition:(Long => (Boolean,  String))) = property(key, 0L, (value:Long) => (true, ""))
  def floatProperty(key:String, condition:(Float => (Boolean,  String))) = property(key, 0.0f, (value:Float) => (true, ""))
  def booleanProperty(key:String, condition:(Boolean => (Boolean,  String))) = property(key, false, (value:Boolean) => (true, ""))

  def appVersion = property("app.version", "Release")
  def appName    = property("app.name", "App")

  log.info("AppProperties loaded")
}
