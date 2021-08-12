package mongo

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.{BSONCollection, BSONSerializationPack}
import reactivemongo.api.indexes.{Index, IndexType}

import scala.concurrent.{ExecutionContext, Future}

abstract class MongoIndexCreator(val reactiveMongoApi: ReactiveMongoApi)(implicit executionContext: ExecutionContext) extends MongoService {
  val service: SingleMongoCollectionService[_]
  lazy val collectionName: String = service.collectionName

  def index(name: String, key: Seq[(String, IndexType)]): Index.Aux[BSONSerializationPack.type] =
    Index(BSONSerializationPack)(
      key = key,
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
    service.collection(c => {
      for {db <- reactiveMongoApi.database
           _ = println(db.connection)
           _ = db.collectionNames.foreach(cNames => println(cNames))
           _ <- db.collectionNames.flatMap {collections =>
             if (!collections.contains(collectionName)){
               println(s"creating ${collectionName}")
               c.create().recover{ case _ => ()}
             } else {
               Future.successful(())
             }
           }
          //indices <- c.indexesManager.list()
          _ = {
            c.indexesManager.create(index(collectionName, Seq(("_id", IndexType.Ascending))))
            c.indexesManager.ensure(index(collectionName, Seq(("_id", IndexType.Ascending))))
          }
      } yield {}
    })
  }

  run()
}
