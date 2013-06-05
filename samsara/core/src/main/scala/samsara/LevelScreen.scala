//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.PlayN._
import playn.core._
import playn.core.util.Clock
import tripleplay.game.UIScreen

class LevelScreen (game :Samsara, level :Level) extends UIScreen {

  val jiva = new Jivaloka(this, level, new Metrics(width, height))

  override def wasAdded () {
    super.wasAdded()

    // reload the screen on 'r' for debugging
    jiva.keyDown.connect(slot[Key] {
      case key if (key == Key.R) =>
        game.screens.replace(new LevelScreen(game, level))
    })

    // TODO: center our level grid in the available space

    // add a renderer for our board
    layer.add(graphics.createImmediateLayer(new ImmediateLayer.Renderer {
      def render (surf :Surface) {
        val size = jiva.metrics.size
        var idx = 0 ; while (idx < level.terrain.length) {
          val x = idx % Level.width
          val y = idx / Level.width
          surf.setFillColor(level.terrain(idx).color).fillRect(x*size, y*size, size, size)
          idx += 1
        }
        var x = 0 ; while (x <= Level.width) {
          x += 1
          surf.setFillColor(0x33FFFFFF).drawLine(x*size, 0, x*size, Level.height*size, 1)
        }
        var y = 0 ; while (y <= Level.height) {
          y += 1
          surf.setFillColor(0x33FFFFFF).drawLine(0, y*size, Level.width*size, y*size, 1)
        }
      }
    }))

    // create our entities
    level.createEntities(jiva)
    // hatch a fly from the nest
    jiva.systems.hatcher.hatch()
  }

  override def paint (clock :Clock) {
    jiva.onPaint.emit(clock)
  }

  override def showTransitionCompleted () {
    super.showTransitionCompleted()
    keyboard.setListener(new Keyboard.Adapter {
      override def onKeyDown (event :Keyboard.Event) = jiva.keyDown.emit(event.key)
    })
  }

  override def hideTransitionStarted () {
    super.hideTransitionStarted()
    invokeLater(new Runnable() {
      def run = keyboard.setListener(null)
    })
  }
}
