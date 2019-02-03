package com.powertrip.repository

import org.bson.conversions.Bson
import org.mongodb.scala.model._
import org.mongodb.scala.result.DeleteResult
import org.mongodb.scala.{ BulkWriteResult, FindObservable, MongoCollection, SingleObservable }

import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.ClassTag

abstract class BaseRepository[T: ClassTag](collectionName: String, executionContext: ExecutionContext)
  extends MongoDB {
  private implicit val ec: ExecutionContext = executionContext

  private val name: String = collectionName
  private lazy val _collection: MongoCollection[T] = db.getCollection[T](name)

  def collection: MongoCollection[T] = _collection

  def findAll: FindObservable[T] = collection.find[T]
  def findSome(filters: Bson): FindObservable[T] = collection.find[T](filters)
  def findOne(filters: Bson): SingleObservable[T] = findSome(filters).first

  def insert(item: T): Future[T] = collection.insertOne(item).head.map { _ => item }
  def updateOne(filters: Bson, updates: Bson, options: FindOneAndUpdateOptions = new FindOneAndUpdateOptions): SingleObservable[T] = collection
    .findOneAndUpdate(filters, updates, options)

  def insertMany(list: List[T], options: BulkWriteOptions = new BulkWriteOptions): SingleObservable[BulkWriteResult] = collection
    .bulkWrite(list map { InsertOneModel(_) })

  def delete(filters: Bson, options: DeleteOptions = new DeleteOptions): SingleObservable[DeleteResult] = collection.deleteOne(filters)
}
