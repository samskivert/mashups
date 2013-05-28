//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import org.junit.Test;
import static org.junit.Assert.*;

public class RulesTest {

  @Test public void testRankDelta () {
    assertEquals(-1, Rank.ACE.delta(Rank.KING));
    assertEquals(-1, Rank.TWO.delta(Rank.ACE));
    assertEquals(-1, Rank.THREE.delta(Rank.TWO));
  }

  @Test public void testCountRank2s () {
    assertEquals(1, Rules.countRank2s(cards("AC", "KC", "TC", "TS")));
    assertEquals(1, Rules.countRank2s(cards("AC", "AD", "AH", "AS")));
    assertEquals(1, Rules.countRank2s(cards("AC", "AD", "AH", "AS", "2C")));
    assertEquals(2, Rules.countRank2s(cards("AC", "AD", "TC", "TD")));
    assertEquals(2, Rules.countRank2s(cards("AC", "AD", "2S", "2D", "2C")));
  }

  @Test public void testIsStraight () {
    // up and down
    assertIsStraight(cards("2C", "3C", "4H", "5D"));
    assertIsStraight(cards("KH", "QD", "JH", "TS"));

    // ace up, up to ace, ace down, and down to ace
    assertIsStraight(cards("AH", "2D", "3H"));
    assertIsStraight(cards("QH", "KC", "AH"));
    assertIsStraight(cards("3S", "2D", "AH"));
    assertIsStraight(cards("AH", "KD", "QS"));

    // make sure wraparound is not a striaght
    assertIsNotStraight(cards("2D", "AH", "KD", "QS"));
    assertIsNotStraight(cards("KD", "AH", "2D", "3S"));
  }

  @Test public void testSpecialScoring () {
    assertEquals(Rules.TWO_PAIR_SCORE, Rules.scoreHand(cards("KD", "KC", "JD", "JS")));
    assertEquals(Rules.TWO_PAIR_SCORE, Rules.scoreHand(cards("KD", "TC", "TD", "KS")));
    assertEquals(Rules.TWO_PAIR_SCORE, Rules.scoreHand(cards("TD", "3C", "TD", "3S")));

    assertEquals(Rules.FULL_HOUSE_SCORE, Rules.scoreHand(cards("KD", "KC", "JD", "JS", "JH")));
    assertEquals(Rules.FULL_HOUSE_SCORE, Rules.scoreHand(cards("KD", "JC", "KD", "JS", "JH")));
    assertEquals(Rules.FULL_HOUSE_SCORE, Rules.scoreHand(cards("KD", "JC", "JD", "JS", "KH")));
    assertEquals(Rules.FULL_HOUSE_SCORE, Rules.scoreHand(cards("KD", "JC", "KC", "JS", "KH")));

    assertEquals(Rules.ROYAL_FLUSH_SCORE, Rules.scoreHand(cards("AD", "KD", "QD", "JD", "TD")));
    assertEquals(Rules.ROYAL_FLUSH_SCORE, Rules.scoreHand(cards("TC", "JC", "QC", "KC", "AC")));
  }

  protected static void assertIsStraight (Cons<Card> cards) {
    if (!Rules.isStraight(cards)) fail(cards + " should be classified as a straight");
  }

  protected static void assertIsNotStraight (Cons<Card> cards) {
    if (Rules.isStraight(cards)) fail(cards + " should not be classified as a straight");
  }

  protected static Cons<Card> cards (String... descs) {
    Cons<Card> cards = Cons.root(card(descs[descs.length-1]));
    for (int ii = descs.length-2; ii >= 0; ii--) cards = cards.prepend(card(descs[ii]));
    return cards;
  }

  // turns strings like AH 4H 5C KD JS into Card instances
  protected static Card card (String desc) {
    int rank = RANKS.indexOf(desc.charAt(0)), suit = SUITS.indexOf(desc.charAt(1));
    return new Card(Suit.values()[suit], Rank.values()[rank]);
  }

  protected static final String RANKS = "A23456789TJQK", SUITS = "CDHS";
}
