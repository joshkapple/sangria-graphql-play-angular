package mongo

import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.{ExecutionContext, Future}
import mongo.Serializers._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.compat._
import json2bson.{toDocumentReader, toDocumentWriter}
import play.api.libs.json.{JsObject, Json, OFormat, OWrites, Reads, Writes}
import reactivemongo.api.bson.collection.BSONSerializationPack.{Reader, Writer}
import reactivemongo.api.Cursor

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
  implicit def readsT: Reads[T]
  implicit def readerT: Reader[T]

  val collectionName: String

  def collection[T](f: BSONCollection => Future[T]): Future[T] = withCollection(collection(collectionName))(f)

  def insert(t: T)(implicit writesT: Writer[T]): Future[WriteResult] = withCollection(collection(collectionName))(c => c.insert.one[T](t))

  def all(): Future[List[T]] = {
    collection {c =>
      c.find(Json.obj()).cursor[T]().collect[List](Int.MaxValue, Cursor.FailOnError[List[T]]())
    }
  }

  def byIds(ids: Seq[MongoObjectId]): Future[List[T]] = {
    collection { c =>
      c.find(Json.obj("_id" -> Json.obj("$in" -> ids))).cursor[T]().collect[List](Int.MaxValue, Cursor.FailOnError[List[T]]())
    }
  }

  def byId(id: MongoObjectId): Future[Option[T]] = {
    collection { c =>
      c.find(Json.obj("_id" -> id)).one[T]
    }
  }

  def byIdAndType(id: MongoObjectId, classOf: Class[_]): Future[Option[T]] =  {
    collection { c =>
      c.find(Json.obj("_id" -> id, withClassType(classOf))).one[T]}
  }

  def withClassType(classOf: Class[_]) = ("_type" -> Json.toJsFieldJsValueWrapper(classOf.getSimpleName.replace("$", "")))
}