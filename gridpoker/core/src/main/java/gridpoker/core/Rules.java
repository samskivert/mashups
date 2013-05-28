//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

/** Handles some rules logic (mainly scoring). */
public class Rules {

  public static int scoreHand (Cons<Card> hand) {
    // we enumerate all possible hands for scoring, so we don't have to worry about scoring subsets
    // of the hand in question; i.e. it's either entirely a straight (or flush) or not
    int hsize = hand.size(), ranks = countRank2s(hand);
    boolean isFlush = isSameSuit(hand), isStraight = isStraight(hand);
    if (isStraight && isFlush && hsize == 5 &&
        (hand.head.rank == Rank.ACE || hand.last().rank == Rank.ACE)) return ROYAL_FLUSH_SCORE;
    else if (isFlush && isStraight)    return STRFLUSH_SCORES[hsize];
    else if (isFlush)                  return FLUSH_SCORES[hsize];
    else if (isStraight)               return STRAIGHT_SCORES[hsize];
    else if (ranks == 1)               return NKIND_SCORES[hsize];
    else if (ranks == 2 && hsize == 5) return FULL_HOUSE_SCORE;
    else if (ranks == 2 && hsize == 4) return TWO_PAIR_SCORE;
    return 0;
  }

  public static boolean isSameSuit (Cons<Card> cards) {
    Suit suit = cards.head.suit;
    for (Cons<Card> card = cards.tail; card != null; card = card.tail) {
      if (card.head.suit != suit) return false;
    }
    return true;
  }

  public static boolean isStraight (Cons<Card> cards) {
    if (cards.tail == null) return false;
    Rank prev = cards.head.rank, next = cards.tail.head.rank;
    int dr = prev.delta(next);
    if (Math.abs(dr) != 1) return false;
    for (Cons<Card> card = cards.tail.tail; card != null; card = card.tail) {
      // delta handles ACE/KING comparisons, but we need to be sure not to wrap around; 'next' will
      // be index 1 when we first enter this loop and it will only range up to the penultimate
      // card, so if 'next' is ever an ACE right here, the hand is not a straight
      if (next == Rank.ACE) return false;
      prev = next;
      next = card.head.rank;
      if (prev.delta(next) != dr) return false;
    }
    return true;
  }

  public static int countRank2s (Cons<Card> cards) {
    int rankMask1 = 0, rankMask2 = 0;
    for (; cards != null; cards = cards.tail) {
      int flag = (1 << cards.head.rank.ordinal());
      if ((rankMask1 & flag) != 0) rankMask2 |= flag;
      else rankMask1 |= flag;
    }
    return Integer.bitCount(rankMask2);
  }

  protected static final int[] NKIND_SCORES =    { 0, 0, 1, 5, 16,  0 };
  protected static final int[] FLUSH_SCORES =    { 0, 0, 0, 4, 12, 25 };
  protected static final int[] STRAIGHT_SCORES = { 0, 0, 0, 3, 10, 20 };
  protected static final int[] STRFLUSH_SCORES = { 0, 0, 0, 7, 14, 40 };

  protected static final int TWO_PAIR_SCORE = 2;
  protected static final int FULL_HOUSE_SCORE = 30;
  protected static final int ROYAL_FLUSH_SCORE = 100;
}
