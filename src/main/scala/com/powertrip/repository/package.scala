package com.powertrip

import com.powertrip.models.Models.Day
import com.powertrip.models.LocalDateTimeCodec
import com.typesafe.config._
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.{CodecRegistries, CodecRegistry}

package object repository {
  trait MongoDB {
    lazy val codecRegistry: CodecRegistry = fromRegistries(
      fromProviders(classOf[Day]),
      CodecRegistries.fromCodecs(new LocalDateTimeCodec),
      DEFAULT_CODEC_REGISTRY
    )
    lazy val config: Config = ConfigFactory.load
    lazy val client = MongoClient(config.getString("mongo.uri"))
    lazy val db: MongoDatabase = client.getDatabase(config.getString("mongo.database")).withCodecRegistry(codecRegistry)
  }
}
