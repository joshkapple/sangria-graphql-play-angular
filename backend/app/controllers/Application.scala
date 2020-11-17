package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.mvc._
import play.api.Configuration


class Application @Inject() (system: ActorSystem, config: Configuration) extends InjectedController {
  import system.dispatcher

  val googleAnalyticsCode = config.getOptional[String]("gaCode")
  val defaultGraphQLUrl = config.getOptional[String]("defaultGraphQLUrl").getOrElse(s"http://localhost:${config.getOptional[Int]("http.port").getOrElse(9000)}/graphql")

  def index = Action {
    Ok(views.html.index(googleAnalyticsCode,defaultGraphQLUrl))
  }
}
