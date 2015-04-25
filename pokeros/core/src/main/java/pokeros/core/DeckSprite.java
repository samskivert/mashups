//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import react.RList;

import playn.scene.ImageLayer;
import playn.scene.GroupLayer;

public class DeckSprite {

  // TODO: make this a group layer
  public final GroupLayer layer = new GroupLayer();

  public DeckSprite (Media media, Deck deck) {
    _media = media;
    _deck = deck;
    deck.cards.connect(new RList.Listener<Card>() {
      @Override public void onRemove (Card card) { refreshTop(); }
    });

    for (int ii = _backs.length-1; ii >= 0; ii--) {
      float gap = (ii+1)*GAP;
      layer.addAt(_backs[ii] = new ImageLayer(media.cardBack), gap, gap);
    }
    layer.add(_top);

    refreshTop();
  }

  protected void refreshTop () {
    if (_deck.cards.isEmpty()) layer.setVisible(false);
    else {
      _top.setSource(_media.card(_deck.cards.get(0)));
      int unflipped = _deck.cards.size()-1;
      for (int ii = 0; ii < _backs.length; ii++) {
        _backs[ii].setVisible(unflipped > ii);
      }
    }
  }

  protected final Media _media;
  protected final Deck _deck;
  protected final ImageLayer _top = new ImageLayer();
  protected final ImageLayer[] _backs = new ImageLayer[3];
  protected final float GAP = 2;
}
