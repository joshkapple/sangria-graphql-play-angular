package hero

import javax.inject.Inject

class HeroService @Inject()(){
  val characterRepo = new CharacterRepo
}
