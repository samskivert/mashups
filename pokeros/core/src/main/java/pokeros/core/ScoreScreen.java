//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import tripleplay.ui.*;

public class ScoreScreen extends UIScreen {

  public ScoreScreen (Pokeros game) {
    super(game);
  }

  @Override protected void createUI (Root root) {
    addTitle(root, "Scoring");
    root.add(UI.stretchShim(), new Label(SCORING).addStyles(UI.textStyles),
             UI.stretchShim(), new Button("Back").onClick(popSlot()));
  }

  @Override protected void pop () {
    _game.screens.remove(this, _game.screens.flip().duration(500).easeInOut());
  }

  protected static final String SCORING =
    "1 - Pair\n" +
    "2 - Three card flush\n" +
    "2 - Three card straight\n" +
    "3 - Two pair\n" +
    "4 - Four card flush\n" +
    "4 - Three of a kind\n" +
    "5 - Four card straight\n" +
    "5 - Full house\n" +
    "8 - Five card flush\n" +
    "10 - Five card straight\n" +
    "10 - Four of a kind\n" +
    "10 - Three card straight flush\n" +
    "20 - Four card straight flush\n" +
    "40 - Five card straight flush\n" +
    "60 - Royal flush";
}
