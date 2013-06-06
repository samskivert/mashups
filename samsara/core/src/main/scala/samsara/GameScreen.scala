//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.PlayN._
import playn.core._
import playn.core.util.Clock
import tripleplay.game.UIScreen

class GameScreen (game :Samsara, levels :LevelDB, level :Level) extends UIScreen {
  def this (game :Samsara, levels :LevelDB) = this(game, levels, levels.get(0, None))

  val metrics = new Metrics(width, height)
  val jiva = new Jivaloka(game, this, levels, level)

  override def wasAdded () {
    super.wasAdded()

    // "reboot" on 'r' for debugging
    jiva.keyDown.connect(slot[Key] {
      case key if (key == Key.R) => game.screens.replace(new GameScreen(game, new LevelDB))
    })

    // TODO: center our level grid in the available space

    // add a renderer for our board
    layer.add(graphics.createImmediateLayer(new ImmediateLayer.Renderer {
      def render (surf :Surface) {
        val size = metrics.size
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

    // display our current level depth number in the upper right
    val levlay = UI.levelCfg.toLayer(level.depth.toString)
    layer.addAt(levlay.setDepth(1).setAlpha(0.3f), (width-levlay.width)/2, (height-levlay.height)/2)

    // add all of the level entities
    jiva.level.entities foreach jiva.add
  }

  override def showTransitionCompleted () {
    super.showTransitionCompleted()
    // hatch a fly from the nest
    jiva.start()
    // start listening for keyboard input
    keyboard.setListener(new Keyboard.Adapter {
      override def onKeyDown (event :Keyboard.Event) = jiva.keyDown.emit(event.key)
    })
  }

  override def paint (clock :Clock) {
    jiva.onPaint.emit(clock)
  }

  override def hideTransitionStarted () {
    super.hideTransitionStarted()
    invokeLater(new Runnable() {
      def run = keyboard.setListener(null)
    })
  }
}
