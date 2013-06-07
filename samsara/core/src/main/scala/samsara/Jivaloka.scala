//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.Key
import playn.core.util.Clock
import react.{Signal, Value, IntValue}

class Jivaloka (
  val game    :Samsara,
  val screen  :GameScreen,
  val levels  :LevelDB,
  val level   :Level
) extends World {

  val keyDown = Signal.create[Key]()
  val onPaint = Signal.create[Clock]()
  val onChomp = Signal.create[MOB]()
  val onMove  = Signal.create[Bodied]()
  val flyMove = Signal.create[FruitFly]()
  val movesLeft = new IntValue(0)

  val rand    = new scala.util.Random // TODO: seeded? save seed?
  val pass    = new Passage(level.terrain)
  val systems = new Systems(this)

  def start () {
    // if we have no protagonist, hatch one!
    if (systems.move.protag == null) tryHatch()
  }

  def tryHatch () {
    // println("Trying hatch on " + level.depth)
    // try hatching a new fruit fly
    systems.hatcher.hatch()
    // if we still have no protagonist, we were unable to find a nest from which to hatch
    if (systems.move.protag == null) {
      // if this is level zero, then it's game over
      if (level.depth == 0) game.screens.remove(screen, game.screens.slide.up)
      // otherwise pop back to the previous screen and try to hatch from there
      else {
        levels.store(level.copy(entities = entities)) // save this world for later
        game.screens.replace(new GameScreen(game, levels, levels.pop(level)), game.screens.slide.up)
      }
    }
  }

  def hatch (coord :Coord, moves :Int, dist :Int = 0) {
    rand.shuffle(coord.within(dist)).find(pass.isPassable) match {
      case None    => hatch(coord, moves, dist+1)
      case Some(c) => add(new FruitFly(moves).at(c))
    }
  }

  def chomp (target :Edible) {
    target.alive = false
    remove(target.entity)
    add(new Splat(0xFF990000).at(target.coord))
  }

  def stomp (target :Stompable) {
    target.alive = false
    remove(target.entity)
    add(new Splat(0xFF660000).at(target.coord))
  }

  def croak (protag :FruitFly) {
    protag.alive = false
    remove(protag)
    add(new Splat(0xFF666666).at(protag.coord)) // TODO: use a tombstone or something
  }

  def ascend (protag :FruitFly) {
    // hide our protagonist (we can't remove them or it will trigger a hatch)
    protag.layer.setVisible(false)
    // save this world for later (minus our protagonist)
    levels.store(level.copy(entities = entities filter(_ != protag)))
    game.screens.replace(new GameScreen(game, levels, levels.get(level.depth+1, protag)),
                         game.screens.slide.down)
  }
}
