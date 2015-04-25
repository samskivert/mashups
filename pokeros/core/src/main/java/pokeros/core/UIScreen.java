//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import playn.core.Game;
import playn.core.Image;

import react.Slot;
import react.UnitSlot;

import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

public abstract class UIScreen extends ScreenStack.UIScreen {

  protected UIScreen (Pokeros game) {
    super(game.plat);
    _game = game;
  }

  @Override public Game game () {
    return _game;
  }

  @Override public void wasAdded () {
    final Root root = iface.createRoot(AxisLayout.vertical(), UI.stylesheet(game().plat), layer);
    _game.media.feltTile.state.onSuccess(new Slot<Image>() {
      public void onEmit (Image felt) {
        root.addStyles(Style.BACKGROUND.is(Background.tiled(felt.texture()).inset(10)));
      }
    });
    createUI(root);
    root.setSize(size());
  }

  @Override public void wasRemoved () {
    super.wasRemoved();
    iface.disposeRoots();
  }

  protected void addTitle (Root root, String title) {
    root.add(new Shim(1, 5),
             new Label(title).addStyles(UI.titleStyles.add(Style.FONT.is(UI.defaultFont.derive(64)),
                                                           Style.AUTO_SHRINK.on)));
  }

  protected void pop () {
    _game.screens.remove(this);
  }

  protected UnitSlot popSlot () {
    return new UnitSlot() { public void onEmit () { pop(); }};
  }

  protected abstract void createUI (Root root);

  protected final Pokeros _game;
}
