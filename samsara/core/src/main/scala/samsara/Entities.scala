//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.Layer

/** A body component is something that exists on screen with a viz, dimensions and coords. */
trait Bodied {

  val start  :Coord
  val width  :Int
  val height :Int
  val viz    :Viz

  /** This body's current coordinates. */
  var coord :Coord = _

  /** This body's visualization. Initialized by the render system. */
  var layer :Layer = _

  /** Updates this body's coordinates, and moves its layer. (TODO: animate) */
  def move (coord :Coord, metrics :Metrics) {
    this.coord = coord
    layer.setTranslation(coord.x * metrics.size + layer.originX,
                         coord.y * metrics.size + layer.originY)
  }
}

trait Footed extends Bodied {
  val footprint :Seq[Coord]
}

trait MOB {
  def behave (player :Player)
}

class Player (val start :Coord, val viz :Viz) extends Entity with Bodied {

  /** The item currently being carried by the player (or null). */
  var item :Entity = _

  // from Bodied
  val width = 1
  val height = 1
}

class Prop (
  val start :Coord, val width :Int, val height :Int, val viz :Viz,
  val footprint :Seq[Coord]
) extends Entity with Bodied with Footed {
  def this (start :Coord, width :Int, height :Int, viz :Viz) = this(
    start, width, height, viz, Entities.makeFoot(width, height))
}

class Frog (val start :Coord, val viz :Viz) extends Entity with Bodied with MOB {

  var orient :Int = 0 // Up

  def behave (player :Player) {
  }

  // from Bodied
  val width = 2
  val height = 2
}

object Entities {

  def makeFoot (width :Int, height :Int) :Seq[Coord] =
    for (x <- 0 until width; y <- 0 until height) yield Coord(x, y)
}
