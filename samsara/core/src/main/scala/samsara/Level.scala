//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import scala.collection.mutable.{ArrayBuffer, Map => MMap}

case class Level (depth :Int, terrain :Array[Terrain], exit :Coord, entities :Seq[Entity])

/** Level-related static data. */
object Level {
  val width = 8
  val height = 12

  def random (depth :Int, ascender :Option[FruitFly]) = {
    val rando = new java.util.Random
    val entities = ArrayBuffer[Entity]()
    val terrain = Array.fill[Terrain](width*height)(Dirt)
    // TODO: add rivers, puddles, etc.

    // we'll use this to track passability when randomly placing props
    val pass = new Passage(terrain)

    // if we have an ascended protagonist, use 'em
    val px = ascender.map(_.coord.x).getOrElse(rando.nextInt(width))
    val protag = ascender.getOrElse(new FruitFly).at(Coord(px, height-1))
    println("Random " + ascender + " " + protag)
    entities += protag
    pass.setImpass(protag.coord)

    // pick an exit point
    val exit = Coord(rando.nextInt(width), 0)
    entities += new Exit().at(exit)
    pass.setImpass(exit)

    // for now stick a nest somewhere randomly on the level
    val nest = Coord(rando.nextInt(width), rando.nextInt(height))
    entities += new Nest(2).at(nest)
    pass.setImpass(nest)

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
        if (remain > 0 && fails < 5) {
          val ent = mkBody.at(Coord(rando.nextInt(width-ew), rando.nextInt(height-eh)))
          if (place(ent)) loop(remain-1, fails)
          else loop(remain, fails+1)
        }
      }
      loop(count, 0)
    }

    // maybe put a big tree in one corners or on one side
    (rando.nextFloat match {
      case f if (f < 0.25f) =>
        val corner = rando.nextInt(4)
        Some(new CornerTree(corner).at(corner match {
          case 0 => Coord(0, 0)
          case 1 => Coord(width-2, 0)
          case 2 => Coord(width-2, height-2)
          case 3 => Coord(0, height-2)
        }))
      case f if (f < 0.5f)  =>
        val left = rando.nextBoolean
        val x = if (left) 0 else width-2
        val y = Level.height/4+rando.nextInt(height/2)
        val tree = if (left) new LeftTree else new RightTree
        Some(tree.at(Coord(x, y)))
      case _ => None
    }) foreach place

    // now put some other trees (TODO: vary density based on depth? maybe cyclically)
    placeN(2+rando.nextInt(3), 2, 2, new Tree2)

    // put some MOBs in there (TODO: keep adding until depth based mob density is reached)
    placeN(1+rando.nextInt(2), 2, 2, new Frog)
    placeN(1+rando.nextInt(2), 1, 1, new AwakeSpider)

    Level(depth, terrain, exit, entities)
  }
}

class LevelDB {

  def get (depth :Int, protag :Option[FruitFly]) :Level =
    _levels.getOrElse(depth, Level.random(depth, protag))

  def store (level :Level) {
    _levels.put(level.depth, level)
  }

  private val _levels = MMap[Int,Level]()
}
