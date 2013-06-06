//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.Layer
import pythagoras.f.FloatMath

/** Something that exists on screen with a viz, dimensions and coords. */
trait Bodied {
  final val DecalDepth = 0
  final val PropDepth = 1
  final val MOBDepth = 2
  final val PlayerDepth = 3

  val start :Coord
  val viz   :Viz

  /** This body's current coordinates. */
  var coord :Coord = _

  /** This body's previous coordinates. */
  var ocoord :Coord = _

  /** This body's visualization. Initialized by the render system. */
  var layer :Layer = _

  /** Specifies the depth of this body's layer. */
  def depth :Float = DecalDepth

  /** Updates this body's coordinates, and moves its layer. (TODO: animate) */
  def move (jiva :Jivaloka, coord :Coord) {
    ocoord = this.coord
    this.coord = coord
    layer.setTranslation(coord.x * jiva.metrics.size + layer.originX,
                         coord.y * jiva.metrics.size + layer.originY)
    jiva.onMove.emit(this)
  }
}

trait Footed extends Bodied {
  val foot  :Seq[Coord]
}

/** Our current protagonist. */
class FruitFly (val start :Coord) extends Entity with Footed {
  /** Indicates that this fly is still alive.
    * MOBs who attack the fly will ignore it if some other MOB managed to kill it first. */
  var alive = true

  /** The item currently being carried by the fly (or null). */
  var item :Entity = _

  val foot = Coord.square(1)
  val viz = Viz(1, 1)
    .line(1/8f,  1/8f, 1/2f,   1/2f, 0xFF111111, 1)
    .line(1/16f, 1/3f, 15/16f, 2/3f, 0xFF111111, 1)
    .line(1/16f, 2/3f, 15/16f, 1/3f, 0xFF111111, 1)
    .line(1/2f,  1/2f, 7/8f,   1/8f,    0xFF111111, 1)
    .ellipseSF(1/4f, 0f, 1/2f, 1f, 0xFF111111, 0xFFCCCCCC)
    .polySF(0xFF111111, 0xFFCCCCCC, (1/2f, 1/4f), (1f, 1f), (1/2f, 3/4f), (0f, 1f))
  override def depth = PlayerDepth
}

/** A nest of eggs, from which our protagonists spawn. */
class Nest (val start :Coord, val eggs :Int) extends Entity with Bodied {
  def hatch (jiva :Jivaloka) {
    jiva.remove(this)
    if (eggs > 1) jiva.add(new Nest(start, eggs-1))
    jiva.add(new FruitFly(coord))
  }

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

class Exit (val start :Coord) extends Entity with Bodied {
  val viz = Viz(1, 1).roundRectSF(0.1f, 0.1f, 0.8f, 0.8f, 1/4f, 0xFFFFCC99)
}

class Splat (val start :Coord) extends Entity with Bodied {
  val viz = {
    val rando = new java.util.Random
    val angcs = Array.fill(20)((rando.nextFloat*2/5f, rando.nextFloat*FloatMath.PI*2))
    (Viz(1, 1) /: angcs) {
      case (v, (r, a)) =>
        v.circleF(1/2f + r*FloatMath.cos(a), 1/2f + r*FloatMath.sin(a), 1/6f, 0xFF990000)
    }
  }
}
