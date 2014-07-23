//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

import playn.core.Pointer;
import playn.core.util.Clock;
import react.Slot;
import tripleplay.game.Screen;
import ziggurat.core.Ziggurat;

public class ZoneTestScreen extends Screen {

  public final Ziggurat game;
  public final Zone zone = Zone.makeTest();
  public final ZoneView view = new ZoneView(zone);

  public ZoneTestScreen (Ziggurat game) {
    this.game = game;

    // TEMP: for testing
    view.cam.clicked().connect(new Slot<Pointer.Event>() {
      public void onEmit (Pointer.Event event) {
        int gx = Grid.toGrid(event.localX()), gy = Grid.toGrid(event.localY());
        view.cam.focusG(gx, gy);
      }
    });
  }

  @Override public void wasAdded () {
    layer.add(view.layer);
  }

  @Override public void update (int delta) {
    view.update(delta);
  }

  @Override public void paint (Clock clock) {
    view.paint(clock);
  }
}
