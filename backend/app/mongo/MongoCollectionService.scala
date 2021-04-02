package mongo

import play.libs.Json
import play.modules.reactivemongo.ReactiveMongoApi
import scala.concurrent.{ExecutionContext, Future}
import mongo.Serializers._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson.{toDocumentReader, toDocumentWriter}

trait MongoCollectionService {
  val reactiveMongoApi: ReactiveMongoApi

  implicit val executionContext: ExecutionContext

  def collection(name: String): Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection[BSONCollection](name))

  def withCollection[T](collection: Future[BSONCollection])(f: BSONCollection => Future[T]): Future[T] = {
    collection flatMap f
  }
}
