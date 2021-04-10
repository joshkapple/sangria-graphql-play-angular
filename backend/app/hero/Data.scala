package hero

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
  def _id: MongoObjectId
  def name: Option[String]
  def friends: List[MongoObjectId]
  def appearsIn: List[Episode.Value]
}

case class Human(_id: MongoObjectId,
                 name: Option[String],
                 friends: List[MongoObjectId],
                 appearsIn: List[Episode.Value],
                 homePlanet: Option[String])
    extends Character {
}

case class Droid(_id: MongoObjectId,
                 name: Option[String],
                 friends: List[MongoObjectId],
                 appearsIn: List[Episode.Value],
                 primaryFunction: Option[String])
    extends Character {
}

case class Jedi(_id: MongoObjectId,
                name: Option[String],
                friends: List[MongoObjectId],
                appearsIn: List[Episode.Value],
                primaryFunction: Option[String])
    extends Character {
}

object CharacterRepo {
  def loadHumans() = List(
    Human(
      _id = BSONObjectID.generate(),
      name = Some("Luke Skywalker"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = Some("Tatooine")
    ),
    Human(
      _id = BSONObjectID.generate(),
      name = Some("Darth Vader"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = Some("Tatooine")),
    Human(
      _id = BSONObjectID.generate(),
      name = Some("Han Solo"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = None),
    Human(
      _id = BSONObjectID.generate(),
      name = Some("Leia Organa"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = Some("Alderaan")
    ),
    Human(
      _id = BSONObjectID.generate(),
      name = Some("Wilhuff Tarkin"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      homePlanet = None)
  )

  def loadDroids() = List(
    Droid(
      _id = BSONObjectID.generate(),
      name = Some("C-3PO"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      primaryFunction = Some("Protocol")
    ),
    Droid(
      _id = BSONObjectID.generate(),
      name = Some("R2-D2"),
      friends = Nil,
      appearsIn = List(Episode.NEWHOPE, Episode.EMPIRE, Episode.JEDI),
      primaryFunction = Some("Astromech")
    )
  )
}
