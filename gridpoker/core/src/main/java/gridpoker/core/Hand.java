//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

public class Hand {

  // a hand with zero score, used in lieu of null
  public static final Hand ZERO = new Hand(null, null, 0);

  // the minimum and maximum hand size (in cards)
  public static final int MIN = 2, MAX = 5;

  public final Cons<Coord> coords;
  public final Cons<Card> cards;
  public final int score;

  public Hand (Cons<Coord> coords, Cons<Card> cards, int score) {
    this.coords = coords;
    this.cards = cards;
    this.score = score;
  }

  @Override public String toString () {
    return score + " : " + cards + " @ " + coords;
  }
}
