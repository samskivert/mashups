//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core._

class Systems (jiva :Jivaloka, screen :LevelScreen, level :Level) {

  val render = new System[Bodied](jiva) {
    override def onAdded (entity :Bodied) {
      entity.layer = entity.viz.create(jiva.metrics).setDepth(entity.depth)
      entity.move(jiva, entity.start) // update layer position
      screen.layer.add(entity.layer)
    }
    override def onRemoved (entity :Bodied) {
      entity.layer.destroy()
      entity.layer = null
    }
    override protected def handles (entity :Entity) = entity.isInstanceOf[Bodied]
  }

  val pass = new System[Footed](jiva) {
    override def onRemoved (entity :Footed) {
      jiva.pass.resetPass(entity.foot, entity.coord)
    }
    override protected def handles (entity :Entity) = entity.isInstanceOf[Footed]

    jiva.onMove.connect(slot[Bodied] {
      case e :Footed =>
        if (e.ocoord != null) jiva.pass.resetPass(e.foot, e.ocoord)
        jiva.pass.makeImpass(e.foot, e.coord)
    })
  }

  val move = new System[FruitFly](jiva) {
    var protag :FruitFly = _

    def move (dx :Int, dy :Int) {
      if (protag != null) {
        val coord = protag.coord.add(dx, dy)
        if (jiva.pass.isPassable(coord)) {
          protag.move(jiva, coord)
          jiva.flyMove.emit(protag)
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
    jiva.flyMove.connect { f :FruitFly => foreach(_.behave(jiva, f)) }
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
