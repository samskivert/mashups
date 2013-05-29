//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import java.util.Comparator;

public class Hand {

  /** Identifies the different kinds of hands. */
  public static enum Kind { NONE, NOFAKIND, STRAIGHT, FLUSH, STRFLUSH, TWOPAIR, FULLHOUSE, ROYAL };

  /** A hand with zero score, used in lieu of null. */
  public static final Hand ZERO = new Hand();

  /** Compares two hands by score. */
  public static final Comparator<Hand> byScore = new Comparator<Hand>() {
    public int compare (Hand a, Hand b) {
      return b.score - a.score;
    }
  };

  /** Compares two hands by size (number of cards). */
  public static final Comparator<Hand> bySize = new Comparator<Hand>() {
    public int compare (Hand a, Hand b) {
      return b.size - a.size;
    }
  };

  // the minimum and maximum hand size (in cards)
  public static final int MIN = 2, MAX = 5;

  public final Cons<Coord> coords;
  public final Cons<Card> cards;
  public final Kind kind;
  public final int score;
  public final int size;

  public Hand (Cons<Coord> coords, Cons<Card> cards) {
    this.coords = coords;
    this.cards = cards;
    this.size = cards.size();

    // we enumerate all possible hands for scoring, so we don't have to worry about scoring
    // subsets of the hand in question; i.e. it's either entirely a straight (or flush) or not
    int sz = cards.size(), ranks = countRanks(cards), rank2s = countRank2s(cards);
    boolean isFlush = isSameSuit(cards), isStraight = isStraight(cards);
    if (isStraight && isFlush && sz == 5 && hasRank(cards, Rank.TEN) && hasRank(cards, Rank.ACE)) {
      this.kind = Kind.ROYAL;
      this.score = ROYAL_FLUSH_SCORE;
    } else if (isFlush && isStraight) {
      this.kind = Kind.STRFLUSH;
      this.score = STRFLUSH_SCORES[sz];
    } else if (isFlush) {
      this.kind = Kind.FLUSH;
      this.score = FLUSH_SCORES[sz];
    } else if (isStraight) {
      this.kind = Kind.STRAIGHT;
      this.score = STRAIGHT_SCORES[sz];
    } else if (ranks == 1) {
      this.kind = Kind.NOFAKIND;
      this.score = NKIND_SCORES[sz];
    } else if (ranks == 2 && rank2s == 2 && sz == 5) {
      this.kind = Kind.FULLHOUSE;
      this.score = FULL_HOUSE_SCORE;
    } else if (ranks == 2 && rank2s == 2 && sz == 4) {
      this.kind = Kind.TWOPAIR;
      this.score = TWO_PAIR_SCORE;
    } else {
      this.kind = Kind.NONE;
      this.score = 0;
    }
  }

  public String descrip () {
    switch (kind) {
    default:
    case      NONE: return "Nothin'"; // not used
    case  NOFAKIND: return (cards.size() == 2) ? "Pair" : (cards.size() + " of a kind");
    case  STRAIGHT: return cards.size() + " card straight";
    case     FLUSH: return cards.size() + " card flush";
    case  STRFLUSH: return cards.size() + " card straight Flush";
    case   TWOPAIR: return "Two pair";
    case FULLHOUSE: return "Full house";
    case     ROYAL: return "Royal Flush!";
    }
  }

  @Override public String toString () {
    return kind + " " + score + " : " + cards + " @ " + coords;
  }

  protected static boolean isSameSuit (Cons<Card> cards) {
    Suit suit = cards.head.suit;
    for (Cons<Card> card = cards.tail; card != null; card = card.tail) {
      if (card.head.suit != suit) return false;
    }
    return true;
  }

  protected static boolean isStraight (Cons<Card> cards) {
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

  protected static int countRanks (Cons<Card> cards) {
    int rankMask = 0;
    for (; cards != null; cards = cards.tail) {
      rankMask |= (1 << cards.head.rank.ordinal());
    }
    return Integer.bitCount(rankMask);
  }

  protected static int countRank2s (Cons<Card> cards) {
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

  private Hand () {
    this.cards = null;
    this.coords = null;
    this.kind = null;
    this.score = 0;
    this.size = 0;
  }

  protected static final int[] NKIND_SCORES =    { 0, 0, 1,  4, 10,  0 };
  protected static final int[] STRAIGHT_SCORES = { 0, 0, 0,  2,  5, 10 };
  protected static final int[] FLUSH_SCORES =    { 0, 0, 0,  2,  4,  8 };
  protected static final int[] STRFLUSH_SCORES = { 0, 0, 0, 10, 20, 40 };

  protected static final int TWO_PAIR_SCORE = 3;
  protected static final int FULL_HOUSE_SCORE = 5;
  protected static final int ROYAL_FLUSH_SCORE = 60;
}
