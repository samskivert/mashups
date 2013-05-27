//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

/** Enumerates our card ranks. */
public enum Rank {

  // this matches the order of the cards in the sprite sheet, for simplicity
  ACE { public int delta (Rank other) { return (other == KING) ? -1 : super.delta(other); }},
  TWO,
  THREE,
  FOUR,
  FIVE,
  SIX,
  SEVEN,
  EIGHT,
  NINE,
  TEN,
  JACK,
  QUEEN,
  KING { public int delta (Rank other) { return (other == ACE) ? 1 : super.delta(other); }};

  /** Returns the difference in ranks (i.e. {@code other - this}). Handles aces high/low by
   * returning 1/-1 as appropriate when an ACE is compared to a TWO or KING. */
  public int delta (Rank other) {
    return other.ordinal() - ordinal();
  }
}
