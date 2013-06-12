//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.PlayN._
import playn.core._
import playn.core.util.Clock
import scala.collection.mutable.{Set => MSet}
import tripleplay.game.ScreenStack

class Samsara extends Game.Default(33) {

  val screens = new ScreenStack
  val seenTips = MSet[Int]()

  override def init ()  {
    screens.push(new MainMenuScreen(this))
  }

  override def update (delta :Int) {
    _clock.update(delta)
    screens.update(delta)
  }

  override def paint (alpha :Float) {
    _clock.paint(alpha)
    screens.paint(_clock)
  }

  private val _clock = new Clock.Source(33)
}
