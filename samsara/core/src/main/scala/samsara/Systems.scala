//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core._

class RenderSystem (screen :LevelScreen) extends System[Bodied](screen.world) {

  override def onAdded (entity :Bodied) {
    entity.layer = entity.viz.create(screen.metrics)
    entity.move(entity.start, screen.metrics) // update layer position
    screen.layer.add(entity.layer)
  }

  override def onRemoved (entity :Bodied) {
    entity.layer.destroy()
    entity.layer = null
  }

  override protected def handles (entity :Entity) = entity.isInstanceOf[Bodied]
}

class PassabilitySystem (screen :LevelScreen,
                         terrain :Array[Terrain]) extends System[Footed](screen.world) {

  /** Returns true if `coord` is passable, false otherwise. */
  def isPassable (coord :Coord) = coord != null && _pass(coord.index)

  val _pass = terrain.map(_.passable)

  override def onAdded (entity :Footed) {
    apply(entity) { c => _pass(c.index) = false }
  }

  override def onRemoved (entity :Footed) {
    apply(entity) { c => _pass(c.index) = terrain(c.index).passable }
  }

  override protected def handles (entity :Entity) = entity.isInstanceOf[Footed]

  private def apply (entity :Footed)(f :(Coord => Unit)) {
    val origin = entity.start
    entity.footprint map(_.add(origin)) filter(_ != null) foreach(f)
  }
}

class MovementSystem (screen :LevelScreen) extends System[Player](screen.world) {

  var player :Player = _

  screen.keyDown.connect((_ :Key) match {
    case Key.UP    => move(0, -1)
    case Key.DOWN  => move(0, 1)
    case Key.LEFT  => move(-1, 0)
    case Key.RIGHT => move(1, 0)
    case _ => // nada
  })

  def move (dx :Int, dy :Int) {
    val coord = player.coord.add(dx, dy)
    if (screen.pass.isPassable(coord)) {
      player.move(coord, screen.metrics)
      screen.onMove.emit(player)
    }
  }

  override def onAdded (entity :Player) {
    if (player != null) throw new IllegalStateException("What? Two players?")
    player = entity
  }

  override def onRemoved (entity :Player) {
    if (player != entity) throw new IllegalStateException("Who was that man?")
    player = null
  }

  override protected def handles (entity :Entity) = entity.isInstanceOf[Player]
}

class BehaviorSystem (screen :LevelScreen) extends System[MOB](screen.world) {

  screen.onMove.connect { p :Player => foreach(_.behave(p)) }

  override protected def handles (entity :Entity) = entity.isInstanceOf[MOB]
}
