package mongo
import hero.{Episode, Jedi}
import hero.Episode.EMPIRE
import reactivemongo.api.bson._
import reactivemongo.play.json.compat._
import bson2json._
import play.api.libs.json.{Json, Reads, Writes}
import reactivemongo.play.json.compat.json2bson.{toDocumentReader, toDocumentWriter}

object Serializers {
  import reactivemongo.play.json._, collection._

  implicit val episodeFormat = Json.formatEnum(Episode)
  implicit val jediFormat = Json.format[Jedi]
}
