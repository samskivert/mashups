//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

/** Enumerates our card ranks. */
public enum Rank {

  TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;
  // note: this matches the order of the cards in the sprite sheet

  public static final String ABBREV = "23456789TJQKA";

  public int flag () {
    return 1 << (ordinal()+1); // make space for the ace
  }
}
