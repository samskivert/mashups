//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

import playn.core.*;
import react.Slot;
import tripleplay.game.ScreenSpace;
import ziggurat.core.Ziggurat;

public class ZoneTestScreen extends ScreenSpace.Screen {

  public final Zone zone = Zone.makeTest();
  public final ZoneView view;

  public ZoneTestScreen (Ziggurat game) {
    super(game);

    view = new ZoneView(this, zone);

    // TEMP: for testing
    view.cam.clicked().connect(iact -> {
      int gx = Grid.toGrid(iact.local.x()), gy = Grid.toGrid(iact.local.y());
      view.cam.focusG(gx, gy);
    });
  }

  @Override public void init () {
    super.init();
    layer.add(view.layer);
  }
}
