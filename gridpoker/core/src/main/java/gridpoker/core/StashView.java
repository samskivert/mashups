//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import react.RSet;
import react.Value;

import playn.core.*;
import static playn.core.PlayN.*;

/** Displays a player's stash, and allows one card to be "highlighted". */
public class StashView {

  /** The layer that holds all of our cards. */
  public final GroupLayer layer = graphics().createGroupLayer();

  /** The currently selected card. */
  public final Value<CardSprite> selection = Value.create(null);

  public StashView (final Media media, RSet<Card> stash) {
    _slots = new CardSprite[Player.STASH];
    _xOff = Math.min(Media.CARD_WID+5, (graphics().width()-Media.CARD_WID)/(Player.STASH-1));
    float width = Media.CARD_WID + _xOff*(Player.STASH-1), height = Media.CARD_HEI;
    layer.setTranslation((graphics().width()-width)/2, graphics().height()-height-10);

    stash.connect(new RSet.Listener<Card>() {
      @Override public void onAdd (Card card) {
        // look for an empty slot
        for (int ii = 0; ii < _slots.length; ii++) {
          if (_slots[ii] == null) {
            add(ii, new CardSprite(media, card));
            return;
          }
        }
        throw new AssertionError("No empty slot for " + card);
      }
      @Override public void onRemove (Card card) {
        select(-1);
        for (int ii = 0; ii < _slots.length; ii++) {
          CardSprite cs = _slots[ii];
          if (cs != null && cs.card == card) {
            cs.layer.destroy();
            _slots[ii] = null;
            return;
          }
        }
        throw new AssertionError("Unknown card removed: " + card);
      }
    });
  }

  protected void add (final int index, CardSprite sprite) {
    _slots[index] = sprite;
    sprite.layer.setDepth(index);
    layer.addAt(sprite.layer, _xOff*index, 0);
    sprite.layer.addListener(new Pointer.Adapter() {
      public void onPointerStart (Pointer.Event event) {
        select(index);
      }
    });
  }

  protected void select (int index) {
    CardSprite oldsel = selection.get();
    if (oldsel != null) oldsel.layer.setTy(0);
    if (index < 0) selection.update(null);
    else {
      selection.update(_slots[index]);
      _slots[index].layer.setTy(-10);
    }
  }

  protected final CardSprite[] _slots;
  protected final float _xOff;
}
