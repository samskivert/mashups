//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

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
    _dx = graphics().width() / (Player.STASH+1);
    layer.setTranslation(0, graphics().height()-Media.CARD_HHEI/2-5);

    stash.connect(new RSet.Listener<Card>() {
      @Override public void onAdd (Card card) {
        // look for an empty slot
        for (int ii = 0; ii < _slots.length; ii++) {
          if (_slots[ii] == null) {
            CardSprite cs = new CardSprite(media, card);
            cs.layer.setScale(0.5f);
            add(ii, cs);
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
    layer.addAt(sprite.layer, _dx+_dx*index, 0);
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
  protected final float _dx;
}
