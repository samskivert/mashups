//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

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

  private def apply (f :(Coord => Unit))(foot :Seq[Coord], origin :Coord) {
    foot map(_.add(origin)) filter(_ != null) foreach(f)
  }

  private val _pass = terrain.map(_.passable)
}
