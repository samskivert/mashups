//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import playn.core.GroupLayer;
import static playn.core.PlayN.graphics;

public class CardSprite {

  public final GroupLayer layer = graphics().createGroupLayer();
  public final Card card;

  public CardSprite (Media media, Card card) {
    this.card = card;
    layer.add(graphics().createImageLayer(media.card(card)));
  }
}
