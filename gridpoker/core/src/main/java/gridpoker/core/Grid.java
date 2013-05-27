//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import react.RMap;

/** Models the card grid; contains some logics. */
public class Grid {

  /** The grid of cards. */
  public final RMap<Coord,Card> cards = RMap.create();

  /** Returns true if {@code coord} has a non-empty neighbor. */
  public boolean hasNeighbor (Coord coord) {
    return cards.containsKey(coord.left()) ||
      cards.containsKey(coord.right()) ||
      cards.containsKey(coord.above()) ||
      cards.containsKey(coord.below());
  }
}
