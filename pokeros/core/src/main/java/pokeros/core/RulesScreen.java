//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

public class RulesScreen extends UIScreen {

  public RulesScreen (Pokeros game) {
    super(game);
  }

  @Override protected String title () {
    return "Rules of the Game";
  }

  @Override protected void createUI (Root root) {
    Group content = new Group(AxisLayout.vertical()).add(
      new Label(RULES).addStyles(UI.textStyles),
      new Label("Scoring"),
      new Label("The scoring for each poker hand is as follows:\n" +
                ScoreScreen.SCORING).addStyles(UI.textStyles));
    root.add(new Shim(5, 5),
             AxisLayout.stretch(new Scroller(content).setBehavior(Scroller.Behavior.VERTICAL)),
             new Shim(5, 5), new Button("Back").onClick(popSlot()));
  }

  protected static final String RULES = "Place cards onto the table above, below, left or " +
    "right of an already placed card. If the card you placed makes one or more poker hands " +
    "with the cards immediately adjacent to it, you score points for those hands. The two " +
    "players take turns placing cards onto the table until the deck is exhausted.";
}
