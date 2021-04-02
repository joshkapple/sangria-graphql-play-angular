package hero

import mongo.{MongoCollectionService, MongoIndexCreator}
import play.api.libs.json.{Json, OWrites}
import play.modules.reactivemongo.ReactiveMongoApi
import mongo.Serializers._
import reactivemongo.api.bson.collection.BSONCollection
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json.compat._
import json2bson._

@Singleton
class JediService @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit val executionContext: ExecutionContext)
    extends MongoCollectionService {
  val collectionName: String = "jedi"

  implicit def collection: Future[BSONCollection] = collection(collectionName)

  def create(name: String) = {
    val newJedi =
      Jedi(BSONObjectID.generate(), Some(name), friends = Nil, appearsIn = Nil, primaryFunction = None)
    insert(newJedi)
  }

  def insert(t: Jedi)(implicit writes: OWrites[Jedi]) = withCollection(collection(collectionName)) { c =>
    c.insert.one[Jedi](t)
  }

  def findByName(name: String): Future[Option[Jedi]] = {
    withCollection(collection(collectionName)) { c =>
      for {
        result <- c.find(Json.obj("name" -> name)).one[Jedi]
      } yield result
    }
  }
}

@Singleton
class JediIndexCreator @Inject()(jediService: JediService, reactiveMongoApi: ReactiveMongoApi)(
    implicit val executionContext: ExecutionContext)
    extends MongoIndexCreator(reactiveMongoApi) {
  override implicit def collection = jediService.collection

  override val collectionName: String = jediService.collectionName
}
