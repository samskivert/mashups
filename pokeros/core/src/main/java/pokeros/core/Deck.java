//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import react.RList;

import tripleplay.util.Randoms;

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
    _rando.shuffle(deck);
    cards.addAll(deck);
  }

  protected final Randoms _rando = Randoms.with(new Random());
}
