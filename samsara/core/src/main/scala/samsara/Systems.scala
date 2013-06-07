//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core._

class Systems (jiva :Jivaloka) {

  val render = new System[Bodied](jiva) {
    override def onAdded (entity :Bodied) {
      entity.init(jiva)
    }
    override def onRemoved (entity :Bodied) {
      entity.destroy()
    }
    override protected def handles (entity :Entity) = entity.isInstanceOf[Bodied]
  }

  val pass = new System[Footed](jiva) {
    override def onAdded (entity :Footed) {
      if (entity.coord == null) println("Entity with null coord in onAdd? " + entity)
      else jiva.pass.makeImpass(entity.foot, entity.coord)
    }
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

  trait Move {
    def protag :FruitFly
  }
  val move = new System[FruitFly](jiva) with Move {
    var protag :FruitFly = _

    def move (dx :Int, dy :Int) {
      if (protag != null) {
        val coord = protag.coord.add(dx, dy)
        if (jiva.pass.isPassable(coord)) protag.move(jiva, coord)
        else if (dx == 0 && dy == -1 && protag.coord == jiva.level.exit) jiva.ascend(protag)
      }
    }

    override def onAdded (entity :FruitFly) {
      if (protag != null) throw new IllegalStateException("What? Two protagonists?")
      protag = entity
      // Constants.BaseMoves;
      jiva.movesLeft.update(protag.movesLeft)
    }
    override def onRemoved (entity :FruitFly) {
      if (protag != entity) throw new IllegalStateException("Who was that man?")
      protag = null
      jiva.tryHatch()
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

  trait Edibles {
    def chomp (region :Set[Coord]) :Boolean
  }
  val edibles = new System[Edible](jiva) with Edibles {
    def chomp (region :Set[Coord]) = {
      var chomped = false
      foreach { ed =>
        if (region(ed.coord)) {
          jiva.chomp(ed)
          chomped = true
        }
      }
      chomped
    }
    override protected def handles (entity :Entity) = entity.isInstanceOf[Edible]
  }

  trait Stompables {
    def stompables (region :Seq[Coord]) :Seq[Stompable]
  }
  val stompables = new System[Stompable](jiva) with Stompables {
    def stompables (region :Seq[Coord]) = {
      Seq() // TODO
    }
    override protected def handles (entity :Entity) = entity.isInstanceOf[Stompable]
  }

  val love = new System[Mate](jiva) {
    override protected def handles (entity :Entity) = entity.isInstanceOf[Mate]
    jiva.flyMove.connect { f :FruitFly => foreach(_.maybeMate(jiva, f)) }
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
