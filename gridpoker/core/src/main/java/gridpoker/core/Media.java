//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import playn.core.Image;
import static playn.core.PlayN.assets;

public class Media {

  public static final int CARD_WID = 79, CARD_HEI = 123;
  public static final Image cards = assets().getImage("images/cards.png");

  public static Image card (Card card) {
    return cards.subImage(card.rank.ordinal()*CARD_WID, card.suit.ordinal()*CARD_HEI,
                          CARD_WID, CARD_HEI);
  }

  public static Image cardBack () {
    return cards.subImage(CARD_WID*2, CARD_HEI*4, CARD_WID, CARD_HEI);
  }
}
