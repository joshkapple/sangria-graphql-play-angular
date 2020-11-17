package hero

import javax.inject.Inject

import scala.concurrent.ExecutionContext

class HeroApi @Inject()(val heroService: HeroService)(implicit executionContext: ExecutionContext){

}
