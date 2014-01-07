//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import java.util.Iterator;

import react.UnitSlot;

import tripleplay.game.UIAnimScreen;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

public class HistoryScreen extends UIAnimScreen {

  public HistoryScreen (Pokeros game) {
    _game = game;
  }

  @Override public void wasAdded () {
    super.wasAdded();

    Root root = iface.createRoot(AxisLayout.vertical(), UI.stylesheet(), layer);
    root.addStyles(Style.BACKGROUND.is(Background.image(_game.media.felt).inset(10)));
    Group games = new Group(AxisLayout.vertical().offStretch());
    root.add(new Label("Recent Games").addStyles(Style.FONT.is(UI.defaultFont.derive(36f))),
             AxisLayout.stretch(games),
             new Button("Back").onClick(new UnitSlot() { public void onEmit () {
               _game.screens.remove(HistoryScreen.this);
             }}));
    // history.recents is oldest to newest, but we want to display the games newest to oldest, so
    // use recursion to reverse the list during addition
    addRecents(games, _game.history.recents.iterator());
    if (games.childCount() == 0) games.add(new Label("No recent games."));
    root.setSize(width(), height());
  }

  private void addRecents (Group games, Iterator<History.Game> iter) {
    if (!iter.hasNext()) return;
    History.Game game = iter.next();
    addRecents(games, iter);
    games.add(new Label(game.format()).addStyles(Style.HALIGN.left));
  }

  protected final Pokeros _game;
}
