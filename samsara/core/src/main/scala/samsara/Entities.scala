//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.scene.{Layer, GroupLayer}
import pythagoras.f.FloatMath

/** Something that exists on screen with a viz, dimensions and coords. */
trait Bodied { self :Entity =>

  final val DecalDepth = 10
  final val PropDepth = 11
  final val MOBDepth = 12
  final val PlayerDepth = 13

  /** Generates this body's visualization. */
  def viz :Viz

  /** This body's current coordinates. */
  var coord :Coord = _

  /** This body's previous coordinates. */
  var ocoord :Coord = _

  /** This body's visualization. Initialized by the render system. */
  var layer :Layer = _

  /** This body's orientation. */
  var orient :Int = 0

/** Specifies the depth of this body's layer. */
  def depth :Float = DecalDepth

  /** Specifies the scale to use for the body's image. */
  def scale :Float = 1

  /** Configures this entity's starting coordinate. */
  def at (start :Coord) :this.type = {
    coord = start
    this
  }

  /** Creates and positions this body's viz. */
  def init (jiva :Jivaloka) {
    layer = viz.create(jiva.game.plat, jiva.screen.metrics).setDepth(depth).setScale(scale)
    position(jiva)
    jiva.screen.layer.add(layer)
    setOrient(orient)
  }

  /** Updates this body's coordinates, and moves its layer. */
  def move (jiva :Jivaloka, coord :Coord) {
    ocoord = this.coord
    this.coord = coord
    position(jiva)
    jiva.onMove.emit(this)
  }

  def setOrient (norient :Int) :Int = {
    orient = norient
    layer.setRotation(Rots(orient))
    norient
  }

  def dorient (orient :Int, delta :Int) = (orient + 4 + delta) % 4

  /** Destroys this body's viz. */
  def destroy () {
    if (saved != null) {
      println("Double destroy!" + this)
      println("Saved:")
      saved.printStackTrace(System.err)
      println("Current:")
      Thread.dumpStack()
    } else {
      saved = new Throwable
      layer.close()
      layer = null
    }
  }

  var saved :Throwable = _

  protected def position (jiva :Jivaloka) {
    jiva.screen.position(layer, coord)
    layer.setRotation(Rots(orient))
  }

  protected final val Rots = Array(0, FloatMath.PI/2, FloatMath.PI, 3*FloatMath.PI/2)
}

trait Footed extends Bodied { self :Entity =>
  val foot :Seq[Coord]
}

trait Living { self :Entity =>
  /** Indicates that this entitiy is still alive. MOBs who eat/stomp it will ignore it if some other
    * MOB managed to kill it first. */
  var alive :Boolean

  def entity :Entity = self
}

trait Edible extends Bodied with Living { self :Entity =>
}

trait Stompable extends Bodied with Living { self :Entity =>
}

/** Our current protagonist. */
class FruitFly (
  /** The number of moves remaining for this fly. */
  var movesLeft :Int = Constants.BaseMoves
) extends Entity with Footed with Edible with Stompable {
  var alive = true

  /** The item currently being carried by the fly (or null). */
  var item :Entity = _

  /** The number of moves our offspring will have. Can be modified by powerups? */
  var offspringMoves = Constants.BaseMoves

  val foot = Coord.square(1)
  def viz = Viz(1, 1, 0xFF111111, 0xFFCCCCCC)
    .line(1/8f,  1/8f, 1/2f,   1/2f, 1)
    .line(1/16f, 1/3f, 15/16f, 2/3f, 1)
    .line(1/16f, 2/3f, 15/16f, 1/3f, 1)
    .line(1/2f,  1/2f, 7/8f,   1/8f, 1)
    .ellipseSF(1/4f, 0f, 1/2f, 1f)
    .polySF((1/2f, 1/4f), (1f, 1f), (1/2f, 3/4f), (0f, 1f))

  override def move (jiva :Jivaloka, coord :Coord) {
    super.move(jiva, coord)
    jiva.flyMove.emit(this)
    // decrement our move counter and note whether we've run out of moves
    movesLeft -= 1
    jiva.movesLeft.update(movesLeft)
    // if we're out of moves and were not killed for other reasons; keel over
    if (alive && movesLeft == 0) jiva.croak(this)
    // update our orientation
  }

  override def depth = PlayerDepth
  override def scale = 0.6f
  override def toString = super.toString + s":$coord"
}

/** A nest of eggs, from which our protagonists spawn. */
class Nest (val eggs :Int, val moves :Int) extends Entity with Footed {
  def hatch (jiva :Jivaloka) {
    jiva.remove(this)
    if (eggs > 1) jiva.add(new Nest(eggs-1, moves).at(coord))
    jiva.hatch(coord, moves)
  }

  val foot = Coord.square(1)
  def viz = {
    val viz = Viz(1, 1, 0xFFFFFFFF, 0xFFFFCC99)
    eggs match {
      case 4 => viz.circleF(1/4f, 1/4f, 1/4f).circleF(3/4f, 1/4f, 1/4f).
          circleF(1/4f, 3/4f, 1/4f).circleF(3/4f, 3/4f, 1/4f)
      case 3 => viz.circleF(1/2f, 1/4f, 1/4f).
          circleF(1/4f, 3/4f, 1/4f).circleF(3/4f, 3/4f, 1/4f)
      case 2 => viz.circleF(1/2f, 1/4f, 1/4f).
          circleF(1/2f, 3/4f, 1/4f)
      case 1 => viz.circleF(1/2f, 1/2f, 1/4f)
    }
  }
}

class Mate extends Entity with Footed with Edible with Stompable {
  def maybeMate (jiva :Jivaloka, fly :FruitFly) {
    if (coord.dist(fly.coord) == 1) {
      jiva.remove(this) // TODO: vary egg count based on depth?
      jiva.add(new Nest(1+jiva.rand.nextInt(4), fly.offspringMoves).at(coord))
      jiva.anim(coord, "\u2665", 0xFFFF0000, 48)
    }
  }

  var alive = true
  val foot = Coord.square(1)
  def viz = Viz(1, 1, 0xFFAA1111, 0xFFCCCCCC)
    .line(1/8f,  7/8f, 1/2f,   1/2f, 1)
    .line(1/16f, 1/3f, 15/16f, 2/3f, 1)
    .line(1/16f, 2/3f, 15/16f, 1/3f, 1)
    .line(1/2f,  1/2f, 7/8f,   7/8f, 1)
    .ellipseSF(1/4f, 0f, 1/2f, 1f)
    .heartSF(1/2f, 3/4f, 1f, 3/4f)

  override def depth = MOBDepth
  override def scale = 0.6f
}

class Exit extends Entity with Bodied {
  def viz = Viz(1, 1, 0xFFFFFFFF, 0x66000000).
    polyF((1/2f, 1/5f), (4/5f, 1/2f), (2/3f, 1/2f), (2/3f, 3/4f),
          (1/3f, 3/4f), (1/3f, 1/2f), (1/5f, 1/2f))
}

class Splat (color :Int) extends Entity with Bodied {
  def viz = {
    val rando = new java.util.Random
    val angcs = Array.fill(20)((rando.nextFloat*2/5f, rando.nextFloat*FloatMath.PI*2))
    (Viz(1, 1, 0xFFFFFFFF, color) /: angcs) {
      case (v, (r, a)) => v.circleF(1/2f + r*FloatMath.cos(a), 1/2f + r*FloatMath.sin(a), 1/6f)
    }
  }
}
