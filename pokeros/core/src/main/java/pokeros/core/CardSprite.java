//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import playn.scene.ImageLayer;
import playn.scene.GroupLayer;

public class CardSprite {

  public final GroupLayer layer;
  public final ImageLayer clayer;
  public final Card card;

  public CardSprite (Media media, Card card) {
    this.card = card;
    this.layer = new GroupLayer();
    clayer = new ImageLayer(media.card(card));
    clayer.setOrigin(clayer.width()/2, clayer.height()/2);
    this.layer.add(clayer);
    ImageLayer shadow = new ImageLayer(media.shadow.image);
    shadow.setOrigin(shadow.width()/2, shadow.height()/2);
    shadow.setDepth(-1);
    this.layer.addAt(shadow, 3, 3);
  }
}
