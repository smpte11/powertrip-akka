package com.powertrip

import com.typesafe.config._
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.{CodecRegistries, CodecRegistry}
import com.powertrip.models.Models.Day
import org.bson.codecs.jsr310.LocalDateTimeCodec

package object repository {
  trait MongoDB {
    val codecRegistry: CodecRegistry = fromRegistries(
      fromProviders(classOf[Day]),
      CodecRegistries.fromCodecs(new LocalDateTimeCodec),
      DEFAULT_CODEC_REGISTRY
    )
    val config: Config = ConfigFactory.load
    val client = MongoClient(config.getString("mongo.uri"))
    val db: MongoDatabase = client.getDatabase(config.getString("mongo.database")).withCodecRegistry(codecRegistry)
  }
}
