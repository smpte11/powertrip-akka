package com.powertrip.models

import java.time.LocalDateTime

import io.circe.Json
import io.circe.syntax._
import io.circe.literal._
import org.scalatest.{ EitherValues, FlatSpec, Matchers }
import com.powertrip.models.Models._
import com.powertrip.models.Models.Day
import org.bson.types.ObjectId

class ModelsTest extends FlatSpec
  with Matchers
  with EitherValues {
  "Day encoder" should "encode a day as Json" in {
    val dayJson = Day(LocalDateTime.now, LocalDateTime.now, LocalDateTime.now).asJson
    dayJson shouldBe a[Json]
  }

  "Day decoder" should "decode a Json string" in {
    val oid = new ObjectId().toHexString
    val date = LocalDateTime.now.toString
    val createdAt = LocalDateTime.now.toString
    val updatedAt = LocalDateTime.now.toString
    val dayString = json"""{ "_id": $oid, "date": $date, "createdAt": $createdAt, "updatedAt": $updatedAt }"""
    val decodedDay = dayString.as[Day]
    decodedDay.right.value shouldBe a[Day]
  }
}
