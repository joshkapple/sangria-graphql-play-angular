package mongo

import play.libs.Json
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.{ExecutionContext, Future}
import mongo.Serializers._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.compat._
import json2bson.{toDocumentReader, toDocumentWriter}
import play.api.libs.json.{Reads, Writes}
import reactivemongo.api.bson.collection.BSONSerializationPack.{Reader, Writer}

trait MongoService {
  val reactiveMongoApi: ReactiveMongoApi

  implicit val executionContext: ExecutionContext

  def collection(name: String): Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection[BSONCollection](name))

  def withCollection[T](collection: Future[BSONCollection])(f: BSONCollection => Future[T]): Future[T] = {
    collection flatMap f
  }
}

trait SingleMongoCollectionService[T] extends MongoService {
  val collectionName: String

  implicit def collection[T]: Future[BSONCollection] = collection(collectionName)

  def withServiceCollection[T](f: BSONCollection => Future[T]) = withCollection(collection(collectionName))(f)

  def insert(t: T)(implicit writesT: Writer[T]): Future[WriteResult] = withCollection(collection(collectionName))(c => c.insert.one[T](t))
}