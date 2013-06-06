//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.{Layer, GroupLayer}
import pythagoras.f.FloatMath

/** Something that exists on screen with a viz, dimensions and coords. */
trait Bodied { self :Entity =>

  final val DecalDepth = 1
  final val PropDepth = 2
  final val MOBDepth = 3
  final val PlayerDepth = 4

  /** Generates this body's visualization. */
  def viz :Viz

  /** This body's current coordinates. */
  var coord :Coord = _

  /** This body's previous coordinates. */
  var ocoord :Coord = _

  /** This body's visualization. Initialized by the render system. */
  var layer :Layer = _

  /** Specifies the depth of this body's layer. */
  def depth :Float = DecalDepth

  /** Specifies the scale to use for the body's image. */
  def scale :Float = 1

  /** Configures this entity's starting coordinate. */
  def at (start :Coord) :this.type = {
    coord = start
    this
  }

  /** Creates and positions this body's viz. Sets starting coord if needed. */
  def init (jiva :Jivaloka) {
    layer = viz.create(jiva.screen.metrics).setDepth(depth).setScale(scale)
    position(jiva)
    jiva.screen.layer.add(layer)
  }

  /** Updates this body's coordinates, and moves its layer. */
  def move (jiva :Jivaloka, coord :Coord) {
    ocoord = this.coord
    this.coord = coord
    position(jiva)
    jiva.onMove.emit(this)
  }

  /** Destroys this body's viz. */
  def destroy () {
    layer.destroy()
    layer = null
  }

  private def position (jiva :Jivaloka) {
    val size = jiva.screen.metrics.size
    layer.setTranslation(coord.x * size + layer.originX, coord.y * size + layer.originY)
  }
}

trait Footed extends Bodied { self :Entity =>
  val foot :Seq[Coord]
}

/** Our current protagonist. */
class FruitFly extends Entity with Footed {
  /** Indicates that this fly is still alive.
    * MOBs who attack the fly will ignore it if some other MOB managed to kill it first. */
  var alive = true

  /** The item currently being carried by the fly (or null). */
  var item :Entity = _

  val foot = Coord.square(1)
  def viz = Viz(1, 1)
    .line(1/8f,  1/8f, 1/2f,   1/2f, 0xFF111111, 1)
    .line(1/16f, 1/3f, 15/16f, 2/3f, 0xFF111111, 1)
    .line(1/16f, 2/3f, 15/16f, 1/3f, 0xFF111111, 1)
    .line(1/2f,  1/2f, 7/8f,   1/8f,    0xFF111111, 1)
    .ellipseSF(1/4f, 0f, 1/2f, 1f, 0xFF111111, 0xFFCCCCCC)
    .polySF(0xFF111111, 0xFFCCCCCC, (1/2f, 1/4f), (1f, 1f), (1/2f, 3/4f), (0f, 1f))

  override def depth = PlayerDepth
  override def scale = 0.6f
  override def toString = s"Fly($coord)"
}

/** A nest of eggs, from which our protagonists spawn. */
class Nest (val eggs :Int) extends Entity with Bodied {
  def hatch (jiva :Jivaloka) {
    jiva.remove(this)
    if (eggs > 1) jiva.add(new Nest(eggs-1).at(coord))
    jiva.add(new FruitFly().at(coord))
    // jiva.moveLives.update(
  }

  def viz = eggs match {
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

class Exit extends Entity with Bodied {
  def viz = Viz(1, 1).roundRectSF(0.1f, 0.1f, 0.8f, 0.8f, 1/4f, 0xFFFFCC99)
}

class Splat extends Entity with Bodied {
  def viz = {
    val rando = new java.util.Random
    val angcs = Array.fill(20)((rando.nextFloat*2/5f, rando.nextFloat*FloatMath.PI*2))
    (Viz(1, 1) /: angcs) {
      case (v, (r, a)) =>
        v.circleF(1/2f + r*FloatMath.cos(a), 1/2f + r*FloatMath.sin(a), 1/6f, 0xFF990000)
    }
  }
}
