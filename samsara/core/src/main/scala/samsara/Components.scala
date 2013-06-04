//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import com.artemis.{Component, Entity}
import playn.core.Layer

/** A body component is something that exists on screen with a viz, dimensions and coords. */
class Body (
  x :Int, y :Int,
  val width  :Int,
  val height :Int,
  val viz    :Viz
) extends Component {

  /** This body's current coordinates. */
  var coord :Coord = Coord(x, y)

  /** This body's visualization. Initialized by the render system. */
  var layer :Layer = _

  /** Updates this body's coordinates, and moves its layer. (TODO: animate) */
  def move (coord :Coord, metrics :Metrics) {
    this.coord = coord
    layer.setTranslation(coord.x * metrics.size + layer.originX,
                         coord.y * metrics.size + layer.originY)
  }
}

/** Contains the footprint of a prop. */
class Footprint (val coords :Seq[Coord]) extends Component

/** Contains information on the player. */
class Player extends Component {

  /** The item currently being carried by the player (or null). */
  var item :Entity = _
}

// TODO: a component for items
