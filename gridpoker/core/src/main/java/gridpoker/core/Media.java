//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import playn.core.*;
import playn.core.util.Callback;
import static playn.core.PlayN.*;

public class Media {

  public static final int CARD_WID = 79, CARD_HEI = 123;
  public static final Image cards = assets().getImage("images/cards.png");
  public static final CanvasImage glow = graphics().createImage(CARD_WID, CARD_HEI);

  // stamp the shape of a card, in all white pixels, into the glow image
  static {
    cards.addCallback(new Callback<Image>() {
      public void onSuccess (Image cards) {
        glow.canvas().setFillColor(0xFFFFFFFF).fillRect(0, 0, CARD_WID, CARD_HEI);
        glow.canvas().setCompositeOperation(Canvas.Composite.DST_IN).drawImage(cardBack(), 0, 0);
      }
      public void onFailure (Throwable t) {} // oh noes!
    });
  }

  public static Image card (Card card) {
    return cards.subImage(card.rank.ordinal()*CARD_WID, card.suit.ordinal()*CARD_HEI,
                          CARD_WID, CARD_HEI);
  }

  public static Image cardBack () {
    return cards.subImage(CARD_WID*2, CARD_HEI*4, CARD_WID, CARD_HEI);
  }
}
