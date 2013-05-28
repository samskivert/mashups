//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

/** Handles some rules logic (mainly scoring). */
public class Rules {

  public static int scoreHand (Cons<Card> hand) {
    // we enumerate all possible hands for scoring, so we don't have to worry about scoring subsets
    // of the hand in question; i.e. it's either entirely a straight (or flush) or not
    int hsize = hand.size(), ranks = countRanks(hand), rank2s = countRank2s(hand);
    boolean isFlush = isSameSuit(hand), isStraight = isStraight(hand);
    if (isStraight && isFlush && hsize == 5 && hasRank(hand, Rank.TEN) &&
        hasRank(hand, Rank.ACE))    return ROYAL_FLUSH_SCORE;
    else if (isFlush && isStraight) return STRFLUSH_SCORES[hsize];
    else if (isFlush)               return FLUSH_SCORES[hsize];
    else if (isStraight)            return STRAIGHT_SCORES[hsize];
    else if (ranks == 1)            return NKIND_SCORES[hsize];
    else if (ranks == 2 && rank2s == 2) {
      if (hsize == 5)               return FULL_HOUSE_SCORE;
      else if (hsize == 4)          return TWO_PAIR_SCORE;
    }
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
    // set a bit for each rank in the hand
    int rankMask = 0;
    for (Cons<Card> card = cards; card != null; card = card.tail) {
      int flag = card.head.rank.flag();
      if ((rankMask & flag) != 0) return false; // if we have a dupe, we're not a straight
      rankMask |= flag;
    }

    // handle aces being high/low
    int aceFlag = Rank.ACE.flag();
    if ((rankMask & aceFlag) != 0 && (rankMask & Rank.KING.flag()) == 0) {
      int lowAceFlag = Rank.TWO.flag() >> 1;
      rankMask &= ~aceFlag;
      rankMask |= lowAceFlag;
    }

    // now make sure our bits are in one contiguous sequence
    int streak = 0, count = cards.size();
    for (int ii = 0, ll = Rank.values().length; ii <= ll; ii++) {
      if ((rankMask & (1 << ii)) != 0) {
        streak += 1;
        if (streak == count) return true;
      } else if (streak != 0) return false;
    }
    throw new AssertionError("Impossible isStraight: " + cards);
  }

  public static int countRanks (Cons<Card> cards) {
    int rankMask = 0;
    for (; cards != null; cards = cards.tail) {
      rankMask |= (1 << cards.head.rank.ordinal());
    }
    return Integer.bitCount(rankMask);
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

  protected static boolean hasRank (Cons<Card> cards, Rank rank) {
    return (cards != null) && (cards.head.rank == rank || hasRank(cards.tail, rank));
  }

  protected static final int[] NKIND_SCORES =    { 0, 0, 1, 5, 16,  0 };
  protected static final int[] FLUSH_SCORES =    { 0, 0, 0, 4, 12, 25 };
  protected static final int[] STRAIGHT_SCORES = { 0, 0, 0, 3, 10, 20 };
  protected static final int[] STRFLUSH_SCORES = { 0, 0, 0, 7, 14, 40 };

  protected static final int TWO_PAIR_SCORE = 2;
  protected static final int FULL_HOUSE_SCORE = 30;
  protected static final int ROYAL_FLUSH_SCORE = 100;
}
