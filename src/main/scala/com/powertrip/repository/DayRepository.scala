package com.powertrip.repository

import com.powertrip.models.Models.Day
import com.powertrip.models.Models.Day._

import scala.concurrent.ExecutionContext

case class DayRepository(implicit ec: ExecutionContext) extends BaseRepository[Day]("trip", ec)
