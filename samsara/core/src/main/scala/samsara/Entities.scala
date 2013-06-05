//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.Layer
import pythagoras.f.FloatMath

/** Something that exists on screen with a viz, dimensions and coords. */
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

/** Our current protagonist. */
class FruitFly (val start :Coord) extends Entity with Bodied {
  /** Indicates that this fly is still alive.
    * MOBs who attack the fly will ignore it if some other MOB managed to kill it first. */
  var alive = true

  /** The item currently being carried by the fly (or null). */
  var item :Entity = _

  val width = 1
  val height = 1
  val viz = Viz(1, 1).circleSF(1/2f, 1/2f, 1/2f)
}

/** A nest of eggs, from which our protagonists spawn. */
class Nest (val start :Coord, val eggs :Int) extends Entity with Bodied {
  def hatch (jiva :Jivaloka) {
    jiva.remove(this)
    if (eggs > 1) jiva.add(new Nest(start, eggs-1))
    jiva.add(new FruitFly(coord))
  }

  val width = 1
  val height = 1
  val viz = eggs match {
    case 4 => Viz(1, 1).
        circleF(1/4f, 1/4f, 1/4f, 0xFFFFCC99).circleF(3/4f, 1/4f, 1/4f, 0xFFFFCC99).
        circleF(1/4f, 3/4f, 1/4f, 0xFFFFCC99).circleF(3/4f, 3/4f, 1/4f, 0xFFFFCC99)
    case 3 => Viz(1, 1).circleF(1/2f, 1/4f, 1/4f, 0xFFFFCC99).
        circleF(1/4f, 3/4f, 1/4f, 0xFFFFCC99).circleF(3/4f, 3/4f, 1/4f, 0xFFFFCC99)
    case 2 => Viz(1, 1).circleF(1/2f, 1/4f, 1/4f, 0xFFFFCC99).
        circleF(1/2f, 3/4f, 1/4f, 0xFFFFCC99)
    case 1 => Viz(1, 1).circleF(1/2f, 1/2f, 1/4f, 0xFFFFCC99)
  }
}

class Splat (val start :Coord) extends Entity with Bodied {
  val width = 1
  val height = 1
  val rando = new java.util.Random
  val viz = {
    val angcs = Array.fill(20)((rando.nextFloat*2/5f, rando.nextFloat*FloatMath.PI*2))
    (Viz(1, 1) /: angcs) {
      case (v, (r, a)) =>
        v.circleF(1/2f + r*FloatMath.cos(a), 1/2f + r*FloatMath.sin(a), 1/6f, 0xFF990000)
    }
  }
}
