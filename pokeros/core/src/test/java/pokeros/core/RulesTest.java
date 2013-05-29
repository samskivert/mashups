//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class RulesTest {

  @Test public void testCountRank2s () {
    assertEquals(1, Hand.countRank2s(cards("AC", "KC", "TC", "TS")));
    assertEquals(1, Hand.countRank2s(cards("AC", "AD", "AH", "AS")));
    assertEquals(1, Hand.countRank2s(cards("AC", "AD", "AH", "AS", "2C")));
    assertEquals(2, Hand.countRank2s(cards("AC", "AD", "TC", "TD")));
    assertEquals(2, Hand.countRank2s(cards("AC", "AD", "2S", "2D", "2C")));
  }

  @Test public void testIsStraight () {
    // up and down
    assertIsStraight(cards("2C", "3C", "4H", "5D"));
    assertIsStraight(cards("KH", "QD", "JH", "TS"));

    // out of order
    assertIsStraight(cards("2C", "4H", "3C", "5D"));
    assertIsStraight(cards("QD", "JH", "TS", "KH"));

    // ace up, up to ace, ace down, and down to ace
    assertIsStraight(cards("AH", "2D", "3H"));
    assertIsStraight(cards("QH", "KC", "AH"));
    assertIsStraight(cards("3S", "2D", "AH"));
    assertIsStraight(cards("AH", "KD", "QS"));

    // make sure wraparound is not a striaght
    assertIsNotStraight(cards("2D", "AH", "KD", "QS"));
    assertIsNotStraight(cards("KD", "AH", "2D", "3S"));
  }

  // @Test public void testSpecialScoring () {
  //   assertEquals(Hand.TWO_PAIR_SCORE, Hand.scoreHand(cards("KD", "KC", "JD", "JS")));
  //   assertEquals(Hand.TWO_PAIR_SCORE, Hand.scoreHand(cards("KD", "TC", "TD", "KS")));
  //   assertEquals(Hand.TWO_PAIR_SCORE, Hand.scoreHand(cards("TD", "3C", "TD", "3S")));

  //   assertEquals(Hand.FULL_HOUSE_SCORE, Hand.scoreHand(cards("KD", "KC", "JD", "JS", "JH")));
  //   assertEquals(Hand.FULL_HOUSE_SCORE, Hand.scoreHand(cards("KD", "JC", "KD", "JS", "JH")));
  //   assertEquals(Hand.FULL_HOUSE_SCORE, Hand.scoreHand(cards("KD", "JC", "JD", "JS", "KH")));
  //   assertEquals(Hand.FULL_HOUSE_SCORE, Hand.scoreHand(cards("KD", "JC", "KC", "JS", "KH")));

  //   assertEquals(Hand.ROYAL_FLUSH_SCORE, Hand.scoreHand(cards("AD", "KD", "QD", "JD", "TD")));
  //   assertEquals(Hand.ROYAL_FLUSH_SCORE, Hand.scoreHand(cards("TC", "JC", "QC", "KC", "AC")));
  // }

  /*@Test*/ public void testHandFreq () {

    int[][] counts = new int[Hand.Kind.values().length][Hand.MAX+1];

    for (int ii = 0; ii < 1000; ii++) {
      Grid grid = new Grid();
      Deck deck = new Deck();

      grid.cards.put(Coord.get(0, 0), deck.cards.remove(0));

      while (!deck.cards.isEmpty()) {
        Card card = deck.cards.get(0);
        Coord bestCoord = null;
        List<Hand> bestHands = null;
        int bestScore = 0;
        for (Coord coord : grid.legalMoves()) {
          int score = 0;
          List<Hand> hands = grid.bestHands(Hand.byScore, card, coord);
          for (Hand hand : hands) score += hand.score;
          if (score >= bestScore) {
            bestCoord = coord;
            bestHands = hands;
            bestScore = score;
          }
        }
        if (bestHands == null) {
          System.out.println("No legal moves, ending game.");
          break;
        }
        for (Hand hand : bestHands) {
          counts[hand.kind.ordinal()][hand.cards.size()]++;
        }
        grid.cards.put(bestCoord, deck.cards.remove(0));
      }
    }

    String bars = "-----", fmt = "%10s %6s %5s %5s %5s\n";
    System.out.println("Raw counts:");
    System.out.printf(fmt, "Kind", "2", "3", "4", "5");
    System.out.printf(fmt, "----------", bars, bars, bars, bars);
    for (int ii = 0; ii < counts.length; ii++) {
      System.out.printf("%10s %6s %5s %5s %5s\n", Hand.Kind.values()[ii],
                        counts[ii][2], counts[ii][3], counts[ii][4], counts[ii][5]);
    }

    System.out.println("\nHand H as pairs/H (number of pairs played for every H):");
    System.out.printf(fmt, "Kind", "2", "3", "4", "5");
    System.out.printf(fmt, "----------", bars, bars, bars, bars);
    float pairs = counts[Hand.Kind.NOFAKIND.ordinal()][2];
    for (int ii = 0; ii < counts.length; ii++) {
      System.out.printf("%10s %6.1f %5.1f %5.1f %5.1f\n", Hand.Kind.values()[ii],
                        fmt(pairs, counts[ii][2]), fmt(pairs, counts[ii][3]),
                        fmt(pairs, counts[ii][4]), fmt(pairs, counts[ii][5]));
    }
  }

  protected static float fmt (float pairs, int count) {
    return count == 0 ? 0 : pairs/count;
  }

  protected static void assertIsStraight (Cons<Card> cards) {
    if (!Hand.isStraight(cards)) fail(cards + " should be classified as a straight");
  }

  protected static void assertIsNotStraight (Cons<Card> cards) {
    if (Hand.isStraight(cards)) fail(cards + " should not be classified as a straight");
  }

  protected static Cons<Card> cards (String... descs) {
    Cons<Card> cards = Cons.root(card(descs[descs.length-1]));
    for (int ii = descs.length-2; ii >= 0; ii--) cards = cards.prepend(card(descs[ii]));
    return cards;
  }

  // turns strings like AH 4H 5C KD JS into Card instances
  protected static Card card (String desc) {
    int rank = Rank.ABBREV.indexOf(desc.charAt(0)), suit = Suit.ABBREV.indexOf(desc.charAt(1));
    return new Card(Suit.values()[suit], Rank.values()[rank]);
  }
}
