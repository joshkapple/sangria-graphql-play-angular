package graphql

import hero.{CharacterRepo, HeroApi}
import javax.inject.{Inject, Singleton}

@Singleton
class ApiRepo @Inject()(heroApi: HeroApi){
  lazy val characterRepo = heroApi.heroService.characterRepo
}
