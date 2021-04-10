package character

import mongo.{MongoIndexCreator, MongoObjectId, MongoService, SingleMongoCollectionService}
import play.api.libs.json.{Json, OFormat, OWrites, Reads}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.collection.BSONCollection
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import mongo.Serializers._
import reactivemongo.api.bson.BSONObjectID
import reactivemongo.play.json.compat._
import json2bson.{toDocumentReader, toDocumentWriter}
import reactivemongo.api.bson.collection.BSONSerializationPack.Reader

@Singleton
class CharacterService @Inject()(val reactiveMongoApi: ReactiveMongoApi)(
    implicit val executionContext: ExecutionContext)
    extends SingleMongoCollectionService[Character] {
  val collectionName: String = "character"
  override def readsT: Reads[Character] = implicitly
  override def readerT: Reader[Character] = implicitly

  def create(name: String, characterType: CharacterType.Value): Future[Character] = {
    val newCharacter = characterType match {
      case CharacterType.Jedi =>
        Jedi(BSONObjectID.generate(), Some(name), friends = Nil, appearsIn = Nil, primaryFunction = None)
      case CharacterType.Human => Human(BSONObjectID.generate(), Some(name), friends = Nil, appearsIn = Nil, None)
      case CharacterType.Droid =>
        Droid(BSONObjectID.generate(), Some(name), friends = Nil, appearsIn = Nil, primaryFunction = None)
    }
    insert(newCharacter).map(_ => newCharacter)
  }

  def jediByName(name: String): Future[Option[Jedi]] = {
    collection { c: BSONCollection =>
      for {
        result <- c.find(Json.obj("name" -> name, withClassType(Jedi.getClass))).one[Jedi]
      } yield result
    }
  }

  def byIdAndType(id: MongoObjectId, characterType: CharacterType.Value): Future[Option[Character]] = {
    println(id)
    characterType match {
      case CharacterType.Jedi => byIdAndType(id, character.Jedi.getClass)
      case CharacterType.Droid => byIdAndType(id, character.Droid.getClass)
      case CharacterType.Human => byIdAndType(id, character.Human.getClass)
    }
  }

  def getHuman(id: MongoObjectId): Future[Option[Human]] = byIdAndType(id, character.Human.getClass).map(_.map(_.asInstanceOf[Human]))
  def getDroid(id: MongoObjectId): Future[Option[Droid]] = byIdAndType(id, character.Droid.getClass).map(_.map(_.asInstanceOf[Droid]))
  def getJedi(id: MongoObjectId): Future[Option[Jedi]] = byIdAndType(id, character.Jedi.getClass).map(_.map(_.asInstanceOf[Jedi]))
}

@Singleton
class CharacterIndexCreator @Inject()(val service: CharacterService, reactiveMongoApi: ReactiveMongoApi)(
    implicit val executionContext: ExecutionContext)
    extends MongoIndexCreator(reactiveMongoApi)
