//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import java.util.Iterator;

import playn.core.Image;

import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;

public class HistoryScreen extends UIScreen {

  public HistoryScreen (Pokeros game) {
    super(game);
  }

  @Override protected String title () {
    return "Recent Games";
  }

  @Override protected void createUI (Root root) {
    Group games = new Group(new TableLayout(TableLayout.COL.alignRight().copy(3)).gaps(0, 10));
    if (_game.history.recents.isEmpty()) games.add(new Label("No recent games."));
    else games.add(new Label(""),
                   new Label(Player.WHO[0]).addStyles(Style.UNDERLINE.on),
                   new Label(Player.WHO[1]).addStyles(Style.UNDERLINE.on));

    root.add(AxisLayout.stretch(games),
             new Button("Back").onClick(popSlot()));
    // history.recents is oldest to newest, but we want to display the games newest to oldest, so
    // use recursion to reverse the list during addition
    System.err.println("Recents " + _game.history.recents.size());
    addRecents(games, _game.history.recents.iterator());
  }

  private void addRecents (Group games, Iterator<History.Game> iter) {
    if (!iter.hasNext()) return;
    History.Game game = iter.next();
    addRecents(games, iter);
    Image icon = game.icon(_game.media);
    games.add((icon == null) ? new Label() : new Label(Icons.image(icon)),
              new Label(String.valueOf(game.scores[0])),
              new Label(String.valueOf(game.scores[1])));
  }
}
