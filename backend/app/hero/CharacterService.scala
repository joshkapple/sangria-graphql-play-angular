package hero

import mongo.{MongoIndexCreator, MongoObjectId, MongoService, SingleMongoCollectionService}
import play.api.libs.json.{Json, OFormat, OWrites}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONCollection
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import mongo.Serializers._
import reactivemongo.api.Cursor
import reactivemongo.api.bson.BSONObjectID
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.compat._,
json2bson.{ toDocumentReader, toDocumentWriter }

@Singleton
class CharacterService @Inject()(val reactiveMongoApi: ReactiveMongoApi)(
    implicit val executionContext: ExecutionContext)
    extends SingleMongoCollectionService[Character] {
  val collectionName: String = "character"

  def create(name: String, characterType: CharacterType.Value = CharacterType.Jedi): Future[WriteResult] = {
    val newCharacter = characterType match {
      case CharacterType.Jedi =>
        Jedi(BSONObjectID.generate(), Some(name), friends = Nil, appearsIn = Nil, primaryFunction = None)
      case CharacterType.Human => Human(BSONObjectID.generate(), Some(name), friends = Nil, appearsIn = Nil, None)
      case CharacterType.Droid =>
        Droid(BSONObjectID.generate(), Some(name), friends = Nil, appearsIn = Nil, primaryFunction = None)
    }
    insert(newCharacter)
  }

  def all(): Future[List[Character]] = {
    collection {c =>
      c.find(Json.obj()).cursor[Character]().collect[List](Int.MaxValue, Cursor.FailOnError[List[Character]]())
    }
  }

  def jediByName(name: String): Future[Option[Jedi]] = {
    collection { c: BSONCollection =>
      for {
        _ <- create(name)
        result <- c.find(Json.obj("name" -> name, "_type" -> "hero.Jedi")).one[Jedi]
      } yield result
    }
  }

  def byIds(ids: Seq[MongoObjectId]): Future[List[Character]] = {
    collection { c =>
      c.find(Json.obj("_id" -> Json.obj("$in" -> ids))).cursor[Character]().collect[List](Int.MaxValue, Cursor.FailOnError[List[Character]]())
    }
  }

  def getHuman(id: MongoObjectId): Future[Option[Human]] = Future{None}
  def getDroid(id: MongoObjectId): Future[Option[Droid]] = Future{None}
  def getJedi(id: MongoObjectId): Future[Option[Jedi]] = Future{None}
}

@Singleton
class CharacterIndexCreator @Inject()(val service: CharacterService, reactiveMongoApi: ReactiveMongoApi)(
    implicit val executionContext: ExecutionContext)
    extends MongoIndexCreator(reactiveMongoApi)
