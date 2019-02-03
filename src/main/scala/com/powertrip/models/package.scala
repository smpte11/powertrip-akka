package com.powertrip

import java.time.{ Instant, LocalDateTime, ZoneId, ZoneOffset }
import java.util.Date

import org.mongodb.scala.model.geojson.Point
import org.bson.{ BsonReader, BsonWriter }
import org.bson.codecs.{ Codec, DecoderContext, EncoderContext }
import io.circe.{ Decoder, Encoder }
import io.circe.generic.extras.Configuration
import org.bson.types.ObjectId

package object models {

  class LocalDateTimeCodec extends Codec[LocalDateTime] {
    override def decode(reader: BsonReader, decoderContext: DecoderContext): LocalDateTime = LocalDateTime
      .ofInstant(Instant.ofEpochMilli(reader.readDateTime), ZoneOffset.UTC)

    override def encode(writer: BsonWriter, value: LocalDateTime, encoderContext: EncoderContext): Unit = writer
      .writeDateTime(value.atZone(ZoneOffset.UTC).toInstant.toEpochMilli)

    override def getEncoderClass: Class[LocalDateTime] = classOf[LocalDateTime]
  }

  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
  implicit val encodeOID: Encoder[ObjectId] = Encoder.encodeString.contramap[ObjectId](_.toHexString)
  implicit val decodeOID: Decoder[ObjectId] = Decoder.decodeString.emap(str => Right(new ObjectId(str)))

  type Date = LocalDateTime
  type Destination = Point
  type Destinations = List[Destination]
}
