package com.github.dunnololda.cli

import org.slf4j.LoggerFactory

object MySimpleLogger {
  class MySimpleLogger(name:String) {
    private val log = LoggerFactory.getLogger(name)

    def debug(message: => String) {if (log.isDebugEnabled) log.debug(message)}
    def info(message: => String) {if (log.isInfoEnabled) log.info(message)}
    def warn(message: => String) {if (log.isWarnEnabled) log.warn(message)}
    def error(message: => String) {if (log.isErrorEnabled) log.error(message)}
  }

  def apply(name:String):MySimpleLogger = new MySimpleLogger(name)
}
