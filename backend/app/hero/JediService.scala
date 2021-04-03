package hero

import mongo.{MongoIndexCreator, MongoService, SingleMongoCollectionService}
import play.api.libs.json.{Json, OWrites}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONCollection

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import reactivemongo.api.bson.{BSONDocument, BSONObjectID}
import mongo.Serializers._
import reactivemongo.play.json.compat._
import json2bson.{toDocumentReader, toDocumentWriter}


@Singleton
class JediService @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit val executionContext: ExecutionContext)
    extends SingleMongoCollectionService[Jedi] {
  val collectionName: String = "jedi"

  def create(name: String) = {
    val newJedi =
      Jedi(BSONObjectID.generate(), Some(name), friends = Nil, appearsIn = Nil, primaryFunction = None)
    insert(newJedi)
  }

  def findByName(name: String): Future[Option[Jedi]] = {
    withServiceCollection { c: BSONCollection =>
      for { _ <- create(name)
        result <- c.find(Json.obj("name" -> name)).one[Jedi]
      } yield result
    }
  }
}

@Singleton
class JediIndexCreator @Inject()(val service: JediService, reactiveMongoApi: ReactiveMongoApi)(
    implicit val executionContext: ExecutionContext)
    extends MongoIndexCreator(reactiveMongoApi)
