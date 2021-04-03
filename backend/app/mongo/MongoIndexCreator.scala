package mongo

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.{BSONCollection, BSONSerializationPack}
import reactivemongo.api.indexes.{Index, IndexType}

import scala.concurrent.{ExecutionContext, Future}

abstract class MongoIndexCreator(val reactiveMongoApi: ReactiveMongoApi)(implicit executionContext: ExecutionContext) extends MongoService {
  val service: SingleMongoCollectionService[_]
  lazy val collectionName: String = service.collectionName

  def index(name: String): Index.Aux[BSONSerializationPack.type] =
    Index(BSONSerializationPack)(
      key = Seq(s"$name-key" -> IndexType.Ascending),
      name = Some(name),
      unique = true,
      background = false,
      sparse = false,
      expireAfterSeconds = None,
      storageEngine = None,
      weights = None,
      defaultLanguage = None,
      languageOverride = None,
      textIndexVersion = None,
      sphereIndexVersion = None,
      bits = None,
      min = None,
      max = None,
      bucketSize = None,
      collation = None,
      wildcardProjection = None,
      version = None,
      partialFilter = None,
      options = BSONDocument.empty
    )

  def run() = {
    service.withServiceCollection(c => {
      for {db <- reactiveMongoApi.database
           _ = println(db.connection)
           _ <- db.collectionNames.flatMap {collections =>
             if (!collections.contains(collectionName)){
               println(s"creating ${collectionName}")
               c.indexesManager.create(index(collectionName))
               c.indexesManager.ensure(index(collectionName))
             } else {
               Future.successful(())
             }
           }
      } yield {}
    })
  }

  run()
}
