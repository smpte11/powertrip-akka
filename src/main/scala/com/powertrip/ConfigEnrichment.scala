package com.powertrip

import com.typesafe.config.Config

import scala.util.{Failure, Success, Try}

object ConfigEnrichment {
  implicit class ConfigEnrichment(val config: Config) {
    def getStringOrElse(path: String): Option[String] = {
      Try(config.getString(path)) match {
        case Failure(_) => None
        case Success(configValue) => Some(configValue)
      }
    }
  }
}
