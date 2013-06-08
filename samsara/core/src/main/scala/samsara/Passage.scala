//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import scala.collection.mutable.BitSet

/** Tracks whether coordinates are passable. */
class Passage (terrain :Array[Terrain]) {

  /** Returns true if `c` is in bounds and passable. */
  val isPassable = (c :Coord) => c != null && _pass(c.index)

  /** Returns true if all (non-null) coordinates in the supplied footprint are passable. */
  def isPassable (foot :Seq[Coord], origin :Coord) :Boolean = foot forall { c =>
    val oc = c.add(origin)
    oc == null || _pass(oc.index)
  }

  def setPass   (c :Coord) = _pass(c.index) = true
  def setImpass (c :Coord) = _pass(c.index) = false
  def reset     (c :Coord) = _pass(c.index) = terrain(c.index).passable

  /** Marks the supplied footprint as passable. */
  val makePass = apply(setPass) _

  /** Marks the supplied footprint as impassable. */
  val makeImpass = apply(setImpass) _

  /** Resets the passability of the supplied footprint to terrain passability. */
  val resetPass = apply(reset) _

  def reach = _reach // TEMP

  /** Returns the indexes of all coords reachable from `coord`. */
  def reachable (from :Coord) :BitSet = {
    val reachable = BitSet(Level.width*Level.height)
    def loop (cs :List[Coord]) {
      if (!cs.isEmpty) {
        reachable += cs.head.index
        loop(cs.head.foldn(cs.tail) { (cs, c) =>
          if (isPassable(c) && !reachable(c.index)) c :: cs else cs })
      }
    }
    loop(from :: Nil)
    reachable
  }

  /** Returns true if `to` can be reached from `from`. */
  def canReach (from :Coord, to :Coord) :Boolean = {
    val toidx = to.index
    val opass = _pass(toidx) // force to to be passable and use `reachable`
    try { _pass(toidx) = true ; reachable(from)(toidx) }
    finally { _pass(toidx) = opass }
  }

  private def apply (f :(Coord => Unit))(foot :Seq[Coord], origin :Coord) {
    foot map(_.add(origin)) filter(_ != null) foreach(f)
  }

  private val _pass = terrain.map(_.passable)
  private val _reach = _pass.clone
}
