package mongo
import hero.{Episode, Jedi}
import play.api.libs.json._
import reactivemongo.api.bson.BSONObjectID
import reactivemongo.play.json.compat._
import json2bson.{toDocumentReader, toDocumentWriter}

object Serializers {
  implicit def mongoObjectId2BSONObjectId(oid: MongoObjectId): BSONObjectID = BSONObjectID.parse(oid.$oid).get
  implicit def bsonObjectId2MongoObjectId(oid: BSONObjectID): MongoObjectId = MongoObjectId(oid.stringify)

  implicit val mongoObjectIdFormat: OFormat[MongoObjectId] = Json.format[MongoObjectId]
  implicit val episodeFormat: Format[Episode.Value] = Json.formatEnum(Episode)

  implicit val jediFormat: OFormat[Jedi] = Json.format[Jedi]
}
