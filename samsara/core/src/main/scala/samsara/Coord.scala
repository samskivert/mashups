//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

/** A grid location on a level. */
class Coord (val x :Int, val y :Int) {

  /** Returns this coordinate's index in a flat array. */
  def index = y * Level.width + x

  /** Returns {@code this} plus {@code that}. */
  def add (that :Coord) = Coord(x + that.x, y + that.y)

  /** Returns {@code this} plus {@code dx, dy}. */
  def add (dx :Int, dy :Int) = Coord(x + dx, y + dy)

  /** Returns the Manhattan distance between `this` and `coord`. */
  def dist (coord :Coord) = math.abs(x - coord.x) + math.abs(y - coord.y)

  /** Returns all coords with Manhattan distance from `this` <= `range`. OOB coords are omitted. */
  def within (range :Int) :Seq[Coord] = for {
    yy <- -range to range ; remain = range-math.abs(yy) ; xx <- -remain to remain ;
    c = Coord(x+xx, y+yy) ; if (c != null)
  } yield c

  /** Folds `f` over the neighbors of this coord. */
  def foldn[A] (z :A)(f :((A, Coord) => A)) :A = {
    val u = add(0, -1)
    val zu = if (u != null) f(z, u) else z
    val d = add(0, 1)
    val zd = if (d != null) f(zu, d) else zu
    val l = add(-1, 0)
    val zl = if (l != null) f(zd, l) else zd
    val r = add(1, 0)
    if (r != null) f(zl, r) else zl
  }

  override def toString = s"+$x+$y"
  override def hashCode = x ^ y
  override def equals (other :Any) = other match {
    case oc :Coord => oc.x == x && oc.y == y
    case _ => false
  }
}

/** A factory/cache for coords. */
object Coord {

  /** Returns the coord at {@code x, y}, or null if that's out of bounds. */
  def apply (x :Int, y :Int) = {
    if (x < 0 || x >= Level.width || y < 0 || y >= Level.height) null
    else _coords(y * Level.width + x)
  }

  /** Returns the coord for the specified index. Note: does no bounds checking. */
  def fromIndex (idx :Int) = _coords(idx)

  /** Returns all coordinates in the square from `[0, 0]` to `[size, size]`. */
  def square (size :Int) = region(0, 0, size, size)

  /** Returns all coordinates in the rectangel from `[0, 0]` to `[width, height]`. */
  def rect (width :Int, height :Int) = region(0, 0, width, height)

  /** Returns all coords in the specified rectangular region. */
  def region (x :Int, y :Int, width :Int, height :Int) :Seq[Coord] =
    for (xx <- 0 until width; yy <- 0 until height) yield Coord(x+xx, y+yy)

  private val _coords = Array.tabulate(Level.width * Level.height) { c =>
    new Coord(c % Level.width, c / Level.width)
  }
}
