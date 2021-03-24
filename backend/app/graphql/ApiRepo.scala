package graphql

import hero.{CharacterRepo, HeroApi, JediService}

import javax.inject.{Inject, Singleton}

@Singleton
class ApiRepo @Inject()(heroApi: HeroApi, js: JediService){
  lazy val characterRepo = heroApi.heroService.characterRepo
  lazy val jediService = js
}
