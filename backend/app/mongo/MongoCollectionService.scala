package mongo

import play.libs.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json._
import scala.concurrent.{ExecutionContext, Future}
import mongo.Serializers._
import play.api.libs.json._
import reactivemongo.api.bson.collection.BSONCollection,
compat.jsObjectWrites,
compat.json2bson._
import reactivemongo.play.json.compat._
import reactivemongo.play.json.compat.json2bson.{toDocumentReader, toDocumentWriter}

trait MongoCollectionService[T] {
  val reactiveMongoApi: ReactiveMongoApi
  val collectionName: String
  implicit val executionContext: ExecutionContext

  def collection(name: String): Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection[BSONCollection](name))

  def withCollection: Future[BSONCollection] = collection(collectionName)

  def insert(t: T)(implicit writes: OWrites[T] ) = withCollection map {c => c.insert.one[T](t)}
}
