package com.powertrip.logging

import journal._

trait Logging {
  private val log = Logger[this.type]
  def info(msg: String): Unit = log.info(msg)
  def error(msg: String): Unit = log.error(msg)
  def warn(msg: String): Unit = log.error(msg)
}
