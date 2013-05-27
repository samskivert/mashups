//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import react.RList;

import playn.core.ImageLayer;
import playn.core.GroupLayer;
import static playn.core.PlayN.graphics;

public class DeckSprite {

  // TODO: make this a group layer
  public final GroupLayer layer = graphics().createGroupLayer();

  public DeckSprite (Deck deck) {
    _deck = deck;
    deck.cards.connect(new RList.Listener<Card>() {
      @Override public void onRemove (Card card) { refreshTop(); }
    });

    for (int ii = _backs.length-1; ii >= 0; ii--) {
      float gap = (ii+1)*GAP;
      layer.addAt(_backs[ii] = graphics().createImageLayer(Media.cardBack()), gap, gap);
    }
    layer.add(_top);

    refreshTop();
  }

  protected void refreshTop () {
    if (_deck.cards.isEmpty()) layer.setVisible(false);
    else {
      _top.setImage(Media.card(_deck.cards.get(0)));
      int unflipped = _deck.cards.size()-1;
      for (int ii = 0; ii < _backs.length; ii++) {
        _backs[ii].setVisible(unflipped > ii);
      }
    }
  }

  protected final Deck _deck;
  protected final ImageLayer _top = graphics().createImageLayer();
  protected final ImageLayer[] _backs = new ImageLayer[3];
  protected final float GAP = 2;
}
