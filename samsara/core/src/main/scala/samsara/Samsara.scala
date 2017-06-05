//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.{Clock, Platform}
import playn.scene.{Pointer, Mouse, Touch, SceneGame}
import scala.collection.mutable.{Set => MSet}
import tripleplay.game.ScreenStack

class Samsara (plat :Platform) extends SceneGame(plat, 33) {

  val screens = new ScreenStack(this, rootLayer)
  val seenTips = MSet[Int]()
  val pointer = new Pointer(plat, rootLayer, false)

  var flickTapInput = true
  var flickInput = false
  var relTapInput = false
  var absTapInput = false

  plat.input.touchEvents.connect(new Touch.Dispatcher(rootLayer, false));
  plat.input.mouseEvents.connect(new Mouse.Dispatcher(rootLayer, false));
  screens.push(new MainMenuScreen(this))
}
