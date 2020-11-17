package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import graphql.{ApiRepo, Schema}
import play.api.libs.json._
import play.api.mvc._
import play.api.Configuration
import sangria.execution._
import sangria.parser.{QueryParser, SyntaxError}
import sangria.marshalling.playJson._
import sangria.execution.deferred.DeferredResolver
import sangria.renderer.SchemaRenderer
import sangria.slowlog.SlowLog

import scala.concurrent.Future
import scala.util.{Failure, Success}

class GraphQlController @Inject() (system: ActorSystem, config: Configuration, apiRepo: ApiRepo, schema: Schema) extends InjectedController {
  import system.dispatcher

  val googleAnalyticsCode = config.getOptional[String]("gaCode")

  def playground = Action {
    Ok(views.html.playground(googleAnalyticsCode))
  }

  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema(schema.StarWarsSchema))
  }

  def graphql(query: String, variables: Option[String], operation: Option[String]) = Action.async { request =>
    executeQuery(query, variables map parseVariables, operation, isTracingEnabled(request))
  }

  def graphqlBody = Action.async(parse.json) { request =>
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]

    val variables = (request.body \ "variables").toOption.flatMap {
      case JsString(vars) => Some(parseVariables(vars))
      case obj: JsObject => Some(obj)
      case _ => None
    }

    executeQuery(query, variables, operation, isTracingEnabled(request))
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "" || variables.trim == "null") Json.obj() else Json.parse(variables).as[JsObject]

  private def executeQuery(query: String, variables: Option[JsObject], operation: Option[String], tracing: Boolean) =
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) =>
        Executor.execute(schema.StarWarsSchema, queryAst, apiRepo,
          operationName = operation,
          variables = variables getOrElse Json.obj(),
          deferredResolver = DeferredResolver.fetchers(schema.characters),
          exceptionHandler = exceptionHandler,
          queryReducers = List(
            QueryReducer.rejectMaxDepth[ApiRepo](15),
            QueryReducer.rejectComplexQueries[ApiRepo](4000, (_, _) => TooComplexQueryError)),
          middleware = if (tracing) SlowLog.apolloTracing :: Nil else Nil)
          .map(Ok(_))
          .recover {
            case error: QueryAnalysisError => BadRequest(error.resolveError)
            case error: ErrorWithResolver => InternalServerError(error.resolveError)
          }

      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) =>
        Future.successful(BadRequest(Json.obj(
          "syntaxError" -> error.getMessage,
          "locations" -> Json.arr(Json.obj(
            "line" -> error.originalError.position.line,
            "column" -> error.originalError.position.column)))))

      case Failure(error) =>
        throw error
    }

  def isTracingEnabled(request: Request[_]) = request.headers.get("X-Apollo-Tracing").isDefined

  lazy val exceptionHandler = ExceptionHandler {
    case (_, error @ TooComplexQueryError) => HandledException(error.getMessage)
    case (_, error @ MaxQueryDepthReachedError(_)) => HandledException(error.getMessage)
  }

  case object TooComplexQueryError extends Exception("Query is too expensive.")
}
