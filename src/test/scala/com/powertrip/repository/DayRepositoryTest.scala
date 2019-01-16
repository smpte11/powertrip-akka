package com.powertrip.repository

import java.time.LocalDateTime

import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Filters.{equal => equalFilter}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfter, BeforeAndAfterEach, Matchers}
import com.powertrip.models.Models.Day
import scala.concurrent.duration._

import scala.concurrent.Await


class DayRepositoryTest extends AsyncFlatSpec
  with BeforeAndAfter
  with BeforeAndAfterEach
  with Matchers {

  var repository: BaseRepository[Day] = new DayRepository

  after {
    Await.ready(repository.collection.drop().toFuture, Duration.Inf)
  }

  "Class extending BaseRepository" should "insert case classes properly in mongo" in {
    val day: Day = Day(LocalDateTime.now, LocalDateTime.now, LocalDateTime.now)
    for {
      result <- repository.insert(day)
    } yield result shouldBe a [Day]
  }

  it should "insert many documents properly in mongo" in {
    val days: List[Day] = List.fill(10) { Day(LocalDateTime.now, LocalDateTime.now, LocalDateTime.now) }
    for {
      bulkResults <- repository.insertMany(days).toFuture
    } yield {
      bulkResults.wasAcknowledged shouldBe true
      bulkResults.getInsertedCount shouldBe 10
    }
  }

  it should "update an already inserted object" in {
    val day: Day = Day(LocalDateTime.now, LocalDateTime.now, LocalDateTime.now)
    for {
      insertResult <- repository.insert(day)
      updateResult <- repository.updateOne(equalFilter("_id", insertResult._id), currentTimestamp("updatedAt")).toFuture
    } yield {
      updateResult shouldBe a [Day]
      updateResult._id should equal (insertResult._id)
    }
  }

  it should "delete a document" in {
    val days: List[Day] = List.fill(10) { Day(LocalDateTime.now, LocalDateTime.now, LocalDateTime.now) }
    for {
      _ <- repository.insertMany(days).toFuture
      firstItem <- repository.findAll.first.head
      deleteResult <- repository.delete(equalFilter("_id", firstItem._id)).toFuture
    } yield {
      deleteResult.wasAcknowledged shouldBe true
      deleteResult.getDeletedCount shouldBe 1
    }
  }

  it should "find all documents" in {
    val days: List[Day] = List.fill(10) { Day(LocalDateTime.now, LocalDateTime.now, LocalDateTime.now) }
    for {
      _ <- repository.insertMany(days).toFuture
      findResult <- repository.findAll.toFuture
    } yield findResult should have size 10
  }
}
