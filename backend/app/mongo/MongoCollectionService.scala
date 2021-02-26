package mongo

import play.libs.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.api.libs.json._
import reactivemongo.play.json._
import collection._
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.play.json._
import collection.JSONCollection

trait MongoCollectionService[T] {
  val reactiveMongoApi: ReactiveMongoApi
  val db = reactiveMongoApi.database
  val collectionName: String
  implicit val executionContext: ExecutionContext

  def collection(name: String): Future[JSONCollection] =
    db.map(_.collection[JSONCollection](name))

  def withCollection: Future[JSONCollection] = collection(collectionName)

  def insert(t: T)(implicit writes: OWrites[T] ) = withCollection map {c => c.insert.one[T](t)}
}
