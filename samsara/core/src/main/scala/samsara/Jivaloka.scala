//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core._
import react.{Signal, IntValue}
import tripleplay.util.StyledText

class Jivaloka (
  val game    :Samsara,
  val screen  :GameScreen,
  val levels  :LevelDB,
  val level   :Level
) extends World {

  val keyDown = Signal.create[Key]()
  val onTap   = Signal.create[Pointer.Event]()
  val onFlick = Signal.create[(Int,Int)]()
  val onPaint = Signal.create[Clock]()
  val onChomp = Signal.create[MOB]()
  val onMove  = Signal.create[Bodied]()
  val flyMove = Signal.create[FruitFly]()
  val movesLeft = new IntValue(0)

  val anims   = new IntValue(0)
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
    if (systems.move.protag == null) game.plat.exec.invokeLater(new Runnable {
      // defer our rollback one frame to give entities a chance to settle down before we save them
      def run () {
        // if we still have animations pending, "busy wait" until they're done
        if (anims.get > 0) game.plat.exec.invokeLater(this)
        // if this is level zero, then it's game over
        else if (level.depth == 0) game.screens.remove(screen, game.screens.slide.up)
        // otherwise pop back to the previous screen and try to hatch from there
        else {
          levels.store(level.copy(entities = entities)) // save this world for later
          game.screens.replace(new GameScreen(game, levels, levels.pop(level)),
                               game.screens.slide.up)
        }
      }
    })
  }

  def hatch (coord :Coord, moves :Int) {
    val reach = pass.reachable(level.exit)
    def loop (dist :Int) {
      rand.shuffle(coord.within(dist)).find(c => pass.isPassable(c) && reach(c.index)) match {
        case None    => loop(dist+1)
        case Some(c) => add(new FruitFly(moves).at(c))
      }
    }
    loop(0)
  }

  def chomp (target :Edible) {
    if (!target.alive) {
      println("Refusing to chomp dead target " + target)
      Thread.dumpStack()
    } else {
      println("Chomping " + target)
      anim(target.coord, "Chomp!", 0xFFFFFFFF, 24)
      target.alive = false
      remove(target.entity)
      add(new Splat(0xFF990000).at(target.coord))
    }
  }

  def stomp (target :Stompable) {
    if (!target.alive) {
      println("Refusing to stomp dead target " + target)
      Thread.dumpStack()
    } else {
      anim(target.coord, "Splat!", 0xFFFFFFFF, 24)
      target.alive = false
      remove(target.entity)
      add(new Splat(0xFF660000).at(target.coord))
    }
  }

  def croak (protag :FruitFly) {
    anim(protag.coord, "Croak!", 0xFFFFFFFF, 24)
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

  def anim (coord :Coord, text :String, color :Int, size :Float) {
    val tlayer = StyledText.span(game.plat.graphics, text, UI.animCfg(color, size)).toLayer()
    tlayer.setOrigin(tlayer.width/2, tlayer.height/2)
    tlayer.setDepth(100)
    screen.center(tlayer, coord)
    anims.increment(1)
    screen.iface.anim.add(screen.layer, tlayer).`then`.
      tweenScale(tlayer).to(1.5f).in(500).`then`.
      tweenAlpha(tlayer).to(0f).in(250).`then`.
      increment(anims, -1).`then`.
      dispose(tlayer)
  }
}
