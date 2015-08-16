//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core.PlayN._
import playn.core._
import playn.core.util.Clock
import pythagoras.f.{MathUtil, Point, Points}
import scala.collection.BitSet
import tripleplay.game.UIScreen
import tripleplay.ui._
import tripleplay.ui.layout.AxisLayout
import tripleplay.util.StyledText

class GameScreen (game :Samsara, levels :LevelDB, level :Level) extends UIScreen {
  def this (game :Samsara, levels :LevelDB) = this(game, levels, levels.level0)

  val metrics = new Metrics(width, height)
  val jiva = new Jivaloka(game, this, levels, level)
  var reach :BitSet = _

  def position (layer :Layer, coord :Coord) {
    val size = metrics.size
    layer.setTranslation(coord.x * size + layer.originX, coord.y * size + layer.originY)
  }

  def center (layer :Layer, coord :Coord) {
    val size = metrics.size
    layer.setTranslation(coord.x * size + size/2, coord.y * size + size/2)
  }

  def toCoord (x :Float) = MathUtil.ifloor(x / metrics.size)

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
        // render the terrain
        var idx = 0 ; while (idx < level.terrain.length) {
          val x = idx % Level.width
          val y = idx / Level.width
          surf.setFillColor(level.terrain(idx).color).fillRect(x*size, y*size, size, size)
          idx += 1
        }

        // render reachable tiles for debuggery
        if (reach != null) {
          idx = 0 ; while (idx < level.terrain.length) {
            if (!reach(idx)) {
              val x = idx % Level.width
              val y = idx / Level.width
              surf.setFillColor(0x44FF0000).fillRect(x*size, y*size, size, size)
            }
            idx += 1
          }
        }

        // var x = 0 ; while (x <= Level.width) {
        //   x += 1
        //   surf.setFillColor(0x33FFFFFF).drawLine(x*size, 0, x*size, Level.height*size, 1)
        // }
        // var y = 0 ; while (y <= Level.height) {
        //   y += 1
        //   surf.setFillColor(0x33FFFFFF).drawLine(0, y*size, Level.width*size, y*size, 1)
        // }
      }
    }))

    // display our current level depth number as a giant decal overlaying the board
    val levlay = StyledText.span(level.depth.toString, UI.levelCfg).toLayer()
    layer.addAt(levlay.setDepth(1).setAlpha(0.3f), (width-levlay.width)/2, (height-levlay.height)/2)

    // display our remaining move count in the lower right
    val croot = iface.createRoot(AxisLayout.vertical, UI.sheet)
    croot.add(new ValueLabel(jiva.movesLeft).addStyles(Style.FONT.is(UI.bodyFont(24))))
    croot.setSize(metrics.size, metrics.size)
    layer.addAt(croot.layer.setDepth(20).setAlpha(0.6f), width-metrics.size, height-metrics.size)

    // add a tip on the first few levels
    if (!game.seenTips(level.depth)) {
      game.seenTips += level.depth
      level.depth match {
        case 0 => addTip("Move your fly up out the exit at the top of the screen.")
        case 1 => addTip("Move to the mate to 'create eggs'.")
        case 2 => addTip("Watch out for frogs. They eat anything in the 2x2 space in front of them.")
        case 3 => addTip("Spiders will eat you too, and they move.")
        case _ => // nada
      }
    }

    // add all of the level entities
    jiva.level.entities foreach jiva.add
  }

  private def addTip (msg :String) {
    val tlayer = StyledText.block(msg, UI.tipCfg, 256).toLayer()
    layer.addAt(tlayer.setDepth(9999), (width-tlayer.width)/2, (height-tlayer.height)/2)
    jiva.keyDown.connect(tlayer.destroy()).once // go away on any key press
    jiva.onTap.connect(tlayer.destroy()).once // go away on any tap
    jiva.onFlick.connect(tlayer.destroy()).once // go away on any flick
  }

  override def showTransitionCompleted () {
    super.showTransitionCompleted()
    // hatch a fly from the nest
    jiva.start()
    // start listening for keyboard input
    keyboard.setListener(new Keyboard.Adapter {
      override def onKeyDown (event :Keyboard.Event) = jiva.keyDown.emit(event.key)
    })
    // listen for pointer input as well
    val usesFlick = game.flickInput || game.flickTapInput
    pointer.setListener(new Pointer.Adapter {
      private var _start = new Point()
      override def onPointerStart (ev :Pointer.Event) = {
        if (usesFlick) _start.set(ev.x, ev.y)
        else jiva.onTap.emit(ev)
      }
      override def onPointerEnd (ev :Pointer.Event) = if (usesFlick) {
        val dx = ev.x - _start.x ; val dy = ev.y - _start.y
        val dist = Points.distance(_start.x, _start.y, ev.x, ev.y)
        if (dist < 5) jiva.onTap.emit(ev)
        else {
          val sum = dx + dy
          jiva.onFlick.emit(if (dx > dy) {
            if (sum > 0) (1, 0) else (0, -1)
          } else {
            if (sum > 0) (0, 1) else (-1, 0)
          })
        }
      }
    })
  }

  override def paint (clock :Clock) {
    super.paint(clock)
    jiva.onPaint.emit(clock)
  }

  override def hideTransitionStarted () {
    super.hideTransitionStarted()
    invokeLater(new Runnable() { def run () {
      keyboard.setListener(null)
      pointer.setListener(null)
    }})
  }
}
