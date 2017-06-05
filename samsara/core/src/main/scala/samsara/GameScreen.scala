//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import playn.core._
import playn.scene.Layer
import pythagoras.f.{MathUtil, Point, Points}
import react.Closeable
import scala.collection.BitSet
import tripleplay.game.ScreenStack
import tripleplay.ui._
import tripleplay.ui.layout.AxisLayout
import tripleplay.util.StyledText

class GameScreen (game :Samsara, levels :LevelDB, level :Level)
    extends ScreenStack.UIScreen(game.plat) {
  def this (game :Samsara, levels :LevelDB) = this(game, levels, levels.level0)

  val metrics = new Metrics(size.width, size.height)
  val jiva = new Jivaloka(game, this, levels, level)
  var reach :BitSet = _
  def game = game

  this.paint.connect((clock :Clock) => jiva.onPaint.emit(clock))

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
    layer.add(new Layer() {
      def paintImpl (surf :Surface) {
        val size = metrics.size
        // render the terrain
        var idx = 0 ; while (idx < level.terrain.length) {
          val x = idx % Level.width
          val y = idx / Level.width
          surf.setFillColor(level.terrain(idx).color).fillRect(x*size, y*size, size, size)
          idx += 1
        }

        // TEMP: fill in the gaps around the edges
        val rx = Level.width*size ; val by = Level.height*size
        surf.setFillColor(Dirt.color).fillRect(rx, 0, width, height)
        surf.setFillColor(Dirt.color).fillRect(0, by, rx, height)

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
    })

    // display our current level depth number as a giant decal overlaying the board
    val levlay = StyledText.span(game.plat.graphics, level.depth.toString, UI.levelCfg).toLayer()
    layer.addAt(levlay.setDepth(1).setAlpha(0.3f),
                (size.width-levlay.width)/2, (size.height-levlay.height)/2)

    // display our remaining move count in the lower right
    val croot = iface.createRoot(AxisLayout.vertical, UI.sheet(game.plat))
    croot.add(new ValueLabel(jiva.movesLeft).addStyles(Style.FONT.is(UI.bodyFont(24))))
    croot.setSize(metrics.size, metrics.size)
    layer.addAt(croot.layer.setDepth(20).setAlpha(0.6f),
                size.width-metrics.size, size.height-metrics.size)

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
    val tlayer = StyledText.block(game.plat.graphics, msg, UI.tipCfg, 256).toLayer()
    layer.addAt(tlayer.setDepth(9999), (size.width-tlayer.width)/2, (size.height-tlayer.height)/2)
    jiva.keyDown.connect(tlayer.close()).once // go away on any key press
    jiva.onTap.connect(tlayer.close()).once // go away on any tap
    jiva.onFlick.connect(tlayer.close()).once // go away on any flick
  }

  override def showTransitionCompleted () {
    super.showTransitionCompleted()

    // start listening for keyboard input
    closeOnHide(game.plat.input.keyboardEvents.connect((event :Keyboard.Event) => event match {
      case kevent :Keyboard.KeyEvent => if (kevent.down) jiva.keyDown.emit(kevent.key)
      case _ =>
    }))

    // listen for pointer input as well
    val UsesFlick = game.flickInput || game.flickTapInput
    val MinFlickDist = 20 // pixels
    var _start = new Point()
    closeOnHide(game.pointer.events.connect((ev :Pointer.Event) => {
      ev.kind match {
        case Pointer.Event.Kind.START =>
          if (UsesFlick) _start.set(ev.x, ev.y)
          else jiva.onTap.emit(ev)
        case Pointer.Event.Kind.END =>
          if (UsesFlick) {
            val dx = ev.x - _start.x ; val dy = ev.y - _start.y
            val dist = Points.distance(_start.x, _start.y, ev.x, ev.y)
            if (dist < MinFlickDist) jiva.onTap.emit(ev)
            else {
              val sum = dx + dy
              jiva.onFlick.emit(if (dx > dy) {
                if (sum > 0) (1, 0) else (0, -1)
              } else {
                if (sum > 0) (0, 1) else (-1, 0)
              })
            }
          }
        case _ => // nada
      }
    }))

    // hatch a fly from the nest
    jiva.start()
  }
}
