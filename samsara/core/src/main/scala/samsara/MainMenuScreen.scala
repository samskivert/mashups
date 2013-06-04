//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package samsara

import tripleplay.game.UIScreen
import tripleplay.ui._
import tripleplay.ui.layout.AxisLayout

class MainMenuScreen extends UIScreen {

  override def wasAdded () {
    val root = iface.createRoot(AxisLayout.vertical, SimpleStyles.newSheet, layer)
    root.addStyles(Style.BACKGROUND.is(Background.solid(0xFFFFFFFF)))
    root.add(new Label("Samsara"))
    root.setSize(width, height)
  }
}
