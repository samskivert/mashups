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
  val onMove  = Signal.create[FruitFly]()
  val onPaint = Signal.create[Clock]()
  val onChomp = Signal.create[MOB]()

  val systems = new Systems(this, screen, level)

  def chomp (chomper :MOB, protag :FruitFly) {
    protag.alive = false
    remove(protag)
    add(new Splat(protag.coord))
    systems.hatcher.hatch()
  }
}
