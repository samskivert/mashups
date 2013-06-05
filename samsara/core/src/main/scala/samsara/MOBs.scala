//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import pythagoras.f.FloatMath

/** Something that takes action when the player moves. */
trait MOB {
  def behave (jiva :Jivaloka, protag :FruitFly)
}

class Frog (val start :Coord) extends Entity with Bodied with MOB {
  var orient :Int = 0 // Up, Right, Down, Left

  def behave (jiva :Jivaloka, protag :FruitFly) {
    if (protag.alive && region(orient, 2)(protag.coord)) jiva.chomp(this, protag)
    // if they're to our left/right, turn left/right
    else if (region(dorient(orient, -1), 1)(protag.coord)) turn(-1)
    else if (region(dorient(orient,  1), 1)(protag.coord)) turn(1)
  }

  def dorient (orient :Int, delta :Int) = (orient + 4 + delta) % 4

  def region (orient :Int, depth :Int) = (orient match {
    case 0 => Coord.region(coord.x, coord.y - depth, width, depth)
    case 1 => Coord.region(coord.x + width, coord.y, depth, height)
    case 2 => Coord.region(coord.x, coord.y + height, width, depth)
    case _ => Coord.region(coord.x - depth, coord.y, depth, height)
  }).toSet

  def turn (delta :Int) {
    orient = dorient(orient, delta)
    layer.setRotation(Rots(orient))
  }

  // from Bodied
  val width = 2
  val height = 2
  val viz = Viz(2, 2)
    .circleF(1/4f, 3/4f, 1/4f, 0xFF336600).circleF(3/4f, 3/4f, 1/4f, 0xFF336600)
    .circleSF(1/2f, 1/2f, 1/2f, 0xFF336600)
    .circleF(1/4f, 1/4f, 1/8f, 0xFF336600).circleF(3/4f, 1/4f, 1/8f, 0xFF336600)

  private final val Rots = Array(0, FloatMath.PI/2, FloatMath.PI, 3*FloatMath.PI/2)
}
