package com.powertrip.service

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}

import com.powertrip.logging.Logging
import com.powertrip.models.Models.Day
import com.powertrip.models.Date
import com.powertrip.repository.DayRepository
import org.mongodb.scala.bson.collection.immutable
import org.mongodb.scala.model.Filters.equal

import scala.concurrent.ExecutionContext.Implicits.global

final case class DayCreationError(message: String)

class DayService(repository: DayRepository = new DayRepository) extends Logging {
  def updateDay(id: Int, document: immutable.Document) = {
    info(s"Updating day ${id}")
    val updated = repository.updateOne(equal("_id", id), immutable.Document())
    info("Done updating")
    updated
  }
}

object DayService {
  def createDays(startDay: Date, endDate: Date): Either[DayCreationError, List[Day]] = {
    val amount: Int = ChronoUnit.DAYS.between(startDay, endDate).toInt
    if (amount <= 0) Left(DayCreationError("A trip must have a duration."))
    else Right(List.tabulate(amount) {
      day => Day(
        startDay.plusDays(day),
        LocalDateTime.now(ZoneId.systemDefault),
        LocalDateTime.now(ZoneId.systemDefault)
      )
    })
  }
}
