//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import scala.collection.mutable.{ArrayBuffer, Map => MMap}

case class Level (depth :Int, terrain :Array[Terrain], exit :Coord, entities :Seq[Entity]) {
  def add (pt :FruitFly) = copy(entities = entities :+ pt.at(Coord(pt.coord.x, Level.height-1)))
}

/** Level-related static data. */
object Level {
  val width = 8
  val height = 12

  def random (depth :Int, ascender :Option[FruitFly]) = {
    val rando = new java.util.Random
    val entities = ArrayBuffer[Entity]()
    val terrain = Array.fill[Terrain](width*height)(Dirt)
    // TODO: add streams, puddles, etc.

    // we'll use this to track passability when randomly placing props
    val pass = new Passage(terrain)

    // pick an exit point
    val exit = Coord(rando.nextInt(width), 0)
    entities += new Exit().at(exit)
    pass.setImpass(exit)

    // helper to place an entity, check for validity, then update passability
    def place (ent :Entity with Footed) = {
      if (!pass.isPassable(ent.foot, ent.coord)) false
      else {
        pass.makeImpass(ent.foot, ent.coord)
        // TODO: check that a route exists from protagonist to exit
        entities += ent
        true
      }
    }

    // helper to place N entities at random coordinates
    def placeN (count :Int, ew :Int, eh :Int, mkBody : =>Entity with Footed) {
      def loop (remain :Int, fails :Int) {
        if (remain > 0 && fails < 10) {
          val ent = mkBody.at(Coord(rando.nextInt(width-ew), rando.nextInt(height-eh)))
          if (place(ent)) loop(remain-1, fails)
          else loop(remain, fails+1)
        }
      }
      loop(count, 0)
    }

    // if we have an ascended protagonist...
    ascender match {
      // note their entry position and add a mate
      case Some(protag) =>
        pass.setImpass(Coord(protag.coord.x, height-1))
        // hackily call the mate height/3 tall to prevent it being placed in the bottom 1/3
        placeN(1, 1, height/3, new Mate)

      // otherwise add a nest in the bottom row (we're on level zero)
      case None =>
        val nest = new Nest(3, Constants.BaseMoves).at(Coord(rando.nextInt(width), height-1))
        entities += nest
        pass.setImpass(nest.coord)
    }

    // maybe put a big tree in one corners or on one side
    (rando.nextFloat match {
      case f if (f < 0.5f) =>
        val corner = rando.nextInt(4)
        Some(new CornerTree(corner).at(corner match {
          case 0 => Coord(0, 0)
          case 1 => Coord(width-2, 0)
          case 2 => Coord(width-2, height-2)
          case 3 => Coord(0, height-2)
        }))
      case _  =>
        val left = rando.nextBoolean
        val x = if (left) 0 else width-2
        val y = Level.height/4+rando.nextInt(height/2)
        val tree = if (left) new LeftTree else new RightTree
        Some(tree.at(Coord(x, y)))
    }) foreach place

    // now put some other trees (TODO: vary density based on depth? maybe cyclically)
    placeN(2+rando.nextInt(3), 2, 2, new Tree2)

    // put some MOBs in there (TODO: keep adding until depth based mob density is reached)
    val frogs = if (depth == 0) 0 else if (depth < 5) 1 else
      if (depth < 10) 1+rando.nextInt(2) else 2
    placeN(frogs, 2, 2, new Frog(rando.nextInt(4)))
    val spiders = if (depth < 3) 0 else if (depth < 8) 1 else 2
    placeN(spiders, 1, 1, new AwakeSpider)

    Level(depth, terrain, exit, entities)
  }
}

class LevelDB {

  def level0 = Level.random(0, None)

  def get (depth :Int, protag :FruitFly) :Level = {
    _levels.getOrElse(depth, Level.random(depth, Some(protag))).add(protag)
  }

  def store (level :Level) {
    _levels.put(level.depth, level)
  }

  def pop (level :Level) = {
    // keep one level beyond where we're popping
    _levels -= (level.depth+1)
    _levels(level.depth-1)
  }

  private val _levels = MMap[Int,Level]()
}
