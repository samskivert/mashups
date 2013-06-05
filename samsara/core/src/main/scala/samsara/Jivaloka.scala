//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.Key
import playn.core.util.Clock
import react.Signal

class Jivaloka (
  val screen  :LevelScreen,
  val level   :Level,
  val metrics :Metrics
) extends World {

  val keyDown = Signal.create[Key]()
  val onPaint = Signal.create[Clock]()
  val onChomp = Signal.create[MOB]()
  val onMove  = Signal.create[Bodied]()
  val flyMove = Signal.create[FruitFly]()

  val rand    = new java.util.Random // TODO: seeded? save seed?
  val systems = new Systems(this, screen, level)

  def chomp (chomper :MOB, protag :FruitFly) {
    protag.alive = false
    remove(protag)
    add(new Splat(protag.coord))
    systems.hatcher.hatch()
  }

  val isPassable = (c :Coord) => c != null && _pass(c.index)
  val makeImpass = (c :Coord) => _pass(c.index) = false
  val resetPass  = (c :Coord) => _pass(c.index) = level.terrain(c.index).passable
  private val _pass = level.terrain.map(_.passable)
}
