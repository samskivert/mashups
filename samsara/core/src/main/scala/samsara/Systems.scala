//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import com.artemis.systems.EntityProcessingSystem
import com.artemis.utils.ImmutableBag
import com.artemis.{Aspect, Entity, EntitySystem, World}
import playn.core._
import playn.core.util.Clock
import scala.collection.mutable.ArrayBuffer
import tripleplay.util.DestroyableBag

class RenderSystem (screen :LevelScreen) extends EntitySystem(
  Aspect.getAspectForAll(classOf[Body])) {

  screen.world.setSystem(this)
  val _bm = screen.world.getMapper(classOf[Body])

  override def inserted (e :Entity) {
    val body = _bm.get(e)
    body.layer = body.viz.create(screen.metrics)
    body.move(body.coord, screen.metrics) // update layer position
    screen.layer.add(body.layer)
  }

  override def removed (e :Entity) {
    val body = _bm.get(e)
    body.layer.destroy()
    body.layer = null
  }

  override def processEntities (entities :ImmutableBag[Entity]) {} // unused
  override def checkProcessing = false
}

class PassabilitySystem (screen :LevelScreen, terrain :Array[Terrain]) extends EntitySystem(
  Aspect.getAspectForAll(classOf[Body], classOf[Footprint])) {

  /** Returns true if `coord` is passable, false otherwise. */
  def isPassable (coord :Coord) = coord != null && _pass(coord.index)

  screen.world.setSystem(this)
  val _bm = screen.world.getMapper(classOf[Body])
  val _fm = screen.world.getMapper(classOf[Footprint])
  val _pass = terrain.map(_.passable)

  override def inserted (e :Entity) {
    apply(e) { c => _pass(c.index) = false }
  }

  override def removed (e :Entity) {
    apply(e) { c => _pass(c.index) = terrain(c.index).passable }
  }

  private def apply (e :Entity)(f :(Coord => Unit)) {
    val origin = _bm.get(e).coord
    _fm.get(e).coords map(_.add(origin)) filter(_ != null) foreach(f)
  }

  override def processEntities (entities :ImmutableBag[Entity]) {} // unused
  override def checkProcessing = false
}

class MovementSystem (screen :LevelScreen) extends EntitySystem(
  Aspect.getAspectForAll(classOf[Player])) {

  screen.world.setSystem(this)
  val _pm = screen.world.getMapper(classOf[Player])
  val _bm = screen.world.getMapper(classOf[Body])
  var _player :Entity = _

  screen.keyDown.connect((_ :Key) match {
    case Key.UP    => move(0, -1)
    case Key.DOWN  => move(0, 1)
    case Key.LEFT  => move(-1, 0)
    case Key.RIGHT => move(1, 0)
    case _ => // nada
  })

  def move (dx :Int, dy :Int) {
    val body = _bm.get(_player)
    val coord = body.coord.add(dx, dy)
    if (screen.pass.isPassable(coord)) body.move(coord, screen.metrics)
  }

  override def inserted (e :Entity) {
    if (_player != null) throw new IllegalStateException("What? Two players?")
    _player = e
  }

  override def removed (e :Entity) {
    if (_player != e) throw new IllegalStateException("Who was that man?")
    _player = null
  }

  override def processEntities (entities :ImmutableBag[Entity]) {} // unused
  override def checkProcessing = false
}
