package com.powertrip.api

import java.time.LocalDateTime

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.powertrip.models.Models.Day
import com.powertrip.repository.DayRepository
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import io.circe.Json
import io.circe.syntax._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.result.DeleteResult
import org.mongodb.scala.{Observable, SingleObservable}
import org.scalamock.scalatest.MixedMockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DayApiTest extends WordSpec
  with Matchers
  with ScalaFutures
  with ScalatestRouteTest
  with MixedMockFactory
  with BeforeAndAfter {
  val repositoryMock: DayRepository = stub[DayRepository]
  val dayApi = new DayApi(repositoryMock)
  val routes: Route = Route.seal(dayApi.dayRoutes)

  "DayApi" should {
    "return a marshalled day when navigating to GET /days/{objectId}" in {
      // setup
      val date, createdAt, updatedAt = LocalDateTime.now
      val day = Day(date, createdAt, updatedAt)
      val path = s"/days/${day._id}/"
      val id = Document("_id" -> new ObjectId(day._id.toHexString))

      (repositoryMock.findOne _)
        .when(id)
        .returns(SingleObservable[Day](day))

      Get(path) ~> routes ~> check {
        status should equal(StatusCodes.OK)
        contentType should equal(ContentTypes.`application/json`)
        responseAs[Json] should be(day.asJson)
      }
    }

    "return a 404 when nothing is found" in {
      val filter = Document("_id" -> new ObjectId())
      (repositoryMock.findOne _)
        .when(filter)
        .returns(Observable(List.empty[Day]))

      val id = filter.getObjectId("_id").toHexString
      val path = s"/days/$id/"
      Get(path) ~> routes ~> check {
        status should equal(StatusCodes.NotFound)
      }
    }

    "should return all element when navigating to GET days" in {
      val concreteRepositoryMock: DayRepository = DayRepository()
      val dayApi = new DayApi(concreteRepositoryMock)
      val routes: Route = Route.seal(dayApi.dayRoutes)
      val days: List[Day] = List.fill(10) {
        Day(LocalDateTime.now, LocalDateTime.now, LocalDateTime.now)
      }

      Await.ready(concreteRepositoryMock.insertMany(days).toFuture, Duration.Inf)

      Get(s"/days/") ~> routes ~> check {
        status should equal(StatusCodes.OK)
        entityAs[List[Day]] should have length 10
      }
    }

    "should create a new day when navigating to POST /days/" in {
      val date, createdAt, updatedAt = LocalDateTime.now
      val day = Day(date, createdAt, updatedAt)
      val jsonBody = day.asJson
      val body = HttpEntity(ContentTypes.`application/json`, jsonBody.toString)

      (repositoryMock.insert _).when(*).returns(Future {
        day
      })

      Post(s"/days/", body) ~> routes ~> check {
        status should equal(StatusCodes.Created)
        entityAs[Day] shouldBe a[Day]
      }
    }

    "should update a new day when navigating to PUT /days/{oid}" in {
      val date, createdAt, updatedAt = LocalDateTime.now
      val day = Day(date, createdAt, updatedAt)
      val updatedDate = date plusDays 1
      val updatedDay = Day(_id = day._id, date = updatedDate, createdAt = day.createdAt, updatedAt = LocalDateTime.now)
      val jsonBody = updatedDay.asJson
      val body = HttpEntity(ContentTypes.`application/json`, jsonBody.toString)

      (repositoryMock.updateOne _).when(*, *, *).returns(SingleObservable(updatedDay))

      Put(s"/days/${day._id.toHexString}", body) ~> routes ~> check {
        status should equal(StatusCodes.OK)
        responseAs[Json] should be(updatedDay.asJson)
      }
    }

    "should delete a day when navigating to DELETE /days/{oid}" in {
      val date, createdAt, updatedAt = LocalDateTime.now
      val day = Day(date, createdAt, updatedAt)

      val deleteResult = mock[DeleteResult]
      (repositoryMock.delete _).when(*, *).returns(SingleObservable(deleteResult))

      Delete(s"/days/${day._id.toHexString}") ~> routes ~> check {
        status should equal(StatusCodes.NoContent)
      }
    }
  }
}
