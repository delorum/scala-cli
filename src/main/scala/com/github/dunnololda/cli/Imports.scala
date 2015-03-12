package com.github.dunnololda.cli

object Imports {
  type Cli           = _root_.com.github.dunnololda.cli.Cli
  val AppProperties  = _root_.com.github.dunnololda.cli.AppProperties
  val MySimpleLogger = _root_.com.github.dunnololda.mysimplelogger.MySimpleLogger

  def properties:Seq[String]                                 = AppProperties.properties
  def reloadProperties()                                     {AppProperties.reloadProperties()}
  def addProperty(key:String, value:Any, description:String) {AppProperties.addProperty(key, value, description)}
  def containsProperty(key:String):Boolean                   = AppProperties.containsProperty(key)
  def addPropertyFile(filename:String, description:String)   {AppProperties.addPropertyFile(filename, description)}

  def property[A : Manifest](key:String, default: => A):A                                      = AppProperties.property(key, default)
  def property[A : Manifest](key:String, default: => A, condition:(A => (Boolean,  String))):A = AppProperties.property(key, default, condition)
  def optProperty[A : Manifest](key:String):Option[A]                                          = AppProperties.optProperty(key)
  def reqProperty[A : Manifest](key:String):A                                                  = AppProperties.reqProperty(key)

  def stringProperty(key:String):String   = AppProperties.stringProperty(key)
  def intProperty(key:String):Int         = AppProperties.intProperty(key)
  def longProperty(key:String):Long       = AppProperties.longProperty(key)
  def floatProperty(key:String):Float     = AppProperties.floatProperty(key)
  def booleanProperty(key:String):Boolean = AppProperties.booleanProperty(key)

  def optStringProperty(key:String):Option[String]   = AppProperties.optStringProperty(key)
  def optIntProperty(key:String):Option[Int]         = AppProperties.optIntProperty(key)
  def optLongProperty(key:String):Option[Long]       = AppProperties.optLongProperty(key)
  def optFloatProperty(key:String):Option[Float]     = AppProperties.optFloatProperty(key)
  def optBooleanProperty(key:String):Option[Boolean] = AppProperties.optBooleanProperty(key)

  def reqStringProperty(key:String):String   = AppProperties.reqStringProperty(key)
  def reqIntProperty(key:String):Int         = AppProperties.reqIntProperty(key)
  def reqLongProperty(key:String):Long       = AppProperties.reqLongProperty(key)
  def reqFloatProperty(key:String):Float     = AppProperties.reqFloatProperty(key)
  def reqBooleanProperty(key:String):Boolean = AppProperties.reqBooleanProperty(key)

  def stringProperty(key:String, condition:(String => (Boolean,  String))):String    = property(key, "", (value:String) => (true, ""))
  def intProperty(key:String, condition:(Int => (Boolean,  String))):Int             = property(key, 0, (value:Int) => (true, ""))
  def longProperty(key:String, condition:(Long => (Boolean,  String))):Long          = property(key, 0L, (value:Long) => (true, ""))
  def floatProperty(key:String, condition:(Float => (Boolean,  String))):Float       = property(key, 0.0f, (value:Float) => (true, ""))
  def booleanProperty(key:String, condition:(Boolean => (Boolean,  String))):Boolean = property(key, false, (value:Boolean) => (true, ""))

  def appVersion = property("app.version", "Release")
  def appName    = property("app.name", "App")
}
