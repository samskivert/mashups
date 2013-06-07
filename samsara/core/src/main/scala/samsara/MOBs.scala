//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import pythagoras.f.FloatMath

/** Something that takes action when the player moves. */
trait MOB extends Footed { self :Entity =>
  def behave (jiva :Jivaloka, protag :FruitFly)
  override def depth = MOBDepth
}

class Frog (
  var orient :Int
) extends Entity with MOB {
  val tongue = 2
  val size = 2

  def behave (jiva :Jivaloka, protag :FruitFly) {
    if (jiva.systems.edibles.chomp(region(orient, tongue))) () // we chomped, we're done
                                                               // (TODO: maybe sleep?
    // if they're to our left/right, turn left/right
    else if (region(dorient(orient, -1), 1)(protag.coord)) turn(-1)
    else if (region(dorient(orient,  1), 1)(protag.coord)) turn(1)
  }

  def dorient (orient :Int, delta :Int) = (orient + 4 + delta) % 4

  def region (orient :Int, depth :Int) = (orient match {
    case 0 => Coord.region(coord.x, coord.y - depth, size, depth)
    case 1 => Coord.region(coord.x + size, coord.y, depth, size)
    case 2 => Coord.region(coord.x, coord.y + size, size, depth)
    case _ => Coord.region(coord.x - depth, coord.y, depth, size)
  }).toSet

  def turn (delta :Int) {
    orient = dorient(orient, delta)
    layer.setRotation(Rots(orient))
  }

  override protected def position (jiva :Jivaloka) {
    super.position(jiva)
    layer.setRotation(Rots(orient))
  }

  val foot = Coord.square(size)
  def viz = Viz(size, size, 0xFF336600, 0xFFFFFFFF)
    .circleF(1/4f, 3/4f, 1/4f, 0xFF336600).circleF(3/4f, 3/4f, 1/4f, 0xFF336600)
    .circleSF(1/2f, 1/2f, 1/2f)
    .circleF(1/4f, 1/4f, 1/8f, 0xFF336600).circleF(3/4f, 1/4f, 1/8f, 0xFF336600)

  private final val Rots = Array(0, FloatMath.PI/2, FloatMath.PI, 3*FloatMath.PI/2)
}

abstract class Spider extends Entity with MOB with Edible {
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

class AwakeSpider extends Spider {

  def behave (jiva :Jivaloka, protag :FruitFly) {
    // if the fly is in our range...
    if (protag.alive && coord.dist(protag.coord) <= range) {
      move(jiva, protag.coord) // jump on him
      jiva.stomp(protag) // and eat him up yum!
      jiva.remove(this) // replace ourselves with a sleeping spider
      jiva.add(new SleepingSpider(5+jiva.rand.nextInt(5)).at(coord))
    }
    // otherwise just move randomly in our range (TODO: bias toward protagonist)
    else {
      val spots = coord.within(1).filter(jiva.pass.isPassable)
      if (!spots.isEmpty) {
        val spot = spots(jiva.rand.nextInt(spots.size))
        move(jiva, spot)
      }
    }
  }

  val range = 2
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
