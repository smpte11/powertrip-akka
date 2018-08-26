package com.powertrip.service

import java.time.{LocalDateTime, ZoneId}

import org.scalatest.{EitherValues, FlatSpec, Matchers}
import com.powertrip.models.Date
import com.powertrip.models.Models.Day

class DayServiceTest extends FlatSpec
  with Matchers
  with EitherValues {

  val tripStartDate: Date = LocalDateTime.now(ZoneId.systemDefault)
  val tripEndDate: Date = LocalDateTime.now(ZoneId.systemDefault) plusDays 7

  "Day service" should "create a new range of days for a trip" in {
    val tripDuration: Either[DayCreationError, List[Day]] = DayService.createDays(tripStartDate, tripEndDate)
    tripDuration.right.value should have length 7
  }

  it should "return an error class if the trip end date is before the start date" in {
    val tripDuration: Either[DayCreationError, List[Day]] = DayService.createDays(tripEndDate, tripStartDate)
    tripDuration.left.value shouldBe a [DayCreationError]
  }

  it should "return an error class if the trip has no duration" in {
    val tripDuration: Either[DayCreationError, List[Day]] = DayService.createDays(tripStartDate, tripStartDate)
    tripDuration.left.value shouldBe a [DayCreationError]
  }
}
