package hero

import mongo.MongoCollectionService
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Json
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.modules.reactivemongo.ReactiveMongoApi
import _root_.play.api.libs.json._
import _root_.reactivemongo.api.bson._
import reactivemongo.play.json.compat._
import mongo.Serializers._
import reactivemongo.play.json.compat.json2bson.{toDocumentReader, toDocumentWriter}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JediService @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit val executionContext: ExecutionContext)
    extends MongoCollectionService[Jedi] {
  override val collectionName: String = "jedi"

  def findByName(name: String): Future[Option[Jedi]] = {
    withCollection flatMap  {c => c.find(BSONDocument("name" -> name)).one[Jedi]}
  }
}
