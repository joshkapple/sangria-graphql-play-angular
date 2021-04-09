package graphql

import hero.{Droid, Episode, Human, Jedi}
import mongo.MongoObjectId
import javax.inject.{Inject, Singleton}
import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema.{
  Argument,
  EnumType,
  EnumValue,
  Field,
  IDType,
  InterfaceType,
  ListType,
  ObjectType,
  OptionInputType,
  OptionType,
  ProjectionName,
  Projector,
  Schema,
  StringType,
  fields,
  interfaces
}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import sangria.macros.derive._

@Singleton
class Schema @Inject()() {

  /**
    * Resolves the lists of characters. These resolutions are batched and
    * cached for the duration of a query.
    */
  val characters = Fetcher.caching((ctx: ApiRepo, ids: Seq[MongoObjectId]) => ctx.cs.byIds(ids))(HasId(_._id))

  val EpisodeEnum = EnumType(
    "Episode",
    Some("One of the films in the Star Wars Trilogy"),
    List(
      EnumValue("NEWHOPE", value = Episode.NEWHOPE, description = Some("Released in 1977.")),
      EnumValue("EMPIRE", value = Episode.EMPIRE, description = Some("Released in 1980.")),
      EnumValue("JEDI", value = Episode.JEDI, description = Some("Released in 1983."))
    )
  )

  val CharacterEnum = EnumType(
    "CharacterType",
    Some("Sub type of the character"),
    List(
      EnumValue("Droid", value = hero.CharacterType.Droid),
      EnumValue("Human", value = hero.CharacterType.Human),
      EnumValue("Jedi", value = hero.CharacterType.Jedi)
  ))

  val Character: InterfaceType[ApiRepo, hero.Character] =
    InterfaceType(
      "Character",
      "A character in the Star Wars Trilogy!!!",
      () =>
        fields[ApiRepo, hero.Character](
          Field("id", IDType, Some("The id of the character."), resolve = _.value._id.$oid),
          Field("name", OptionType(StringType), Some("The name of the character."), resolve = _.value.name),
          Field(
            "friends",
            ListType(Character),
            Some("The friends of the character, or an empty list if they have none."),
            complexity = Some((_, _, children) => 100 + 1.5 * children),
            resolve = ctx => characters.deferSeqOpt(ctx.value.friends)
          ),
          Field(
            "appearsIn",
            OptionType(ListType(OptionType(EpisodeEnum))),
            Some("Which movies they appear in."),
            resolve = _.value.appearsIn map (e => Some(e))),
          Field(
            "characterType",
            CharacterEnum,
            Some("Which movies they appear in."),
            resolve = _.value.characterType))
    )

  val Human =
    ObjectType(
      "Human",
      "A humanoid creature in the Star Wars universe.",
      interfaces[ApiRepo, Human](Character),
      fields[ApiRepo, Human](
        Field("id", IDType, Some("The id of the human."), resolve = _.value._id.$oid),
        Field("name", OptionType(StringType), Some("The name of the human."), resolve = _.value.name),
        Field(
          "friends",
          ListType(Character),
          Some("The friends of the human, or an empty list if they have none."),
          complexity = Some((_, _, children) => 100 + 1.5 * children),
          resolve = ctx => characters.deferSeqOpt(ctx.value.friends)
        ),
        Field(
          "appearsIn",
          OptionType(ListType(OptionType(EpisodeEnum))),
          Some("Which movies they appear in."),
          resolve = _.value.appearsIn map (e => Some(e))),
        Field(
          "homePlanet",
          OptionType(StringType),
          Some("The home planet of the human, or null if unknown."),
          resolve = _.value.homePlanet)
      )
    )

  val Droid = ObjectType(
    "Droid",
    "A mechanical creature in the Star Wars universe.",
    interfaces[ApiRepo, Droid](Character),
    fields[ApiRepo, Droid](
      Field(
        "id",
        IDType,
        Some("The id of the droid."),
        tags = ProjectionName("_id") :: Nil,
        resolve = _.value._id.$oid),
      Field(
        "name",
        OptionType(StringType),
        Some("The name of the droid."),
        resolve = ctx => Future.successful(ctx.value.name)),
      Field(
        "friends",
        ListType(Character),
        Some("The friends of the droid, or an empty list if they have none."),
        complexity = Some((_, _, children) => 100 + 1.5 * children),
        resolve = ctx => characters.deferSeqOpt(ctx.value.friends)
      ),
      Field(
        "appearsIn",
        OptionType(ListType(OptionType(EpisodeEnum))),
        Some("Which movies they appear in."),
        resolve = _.value.appearsIn map (e => Some(e))),
      Field(
        "primaryFunction",
        OptionType(StringType),
        Some("The primary function of the droid."),
        resolve = _.value.primaryFunction)
    )
  )

  implicit val episodeFieldType: EnumType[Episode.Value] = deriveEnumType[Episode.Value]()

  implicit val MongoFieldType: ObjectType[Unit, MongoObjectId] = deriveObjectType[Unit, MongoObjectId](
    RenameField("$oid", "id")
  )

  val Jedi = ObjectType(
    "Jedi",
    "A jedi",
    interfaces[ApiRepo, Jedi](Character),
    fields[ApiRepo, Jedi](
      Field("id", IDType, Some("The id of the jedi."), tags = ProjectionName("_id") :: Nil, resolve = _.value._id.$oid),
      Field(
        "name",
        OptionType(StringType),
        Some("The name of the droid."),
        resolve = ctx => Future.successful(ctx.value.name)),
      Field(
        "friends",
        ListType(Character),
        Some("The friends of the droid, or an empty list if they have none."),
        complexity = Some((_, _, children) => 100 + 1.5 * children),
        resolve = ctx => characters.deferSeqOpt(ctx.value.friends)
      ),
      Field(
        "appearsIn",
        OptionType(ListType(OptionType(EpisodeEnum))),
        Some("Which movies they appear in."),
        resolve = _.value.appearsIn map (e => Some(e))),
      Field(
        "primaryFunction",
        OptionType(StringType),
        Some("The primary function of the droid."),
        resolve = _.value.primaryFunction)
    )
  )

  val ID = Argument("id", IDType, description = "id of the character")

  val name = Argument("name", StringType, description = "name of the character")

  val EpisodeArg = Argument(
    "episode",
    OptionInputType(EpisodeEnum),
    description =
      "If omitted, returns the hero of the whole saga. If provided, returns the hero of that particular episode."
  )

  val Query = ObjectType(
    "Query",
    fields[ApiRepo, Unit](
      Field(
        "all",
        ListType(Character),
        arguments = Nil,
        deprecationReason = Some("Use `human` or `droid` fields instead"),
        resolve = (ctx) => ctx.ctx.cs.all()),
      Field(
        "human",
        OptionType(Human),
        arguments = ID :: Nil,
        resolve = ctx => ctx.ctx.cs.getHuman(ctx arg ID.toString)),
      Field(
        "droid",
        OptionType(Droid),
        arguments = ID :: Nil,
        resolve = Projector((ctx, f) => ctx.ctx.cs.getDroid(ctx arg ID.toString))),
      Field(
        "jedi",
        OptionType(Jedi),
        arguments = name :: Nil,
        resolve = Projector((ctx, f) => ctx.ctx.cs.jediByName(ctx arg name)))
    )
  )

  val StarWarsSchema = Schema(Query)
}
