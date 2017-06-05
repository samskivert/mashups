//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import tripleplay.game.ScreenStack
import tripleplay.ui._
import tripleplay.ui.layout.AxisLayout
import tripleplay.ui.layout.TableLayout

class MainMenuScreen (game :Samsara) extends ScreenStack.UIScreen(game.plat) {

  def game = game

  override def wasAdded () {
    val root = iface.createRoot(AxisLayout.vertical, UI.sheet(game.plat), layer)
    root.addStyles(Style.BACKGROUND.is(Background.solid(0xFFFFFFFF)))

    val flickTapBox = new CheckBox()
    flickTapBox.select(game.flickTapInput)
    flickTapBox.selected.connect(slot[java.lang.Boolean] { case checked =>
      game.flickTapInput = checked
    })
    val flickBox = new CheckBox()
    flickBox.select(game.flickInput)
    flickBox.selected.connect(slot[java.lang.Boolean] { case checked =>
      game.flickInput = checked
    })
    val relTapBox = new CheckBox();
    relTapBox.select(game.absTapInput)
    relTapBox.selected.connect(slot[java.lang.Boolean] { case checked =>
      game.relTapInput = checked
    })
    val absTapBox = new CheckBox();
    absTapBox.select(game.absTapInput)
    absTapBox.selected.connect(slot[java.lang.Boolean] { case checked =>
      game.absTapInput = checked
    })

    root.add(UI.stretchShim,
             new Label("Sa\u1E43s\u0101ra").addStyles(Style.FONT.is(UI.titleFont)),
             UI.stretchShim,
             new Group(new TableLayout(TableLayout.COL, TableLayout.COL.alignLeft()).
      gaps(5, 5)).add(
      flickTapBox, new Label("Flick+tap input"),
      flickBox, new Label("Flick input"),
      relTapBox, new Label("Relative tap input"),
      absTapBox, new Label("Absolute tap input")),
             UI.stretchShim,
             new Button("New Game").addStyles(Style.FONT.is(UI.menuFont)).onClick(newGame),
             UI.stretchShim)
    root.setSize(size)
  }

  protected def newGame () {
    game.screens.push(new GameScreen(game, new LevelDB), game.screens.slide.down)
  }
}
