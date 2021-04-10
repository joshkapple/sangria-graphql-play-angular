package mongo

import com.google.inject.AbstractModule
import character.CharacterIndexCreator

class MongoIndexModule extends AbstractModule {
  override def configure() = {
    bind(classOf[CharacterIndexCreator]).asEagerSingleton()
  }
}
