//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

import react.{Slot, UnitSlot}

/** Global stuffs; mostly implicits to make using React/TriplePlay more pleasant. */
package object samsara {

  implicit def toSlot[A,B] (f :Function1[A,B]) = new Slot[A] {
    override def onEmit (value :A) = f(value)
  }

  implicit def toUnitSlot (action : =>Unit) = new UnitSlot {
    override def onEmit = action
  }
}
