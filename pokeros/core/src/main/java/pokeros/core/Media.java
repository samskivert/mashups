//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import playn.core.*;
import playn.core.util.Callback;
import static playn.core.PlayN.*;

public class Media {

  public static final float CARD_WID = 169/2f, CARD_HEI = 236/2f;
  public static final float CARD_HWID = CARD_WID/2, CARD_HHEI = CARD_HEI/2;

  public final Image[] cards = {
    assets().getImage("images/spades.png"),
    assets().getImage("images/hearts.png"),
    assets().getImage("images/clubs.png"),
    assets().getImage("images/diamonds.png")
  };
  public final Image cardBack = assets().getImage("images/redback.png");
  public final Image felt = assets().getImage("images/felt.jpg");
  public final CanvasImage glow = graphics().createImage(CARD_WID, CARD_HEI);
  public final Image move = createMoveIndicator();

  // stamp the shape of a card, in all white pixels, into the glow image
  public Media () {
    cardBack.addCallback(new Callback<Image>() {
      public void onSuccess (Image cards) {
        glow.canvas().setFillColor(0xFFFFFFFF).fillRect(0, 0, CARD_WID, CARD_HEI);
        glow.canvas().setCompositeOperation(Canvas.Composite.DST_IN).drawImage(cardBack, 0, 0);
      }
      public void onFailure (Throwable t) {} // oh noes!
    });
  }

  public Image card (Card card) {
    int x = card.rank.ordinal() % 5, y = card.rank.ordinal() / 5;
    return cards[card.suit.ordinal()].subImage(x*CARD_WID, y*CARD_HEI, CARD_WID, CARD_HEI);
  }

  protected Image createMoveIndicator () {
    CanvasImage image = graphics().createImage(CARD_WID/2, CARD_HEI/2);
    image.canvas().setFillColor(0x66FFFFFF).fillRoundRect(
      0, 0, image.width(), image.height(), CARD_WID/8);
    return image;
  }
}
