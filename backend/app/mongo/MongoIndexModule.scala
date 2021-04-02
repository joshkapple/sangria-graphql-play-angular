package mongo

import com.google.inject.AbstractModule
import hero.JediIndexCreator

class MongoIndexModule extends AbstractModule {
  override def configure() = {
    bind(classOf[JediIndexCreator]).asEagerSingleton()
  }
}
