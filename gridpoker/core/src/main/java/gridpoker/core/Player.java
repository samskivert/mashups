//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

/** Identifies player as human or AI. Could also expand to include AI variants. */
public enum Player {
  HUMAN {
    public String name (int index) { return "Human " + (index+1); }
  },
  AI {
    public String name (int index) { return "Computer " + (index+1); }
  };

  public abstract String name (int index);
}
