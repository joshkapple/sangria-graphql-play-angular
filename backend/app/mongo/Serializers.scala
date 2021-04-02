package mongo
import hero.{Episode, Jedi}
import reactivemongo.api.bson.Macros
import _root_.play.api.libs.json._
import _root_.reactivemongo.api.bson._
import reactivemongo.play.json.compat._
import reactivemongo.play.json._
import collection._
import json2bson._
import reactivemongo.play.json.compat.json2bson._
import reactivemongo.play.json.compat.json2bson.{toDocumentReader, toDocumentWriter}


object Serializers {

  implicit def mongoObjectId2BSONObjectId(oid: MongoObjectId): BSONObjectID = BSONObjectID.parse(oid.$oid).get
  implicit def bsonObjectId2MongoObjectId(oid: BSONObjectID): MongoObjectId = MongoObjectId(oid.stringify)

  implicit val mongoObjectIdFormat: OFormat[MongoObjectId] = Json.format[MongoObjectId]
  implicit val episodeFormat: Format[Episode.Value] = Json.formatEnum(Episode)

  implicit val jediFormat: OFormat[Jedi] = Json.format[Jedi]
  implicit val jediBsonDocumentWriter: BSONDocumentWriter[Jedi] = toDocumentWriter
  implicit val jediBsonDocumentReader: BSONDocumentReader[Jedi] = toDocumentReader
}
