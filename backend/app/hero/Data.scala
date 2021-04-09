package hero

import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType
import mongo.MongoObjectId
import reactivemongo.api.bson.BSONObjectID
import mongo.Serializers._

object Episode extends Enumeration {
  val NEWHOPE, EMPIRE, JEDI = Value
}

object CharacterType extends Enumeration {
  val Human, Droid, Jedi = Value
}

sealed trait Character {
  val characterType: CharacterType.Value
  def id: MongoObjectId
  def name: Option[String]
  def friends: List[MongoObjectId]
  def appearsIn: List[Episode.Value]
}

case class Human(id: MongoObjectId,
                 name: Option[String],
                 friends: List[MongoObjectId],
                 appearsIn: List[Episode.Value],
                 homePlanet: Option[String])
    extends Character {
  override val characterType = CharacterType.Human
}

case class Droid(id: MongoObjectId,
                 name: Option[String],
                 friends: List[MongoObjectId],
                 appearsIn: List[Episode.Value],
                 primaryFunction: Option[String])
    extends Character {
  override val characterType = CharacterType.Droid
}

case class Jedi(id: MongoObjectId,
                name: Option[String],
                friends: List[MongoObjectId],
                appearsIn: List[Episode.Value],
                primaryFunction: Option[String])
    extends Character {
  override val characterType = CharacterType.Jedi
}

object CharacterRepo {
  def loadHumans() = List(
    Human(
      id = BSONObjectID.generate(),
      name = Some("Luke Skywalker"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = Some("Tatooine")
    ),
    Human(
      id = BSONObjectID.generate(),
      name = Some("Darth Vader"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = Some("Tatooine")),
    Human(
      id = BSONObjectID.generate(),
      name = Some("Han Solo"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = None),
    Human(
      id = BSONObjectID.generate(),
      name = Some("Leia Organa"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = Some("Alderaan")
    ),
    Human(
      id = BSONObjectID.generate(),
      name = Some("Wilhuff Tarkin"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = None)
  )

  def loadDroids() = List(
    Droid(
      id = BSONObjectID.generate(),
      name = Some("C-3PO"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      primaryFunction = Some("Protocol")
    ),
    Droid(
      id = BSONObjectID.generate(),
      name = Some("R2-D2"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      primaryFunction = Some("Astromech")
    )
  )
}
