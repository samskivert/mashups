//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import react.UnitSlot;

import tripleplay.game.UIAnimScreen;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

public class MainMenuScreen extends UIAnimScreen {

  public MainMenuScreen (Pokeros game) {
    _game = game;
  }

  @Override public void wasAdded () {
    super.wasAdded();

    Root root = iface.createRoot(AxisLayout.vertical(), UI.stylesheet(), layer);
    root.addStyles(Style.BACKGROUND.is(Background.image(_game.media.felt).inset(10)));
    root.add(UI.stretchShim(),
             new Label("Pokeros").addStyles(Style.FONT.is(UI.defaultFont.derive(68f))),
             UI.stretchShim(),
             new Button("New Game").addStyles(UI.bigButtonStyles).onClick(
               new UnitSlot() { public void onEmit () {
                 _game.screens.push(new GameScreen(_game, Player.human(), Player.computer()));
               }}),
             UI.stretchShim());
    root.setSize(width(), height());
  }

  protected final Pokeros _game;
}
