//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import tripleplay.game.UIScreen
import tripleplay.ui._
import tripleplay.ui.layout.AxisLayout

class MainMenuScreen (game :Samsara) extends UIScreen {

  override def wasAdded () {
    val root = iface.createRoot(AxisLayout.vertical, SimpleStyles.newSheet, layer)
    root.addStyles(Style.BACKGROUND.is(Background.solid(0xFFFFFFFF)))
    root.add(UI.stretchShim,
             new Label("Sa\u1E43s\u0101ra").addStyles(Style.FONT.is(UI.titleFont)),
             UI.stretchShim,
             new Button("New Game").addStyles(Style.FONT.is(UI.menuFont)).onClick(newGame),
             UI.stretchShim)
    root.setSize(width, height)
  }

  protected def newGame () {
    game.screens.push(new GameScreen(game, new LevelDB), game.screens.slide.down)
  }
}
