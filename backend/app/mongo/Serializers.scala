package mongo
import play.api.libs.json.JsValue
import reactivemongo.api.bson.{BSONHandler, BSONValue}
import reactivemongo.play.json.compat._
import reactivemongo.api.bson.{
  BSONDocumentWriter, BSONDocumentReader, Macros, document
}

object Serializers {
  import reactivemongo.play.json._, collection._

}
