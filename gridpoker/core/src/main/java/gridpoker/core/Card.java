//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

/** Models a card. */
public class Card {

  public final Suit suit;
  public final Rank rank;

  public Card (Suit suit, Rank rank) {
    this.suit = suit;
    this.rank = rank;
  }

  @Override public int hashCode () {
    return suit.hashCode() ^ rank.hashCode();
  }

  @Override public boolean equals (Object other) {
    return other instanceof Card && ((Card)other).suit == suit && ((Card)other).rank == rank;
  }

  @Override public String toString () {
    return rank + " " + suit;
  }
}
