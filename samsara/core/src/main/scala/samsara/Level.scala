//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import scala.collection.mutable.ArrayBuffer

case class Level (depth :Int, terrain :Array[Terrain], entities :Seq[Entity])

/** Level-related static data. */
object Level {
  val width = 8
  val height = 12

  def random (depth :Int) :Level = random(System.currentTimeMillis, depth)

  def random (seed :Long, depth :Int) = {
    val rando = new java.util.Random(seed)
    val terrain = Array.fill[Terrain](width*height)(Dirt)

    // TODO: add rivers, puddles, etc.

    // we'll use this to track passability when randomly placing props
    val pass = new Passage(terrain)

    // choose an entry and exit point; mark them as impassable during level gen
    val entry = Coord(rando.nextInt(width), height-1)
    val exit = Coord(rando.nextInt(width), 0)
    pass.setImpass(exit)
    pass.setImpass(entry)

    val entities = ArrayBuffer[Entity]()
    // for now stick a nest on the entry location
    entities += new Nest(entry, 4)
    entities += new Exit(exit)

    // helper to place an entity, check for validity, then update passability
    def place (ent :Entity with Footed) = {
      if (!pass.isPassable(ent.foot, ent.start)) false
      else {
        pass.makeImpass(ent.foot, ent.start)
        // TODO: check that a route exists from entry to exit
        entities += ent
        true
      }
    }

    // helper to place N entities at random coordinates
    def placeN (count :Int, ew :Int, eh :Int, mkBody :(Coord => Entity with Footed)) {
      def loop (remain :Int, fails :Int) {
        if (remain > 0 && fails < 5) {
          val ent = mkBody(Coord(rando.nextInt(width-ew), rando.nextInt(height-eh)))
          if (place(ent)) loop(remain-1, fails)
          else loop(remain, fails+1)
        }
      }
      loop(count, 0)
    }

    // maybe put a big tree in one corners or on one side
    (rando.nextFloat match {
      case f if (f < 0.25f) => Some(new CornerTree(rando.nextInt(4)))
      case f if (f < 0.5f)  =>
        val y = Level.height/4+rando.nextInt(height/2)
        Some(if (rando.nextBoolean) new LeftTree(y) else new RightTree(y))
      case _ => None
    }) foreach place

    // now put some other trees (TODO: vary density based on depth? maybe cyclically)
    placeN(2+rando.nextInt(3), 2, 2, new Tree2(_))

    // put some MOBs in there (TODO: keep adding until depth based mob density is reached)
    placeN(1+rando.nextInt(2), 2, 2, new Frog(_))
    placeN(1+rando.nextInt(2), 1, 1, new AwakeSpider(_))

    Level(depth, terrain, entities)
  }
}
