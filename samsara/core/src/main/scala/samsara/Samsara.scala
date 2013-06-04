//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.Game
import playn.core.util.Clock

import tripleplay.game.ScreenStack

class Samsara extends Game.Default(33) {

  override def init ()  {
    _screens.push(new MainMenuScreen)
  }

  override def update (delta :Int) {
    _clock.update(delta)
    _screens.update(delta)
  }

  override def paint (alpha :Float) {
    _clock.paint(alpha)
    _screens.paint(_clock)
  }

  private val _clock = new Clock.Source(33)
  private val _screens = new ScreenStack
}
