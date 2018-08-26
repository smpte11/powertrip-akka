package com.powertrip.api

import java.time.LocalDateTime

import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpResponse, ResponseEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import akka.stream.scaladsl.Sink
import com.powertrip.models.Models.Day
import com.powertrip.models._
import com.powertrip.repository.DayRepository
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe._
import org.mongodb.scala.bson.conversions.Bson

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


class DayApi(repository: DayRepository)(implicit executionContext: ExecutionContext, materializer: Materializer) extends ErrorAccumulatingCirceSupport {

  case class Base(test: String)

  val dayRoutes: Route = ignoreTrailingSlash {
    pathPrefix("days") {
      pathEnd {
        get {
          val source = MongoSource[Day](repository.findAll)
          val future = source.runWith(Sink.seq)
          onComplete(future) {
            case Success(values) =>
              val marshalled = Marshal(values)
                .to[ResponseEntity]
                .map(entity => HttpResponse(entity = entity, status = StatusCodes.OK))
              complete(marshalled)
            case Failure(_) =>
              complete(HttpResponse(StatusCodes.BadRequest))
          }
        } ~ (post & entity(as[Day])) { day =>
          val future = repository.insert(day)
          onComplete(future) {
            case Success(value) =>
              val marshalled = Marshal(value)
                .to[ResponseEntity]
                .map(entity => HttpResponse(entity = entity, status = StatusCodes.Created, headers = List(Location(s"/api/users/${value._id}"))))
              complete(marshalled)
            case Failure(_) => complete(HttpResponse(status = StatusCodes.BadRequest))
          }
        }
      } ~ path(Segment).as(FindByIdRequest) { request =>
        get {
          val id = Document("_id" -> new ObjectId(request.id))
          val source = MongoSource[Day](repository.findOne(id))
          val future = source.runWith(Sink.head[Day])
          onComplete(future) {
            case Success(value) =>
              val marshalled = Marshal(value)
                .to[ResponseEntity]
                .map(entity => HttpResponse(entity = entity, status = StatusCodes.OK))
              complete(marshalled)
            case Failure(_) => complete(HttpResponse(status = StatusCodes.NotFound))
          }
        } ~ (put & entity(as[Json])) { day =>
          val filter = equal("_id", request.id)
          val cursor = day.hcursor
          val update: Option[Bson] = for {
            _id <- cursor.downField("_id").as[ObjectId].toOption
            date <- cursor.downField("date").as[LocalDateTime].toOption
            createdAt <- cursor.downField("createdAt").as[LocalDateTime].toOption
          } yield combine(set("_id", _id), set("date", date), set("createdAt", createdAt))

          update match {
            case Some(u) =>
              val future = repository.updateOne(filter, u).toFuture
              onComplete(future) {
                case Success(value) =>
                  val marshalled = Marshal(value)
                    .to[ResponseEntity]
                    .map(entity => HttpResponse(entity = entity, status = StatusCodes.OK))
                  complete(marshalled)
                case Failure(_) => complete(HttpResponse(status = StatusCodes.BadRequest))
              }
            case None => complete(HttpResponse(status = StatusCodes.BadRequest))
          }
        } ~ delete {
          val filter = equal("_id", request.id)
          val future = repository.delete(filter).toFuture
          onComplete(future) {
            case Success(_) => complete(HttpResponse(status = StatusCodes.NoContent))
            case Failure(_) => complete(HttpResponse(status = StatusCodes.BadRequest))
          }
        }
      }
    }
  }
}
