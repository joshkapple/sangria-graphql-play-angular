package mongo
import character.{CharacterType, Droid, Episode, Human, Jedi, Character}
import play.api.libs.json._
import reactivemongo.api.bson.{BSONDocumentHandler, BSONDocumentWriter, BSONHandler, BSONObjectID, Macros}
import reactivemongo.play.json.compat._
import json2bson.{toDocumentReader, toDocumentWriter}
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.bson._
import play.api.libs.json.Json
import reactivemongo.play.json.compat._

object Serializers {
  implicit val cfg = JsonConfiguration(typeNaming = JsonNaming{fullName => fullName.substring(fullName.lastIndexOf(".") + 1)}) // Strip fully qualified name to just the class name

  implicit def mongoObjectId2BSONObjectId(oid: MongoObjectId): BSONObjectID = BSONObjectID.parse(oid.$oid).get
  implicit def bsonObjectId2MongoObjectId(oid: BSONObjectID): MongoObjectId = MongoObjectId(oid.stringify)

  implicit val mongoObjectIdFormat: OFormat[MongoObjectId] = Json.format[MongoObjectId]
  implicit val episodeFormat: Format[Episode.Value] = Json.formatEnum(Episode)

  implicit val jediFormat: OFormat[Jedi] = Json.format[Jedi]
  implicit val humanFormat: OFormat[Human] = Json.format[Human]
  implicit val droidFormat: OFormat[Droid] = Json.format[Droid]

  implicit val characterFormats: OFormat[Character] = Json.format[Character]

}
