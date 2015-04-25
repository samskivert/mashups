//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import playn.core.*;

import react.Slot;

public class Media {

  public static final float CARD_WID = 169/2f, CARD_HEI = 236/2f;
  public static final float CARD_HWID = CARD_WID/2, CARD_HHEI = CARD_HEI/2;

  public final Image[] cards;
  public final Image cardBack, feltTile;

  public final Image smile, frown;

  public final Canvas glow, shadow;
  public final Image move;

  // stamp the shape of a card, in all white pixels, into the glow image
  public Media (Platform plat) {
    cards = new Image[]{
      plat.assets().getImage("images/spades.png"),
      plat.assets().getImage("images/hearts.png"),
      plat.assets().getImage("images/clubs.png"),
      plat.assets().getImage("images/diamonds.png")
    };
    cardBack = plat.assets().getImage("images/redback.png");
    cardBack.state.onSuccess(new Slot<Image>() {
      public void onEmit (Image cards) {
        glow.setFillColor(0xFFFFFFFF).fillRect(0, 0, CARD_WID, CARD_HEI).
          setCompositeOperation(Canvas.Composite.DST_IN).draw(cardBack, 0, 0);
        shadow.setFillColor(0x66000000).fillRect(0, 0, CARD_WID, CARD_HEI).
          setCompositeOperation(Canvas.Composite.DST_IN).draw(cardBack, 0, 0);
      }
    });
    feltTile = plat.assets().getImage("images/felttile.jpg");

    smile = plat.assets().getImage("images/smile.png");
    frown = plat.assets().getImage("images/frown.png");

    glow = plat.graphics().createCanvas(CARD_WID, CARD_HEI);
    shadow = plat.graphics().createCanvas(CARD_WID, CARD_HEI);
    move = createMoveIndicator(plat);
  }

  public Image.Region card (Card card) {
    int x = card.rank.ordinal() % 5, y = card.rank.ordinal() / 5;
    return cards[card.suit.ordinal()].region(x*CARD_WID, y*CARD_HEI, CARD_WID, CARD_HEI);
  }

  protected Image createMoveIndicator (Platform plat) {
    Canvas cv = plat.graphics().createCanvas(CARD_WID/2, CARD_HEI/2);
    cv.setFillColor(0x66FFFFFF).fillRoundRect(0, 0, cv.width, cv.height, CARD_WID/8);
    return cv.image;
  }
}
