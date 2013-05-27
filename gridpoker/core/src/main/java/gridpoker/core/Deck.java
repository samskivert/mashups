//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import react.RList;

/** Models a deck of cards. */
public class Deck {

  public final RList<Card> cards = RList.create();

  public Deck () {
    reset();
  }

  public void reset () {
    cards.clear();
    List<Card> deck = new ArrayList<Card>();
    for (Suit suit : Suit.values()) for (Rank rank : Rank.values()) deck.add(new Card(suit, rank));
    Collections.shuffle(deck);
    cards.addAll(deck);
  }
}
