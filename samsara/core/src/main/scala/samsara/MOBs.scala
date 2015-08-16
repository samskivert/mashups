//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import pythagoras.f.FloatMath

/** Something that takes action when the player moves. */
trait MOB extends Footed with Living { self :Entity =>
  def behave (jiva :Jivaloka, protag :FruitFly)
  override def depth = MOBDepth
}

trait Eater { self :Entity =>
  def eat (jiva :Jivaloka, edible :Edible)
}

class Frog (
  startOrient :Int
) extends Entity with MOB with Eater {

  val tongue = 2
  val size = 2
  orient = startOrient

  def behave (jiva :Jivaloka, protag :FruitFly) {
    // if they're to our left/right, turn left/right
    if (_left(protag.coord)) setOrient(dorient(orient, -1))
    else if (_right(protag.coord)) setOrient(dorient(orient, 1))
  }

  def eat (jiva :Jivaloka, edible :Edible) {
    if (edible.alive && _front(edible.coord)) jiva.chomp(edible) // (TODO: maybe sleep?)
  }

  override def setOrient (norient :Int) :Int = {
    _front = region(norient, tongue)
    _left = region(dorient(norient, -1), 1)
    _right = region(dorient(norient, 1), 1)
    super.setOrient(norient)
  }

  def region (orient :Int, depth :Int) = (orient match {
    case 0 => Coord.region(coord.x, coord.y - depth, size, depth)
    case 1 => Coord.region(coord.x + size, coord.y, depth, size)
    case 2 => Coord.region(coord.x, coord.y + size, size, depth)
    case _ => Coord.region(coord.x - depth, coord.y, depth, size)
  }).toSet

  var alive = true
  val foot = Coord.square(size)
  def viz = Viz(size, size, 0xFF336600, 0xFFFFFFFF)
    .circleF(1/4f, 3/4f, 1/4f, 0xFF336600).circleF(3/4f, 3/4f, 1/4f, 0xFF336600)
    .circleSF(1/2f, 1/2f, 1/2f)
    .circleF(1/4f, 1/4f, 1/8f, 0xFF336600).circleF(3/4f, 1/4f, 1/8f, 0xFF336600)

  private var _front :Set[Coord] = _
  private var _left  :Set[Coord] = _
  private var _right :Set[Coord] = _
}

abstract class Spider extends Entity with MOB {

  var alive = true
  val foot = Coord.square(1)
  def viz = Viz(1, 1, 0xFF330066, 0xFFFFFFFF)
    .line(0, 0,    1, 1)
    .line(0, 1/3f, 1, 2/3f)
    .line(0, 2/3f, 1, 1/3f)
    .line(0, 1,    1, 0)
    .circleSF(1/2f, 1/2f, 2/5f)
  override def scale = 0.75f
}

class AwakeSpider extends Spider with Edible {

  val range = 2

  def behave (jiva :Jivaloka, protag :FruitFly) {
    val pdist = coord.dist(protag.coord)
    // if the fly is in our range...
    if (protag.alive && pdist <= range) {
      move(jiva, protag.coord) // jump on him
      jiva.stomp(protag)       // eat the fly
      jiva.remove(this)        // replace ourselves with a sleeping spider
      jiva.add(new SleepingSpider(7+jiva.rand.nextInt(7)).at(coord))
    }
    // otherwise...
    else {
      val spots = coord.within(1).filter(jiva.pass.isPassable)
      if (!spots.isEmpty) {
        val spot = spots(jiva.rand.nextInt(spots.size))
        // TOO HARD: if he's kind of close, move toward him, otherwise just move randomly
        /// if (protag.alive && pdist <= range+2) spots.minBy(protag.coord.dist)
        move(jiva, spot)
      }
    }
  }
}

class SleepingSpider (var turns :Int) extends Spider {

  def behave (jiva :Jivaloka, protag :FruitFly) {
    turns -= 1
    if (turns <= 0) {
      jiva.remove(this) // wake up!
      jiva.add(new AwakeSpider().at(coord))
    }
  }

  override def viz = super.viz.textF("zzz", 0xFF330066)
}
