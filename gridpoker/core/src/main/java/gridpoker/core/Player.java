//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import react.IntValue;
import react.RList;

/** Identifies player as human or AI. Could also expand to include AI variants. */
public abstract class Player {

  public static Player human () { return new Player() {
    public boolean isHuman () { return true; }
    public String name (int index) { return "Human " + (index+1); }
  };}

  public static Player computer () { return new Player() {
    public boolean isHuman () { return false; }
    public String name (int index) { return "Computer " + (index+1); }
  };}

  public final RList<Card> stash = RList.create();
  public final IntValue score = new IntValue(0);

  public abstract boolean isHuman ();
  public abstract String name (int index);
}
