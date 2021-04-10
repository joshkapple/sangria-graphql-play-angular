package graphql

import character.{CharacterRepo, CharacterService}

import javax.inject.{Inject, Singleton}

@Singleton
class ApiRepo @Inject()(val cs: CharacterService){
}
