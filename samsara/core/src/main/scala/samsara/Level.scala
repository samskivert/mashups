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
  val height = 14

  def random (depth :Int, ascender :Option[FruitFly]) :Level = {
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
    abstract class Placer (count :Int, ew :Int, eh :Int) {
      def mkBody :Entity with Footed
      def check (ent :Footed) = true
      def go () {
        def loop (remain :Int, fails :Int) {
          if (remain > 0 && fails < 10) {
            val ent = mkBody.at(Coord(rando.nextInt(width-ew), rando.nextInt(height-eh)))
            if (check(ent) && place(ent)) loop(remain-1, fails)
            else loop(remain, fails+1)
          }
        }
        loop(count, 0)
      }
    }

    // if we have an ascended protagonist...
    val start = ascender match {
      // note their entry position and add a mate
      case Some(protag) =>
        // hackily call the mate height/3 tall to prevent it being placed in the bottom 1/3
        new Placer(1, 1, height/3) {
          def mkBody = new Mate
        }.go()
        Coord(protag.coord.x, height-1)

      // otherwise add a nest in the bottom row (we're on level zero)
      case None =>
        val nest = new Nest(3, Constants.BaseMoves).at(Coord(rando.nextInt(width), height-1))
        entities += nest
        nest.coord
    }
    pass.setImpass(start)

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
    new Placer(2+rando.nextInt(3), 2, 2) {
     def mkBody = new Tree2
    }.go()

    // put some MOBs in there (TODO: keep adding until depth based mob density is reached)
    val frogs =
      if (depth < 2) 0
      else if (depth < 5) 1
      else if (depth < 10) 1+rando.nextInt(2)
      else 2
    new Placer(frogs, 2, 2) {
      def mkBody = new Frog(rando.nextInt(4))
      // make sure the exit is not right next to this frog; that can result in badness
      override def check (frog :Footed) = {
        if (tooClose(frog.coord, exit)) {
          println(s"Frog too close to exit ($exit) ${frog.coord}") ; false }
        else if (tooClose(frog.coord, start)) {
          println(s"Frog too close to start ($start) ${frog.coord}") ; false }
        else true
      }
      def tooClose (fc :Coord, c :Coord) = {
        def in (v :Int, low :Int, high :Int) = (v >= low) && (v < high)
        val above = c.y == fc.y-1 ; val below = c.y == fc.y+2
        val left  = c.x == fc.x-1 ; val right = c.y == fc.x+2
        in(c.x - fc.x, 0, 2) && (above || below) || in(c.y - fc.y, 0, 2) && (left || right)
      }
    }.go()

    val spiders =
      if (depth < 3) 0
      else if (depth < 8) 1
      else 2
    new Placer(spiders, 1, 1) {
      def mkBody = new AwakeSpider
      override def check (spider :Footed) = {
        val byStart = spider.coord.within(1).toSet.contains(start)
        if (byStart) println(s"Spider too close to start ($start) ${spider.coord}")
        !byStart
      }
    }.go()

    if (pass.canReach(start, exit)) Level(depth, terrain, exit, entities)
    else {
      println("Oops, can't get to exit; reboot!")
      random(depth, ascender)
    }
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
