package com.powertrip.models

import java.time.LocalDateTime

import cats.Applicative
import io.circe.{ Decoder, Encoder }
import io.circe.generic.semiauto._
import org.mongodb.scala.bson.ObjectId

object Models {
  trait BaseEntity {
    val createdAt: Date
    val updatedAt: Date
  }

  case class Day(
    _id: ObjectId,
    date: Date,
    createdAt: Date,
    updatedAt: Date) extends BaseEntity

  object Day {
    def apply(date: Date, createdAt: Date, updatedAt: Date): Day = new Day(new ObjectId, date, createdAt, updatedAt)
  }

  implicit val encoder: Encoder[Day] = deriveEncoder[Day]
  implicit val decoder: Decoder[Day] = deriveDecoder[Day]
}

