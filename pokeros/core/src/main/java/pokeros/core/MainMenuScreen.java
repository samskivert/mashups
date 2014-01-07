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
    root.add(new Label("Pokeros").addStyles(Style.FONT.is(UI.defaultFont.derive(68f))),
             UI.stretchShim(),
             new Button("Play").addStyles(UI.bigButtonStyles).onClick(
               new UnitSlot() { public void onEmit () {
                 _game.screens.push(new GameScreen(_game));
               }}),
             UI.stretchShim(),
             new Group(AxisLayout.horizontal()).add(
               new Label("Wins:"), new ValueLabel(_game.history.wins), new Shim(10, 1),
               new Label("Losses:"), new ValueLabel(_game.history.losses), new Shim(20, 1),
               new Button("More...").onClick(
                 new UnitSlot() { public void onEmit () {
                   _game.screens.push(new HistoryScreen(_game));
                 }})));
    root.setSize(width(), height());
  }

  protected final Pokeros _game;
}
