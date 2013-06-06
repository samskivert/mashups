//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import pythagoras.f.FloatMath

/** Something that takes action when the player moves. */
trait MOB extends Footed {
  def behave (jiva :Jivaloka, protag :FruitFly)
  override def depth = MOBDepth
}

class Frog (val start :Coord) extends Entity with MOB {
  val tongue = 2
  val size = 2
  var orient :Int = 0 // Up, Right, Down, Left

  def behave (jiva :Jivaloka, protag :FruitFly) {
    if (protag.alive && region(orient, tongue)(protag.coord)) jiva.chomp(this, protag)
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

  val foot = Coord.square(size)
  val viz = Viz(size, size)
    .circleF(1/4f, 3/4f, 1/4f, 0xFF336600).circleF(3/4f, 3/4f, 1/4f, 0xFF336600)
    .circleSF(1/2f, 1/2f, 1/2f, 0xFF336600)
    .circleF(1/4f, 1/4f, 1/8f, 0xFF336600).circleF(3/4f, 1/4f, 1/8f, 0xFF336600)

  private final val Rots = Array(0, FloatMath.PI/2, FloatMath.PI, 3*FloatMath.PI/2)
}

class Spider (val start :Coord) extends Entity with MOB {

  def behave (jiva :Jivaloka, protag :FruitFly) {
    // if the fly is in our range...
    if (protag.alive && coord.dist(protag.coord) <= range) {
      move(jiva, protag.coord) // jump on him
      jiva.chomp(this, protag) // and eat him up yum!
      // TODO: put spider to sleep for some period of time after eating (display zzzs?)
    }
    // otherwise just move randomly in our range
    else {
      val spots = coord.within(range).filter(jiva.pass.isPassable)
      if (!spots.isEmpty) {
        val spot = spots(jiva.rand.nextInt(spots.size))
        move(jiva, spot)
      }
    }
  }

  val foot = Coord.square(1)
  val viz = Viz(1, 1)
    .line(0, 0,    1, 1,    0xFF330066)
    .line(0, 1/3f, 1, 2/3f, 0xFF330066)
    .line(0, 2/3f, 1, 1/3f, 0xFF330066)
    .line(0, 1,    1, 0,    0xFF330066)
    .circleSF(1/2f, 1/2f, 2/5f, 0xFF330066)

  val range = 2
}
