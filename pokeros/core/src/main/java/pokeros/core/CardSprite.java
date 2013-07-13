//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import playn.core.ImageLayer;
import static playn.core.PlayN.graphics;

public class CardSprite {

  public final ImageLayer layer;
  public final Card card;

  public CardSprite (Media media, Card card) {
    this.card = card;
    this.layer = graphics().createImageLayer(media.card(card));
    layer.setOrigin(layer.width()/2, layer.height()/2);
  }
}
