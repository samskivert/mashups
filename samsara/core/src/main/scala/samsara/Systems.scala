//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core._

class Systems (jiva :Jivaloka, screen :LevelScreen, level :Level) {

  val render = new System[Bodied](jiva) {
    override def onAdded (entity :Bodied) {
      entity.layer = entity.viz.create(jiva.metrics)
      entity.move(entity.start, jiva.metrics) // update layer position
      screen.layer.add(entity.layer)
    }
    override def onRemoved (entity :Bodied) {
      entity.layer.destroy()
      entity.layer = null
    }
    override protected def handles (entity :Entity) = entity.isInstanceOf[Bodied]
  }

  trait Pass {
    /** Returns true if `coord` is passable, false otherwise. */
    def isPassable (coord :Coord) :Boolean
  }
  val pass = new System[Footed](jiva) with Pass {
    def isPassable (coord :Coord) = coord != null && _pass(coord.index)

    override def onAdded (entity :Footed) {
      apply(entity) { c => _pass(c.index) = false }
    }
    override def onRemoved (entity :Footed) {
      apply(entity) { c => _pass(c.index) = level.terrain(c.index).passable }
    }
    override protected def handles (entity :Entity) = entity.isInstanceOf[Footed]

    private def apply (entity :Footed)(f :(Coord => Unit)) {
      val origin = entity.start
      entity.footprint map(_.add(origin)) filter(_ != null) foreach(f)
    }

    private val _pass = level.terrain.map(_.passable)
  }

  val move = new System[FruitFly](jiva) {
    var protag :FruitFly = _

    def move (dx :Int, dy :Int) {
      if (protag != null) {
        val coord = protag.coord.add(dx, dy)
        if (pass.isPassable(coord)) {
          protag.move(coord, jiva.metrics)
          jiva.onMove.emit(protag)
        }
      }
    }

    override def onAdded (entity :FruitFly) {
      if (protag != null) throw new IllegalStateException("What? Two protagonists?")
      protag = entity
    }
    override def onRemoved (entity :FruitFly) {
      if (protag != entity) throw new IllegalStateException("Who was that man?")
      protag = null
    }
    override protected def handles (entity :Entity) = entity.isInstanceOf[FruitFly]

    jiva.keyDown.connect((_ :Key) match {
      case Key.UP    => move(0, -1)
      case Key.DOWN  => move(0, 1)
      case Key.LEFT  => move(-1, 0)
      case Key.RIGHT => move(1, 0)
      case _ => // nada
    })
  }

  val behave = new System[MOB](jiva) {
    override protected def handles (entity :Entity) = entity.isInstanceOf[MOB]
    jiva.onMove.connect { f :FruitFly => foreach(_.behave(jiva, f)) }
  }

  trait Hatcher {
    /** Hatches a new fly from the nest on the level (if any). */
    def hatch ()
  }
  val hatcher = new System[Nest](jiva) with Hatcher {
    def hatch () = foreach(_.hatch(jiva))
    override protected def handles (entity :Entity) = entity.isInstanceOf[Nest]
  }
}
