//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import react.IntValue;
import react.RSet;

/** Identifies player as human or AI. Could also expand to include AI variants. */
public abstract class Player {

  public static final int STASH = 3;
  public static final String[] WHO = { "You", "HAL" };

  public static Player human () { return new Player() {
    public boolean isHuman () { return true; }
  };}

  public static Player computer () { return new Player() {
    public boolean isHuman () { return false; }
  };}

  public final RSet<Card> stash = RSet.create();
  public final IntValue score = new IntValue(0);

  public abstract boolean isHuman ();

  public void upStash (Deck deck) {
    while (stash.size() < STASH && !deck.cards.isEmpty()) {
      stash.add(deck.cards.remove(0));
    }
  }

  @Override public String toString () {
    return "[stash=" + stash + ", score=" + score + "]";
  }
}
