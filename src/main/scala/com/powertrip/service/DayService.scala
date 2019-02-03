package com.powertrip.service

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, ZoneId}

import com.powertrip.models.Models.Day
import com.powertrip.models.Date

final case class DayCreationError(message: String)

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
