//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import scala.collection.mutable.{BitSet => MBitSet}

abstract class Entity {
  private[samsara] var index :Int = 0
}

abstract class System[A] (world :World) {

  def onAdded (entity :A) {
  }

  def onRemoved (entity :A) {
  }

  def foreach (f :(A => Unit)) {
    val max = world.maxIndex
    var idx = 0 ; while (idx < max) {
      if (_ents(idx)) f(world.entity(idx).asInstanceOf[A])
      idx += 1
    }
  }

  protected def handles (entity :Entity) :Boolean

  private[samsara] def entityAdded (entity :Entity) {
    if (handles(entity)) {
      _ents += entity.index
      onAdded(entity.asInstanceOf[A])
    }
  }

  private[samsara] def entityRemoved (entity :Entity) {
    if (_ents(entity.index)) {
      _ents -= entity.index
      onRemoved(entity.asInstanceOf[A])
    }
  }

  world.addSystem(this)
  private[this] val _ents = new MBitSet()
}

class World {

  /** Adds `entity` to the world. */
  def add (entity :Entity) {
    // stick this entity into a slot we know to be available
    entity.index = _nextIndex
    _ents(_nextIndex) = entity

    // now look for the next empty entity slot; expanding our array if needed
    var idx = _nextIndex+1
    while (_ents(idx) != null) {
      idx += 1
      if (idx == _ents.length) {
        val ents = new Array[Entity](math.min(_ents.size+1024, _ents.size*2))
        System.arraycopy(_ents, 0, ents, 0, _ents.length)
        _ents = ents
      }
    }
    _nextIndex = idx

    // tell our systems about the addition
    var sys = _syss ; while (sys != Nil) {
      sys.head.entityAdded(entity) ; sys = sys.tail
    }
  }

  /** Removes `entity` from the world. */
  def remove (entity :Entity) {
    _ents(entity.index) = null
    // insert the next added entity here if this index is lower than our current target
    if (entity.index < _nextIndex) _nextIndex = entity.index
    // tell our systems about the removal
    var sys = _syss ; while (sys != Nil) {
      sys.head.entityRemoved(entity) ; sys = sys.tail
    }
  }

  private[samsara] def addSystem (sys :System[_]) {
    _syss = sys :: _syss
  }

  private[samsara] def maxIndex :Int = _ents.size

  private[samsara] def entity (index :Int) = _ents(index)

  private[this] var _ents = new Array[Entity](256)
  private[this] var _nextIndex = 0
  private[this] var _syss = List[System[_]]()
}
