package graphql

import hero.{Droid, Episode, Human}
import javax.inject.{Inject, Singleton}
import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema.{Argument, EnumType, EnumValue, Field, InterfaceType, ListType, ObjectType, OptionInputType, OptionType, ProjectionName, Projector, Schema, StringType, fields, interfaces}

import scala.concurrent.Future

@Singleton
class Schema @Inject()(){
  /**
   * Resolves the lists of characters. These resolutions are batched and
   * cached for the duration of a query.
   */
  val characters = Fetcher.caching(
    (ctx: ApiRepo, ids: Seq[String]) =>
      Future.successful(ids.flatMap(id => ctx.characterRepo.getHuman(id) orElse ctx.characterRepo.getDroid(id))))(HasId(_.id))

  val EpisodeEnum = EnumType(
    "Episode",
    Some("One of the films in the Star Wars Trilogy"),
    List(
      EnumValue("NEWHOPE",
        value = Episode.NEWHOPE,
        description = Some("Released in 1977.")),
      EnumValue("EMPIRE",
        value = Episode.EMPIRE,
        description = Some("Released in 1980.")),
      EnumValue("JEDI",
        value = Episode.JEDI,
        description = Some("Released in 1983."))))

  val Character: InterfaceType[ApiRepo, hero.Character] =
    InterfaceType(
      "Character",
      "A character in the Star Wars Trilogy!!!",
      () => fields[ApiRepo, hero.Character](
        Field("id", StringType,
          Some("The id of the character."),
          resolve = _.value.id),
        Field("name", OptionType(StringType),
          Some("The name of the character."),
          resolve = _.value.name),
        Field("friends", ListType(Character),
          Some("The friends of the character, or an empty list if they have none."),
          complexity = Some((_, _, children) => 100 + 1.5 * children),
          resolve = ctx => characters.deferSeqOpt(ctx.value.friends)),
        Field("appearsIn", OptionType(ListType(OptionType(EpisodeEnum))),
          Some("Which movies they appear in."),
          resolve = _.value.appearsIn map (e => Some(e)))
      ))

  val Human =
    ObjectType(
      "Human",
      "A humanoid creature in the Star Wars universe.",
      interfaces[ApiRepo, Human](Character),
      fields[ApiRepo, Human](
        Field("id", StringType,
          Some("The id of the human."),
          resolve = _.value.id),
        Field("name", OptionType(StringType),
          Some("The name of the human."),
          resolve = _.value.name),
        Field("friends", ListType(Character),
          Some("The friends of the human, or an empty list if they have none."),
          complexity = Some((_, _, children) => 100 + 1.5 * children),
          resolve = ctx => characters.deferSeqOpt(ctx.value.friends)),
        Field("appearsIn", OptionType(ListType(OptionType(EpisodeEnum))),
          Some("Which movies they appear in."),
          resolve = _.value.appearsIn map (e => Some(e))),
        Field("homePlanet", OptionType(StringType),
          Some("The home planet of the human, or null if unknown."),
          resolve = _.value.homePlanet)
      ))

  val Droid = ObjectType(
    "Droid",
    "A mechanical creature in the Star Wars universe.",
    interfaces[ApiRepo, Droid](Character),
    fields[ApiRepo, Droid](
      Field("id", StringType,
        Some("The id of the droid."),
        tags = ProjectionName("_id") :: Nil,
        resolve = _.value.id),
      Field("name", OptionType(StringType),
        Some("The name of the droid."),
        resolve = ctx => Future.successful(ctx.value.name)),
      Field("friends", ListType(Character),
        Some("The friends of the droid, or an empty list if they have none."),
        complexity = Some((_, _, children) => 100 + 1.5 * children),
        resolve = ctx => characters.deferSeqOpt(ctx.value.friends)),
      Field("appearsIn", OptionType(ListType(OptionType(EpisodeEnum))),
        Some("Which movies they appear in."),
        resolve = _.value.appearsIn map (e => Some(e))),
      Field("primaryFunction", OptionType(StringType),
        Some("The primary function of the droid."),
        resolve = _.value.primaryFunction)
    ))

  val ID = Argument("id", StringType, description = "id of the character")

  val EpisodeArg = Argument("episode", OptionInputType(EpisodeEnum),
    description = "If omitted, returns the hero of the whole saga. If provided, returns the hero of that particular episode.")

  val Query = ObjectType(
    "Query", fields[ApiRepo, Unit](
      Field("hero", Character,
        arguments = EpisodeArg :: Nil,
        deprecationReason = Some("Use `human` or `droid` fields instead"),
        resolve = (ctx) => ctx.ctx.characterRepo.getHero(ctx.arg(EpisodeArg))),
      Field("human", OptionType(Human),
        arguments = ID :: Nil,
        resolve = ctx => ctx.ctx.characterRepo.getHuman(ctx arg ID)),
      Field("droid", Droid,
        arguments = ID :: Nil,
        resolve = Projector((ctx, f) => ctx.ctx.characterRepo.getDroid(ctx arg ID).get))
    ))

  val StarWarsSchema = Schema(Query)
}
