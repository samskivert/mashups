//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

import playn.core.*;
import playn.core.util.Clock;
import pythagoras.f.*;
import react.Value;
import static playn.core.PlayN.*;

/**
 * Displays a zone. Interoperates with {@link ZoneController} to allow interaction. The view assumes
 * it completely covers the screen. Other UI elements will be layered atop the zone view.
 */
public class ZoneView {

  /** The layer that contains all other zone layers. The scroll position of this layer controls the
    * viewport into the zone. */
  public final GroupLayer layer = graphics().createGroupLayer();

  /** The zone being displayed. */
  public final Zone zone;

  /** Controls the view "camera". */
  public final Camera cam;

  /** The currently selected unit, or null. */
  public final Value<Unit> selected = Value.create(null);

  /** The currently pending move, or null. */
  public final Value<Loc> pendingMove = Value.create(null);

  public ZoneView (Zone zone) {
    this.zone = zone;

    // wire up our layer hit tester (it's a hit!) and pointer listener
    layer.setHitTester(new Layer.HitTester() {
      public Layer hitTest (Layer layer, Point point) { return layer; }
    });

    // create our camera
    cam = new Camera(layer, new Rectangle(0, 0, Grid.toScreen(zone.width),
                                          Grid.toScreen(zone.height)));

    // add our terrian renderer first
    layer.add(graphics().createImmediateLayer(_terrainRenderer));

    // start the camera at the zone start location
    cam.snapG(zone.start.x, zone.start.y);
  }

  public void update (int delta) {
    cam.update(delta);
  }

  public void paint (Clock clock) {
    cam.paint(clock);
  }

  protected final ImmediateLayer.Renderer _terrainRenderer = new ImmediateLayer.Renderer() {
    public void render (Surface surf) {
      int gw = Grid.toGrid(graphics().width()), gh = Grid.toGrid(graphics().height());
      int gx = Grid.toGrid(-layer.tx()), gy = Grid.toGrid(-layer.ty());
      int xmax = gx+gw+1, ymax = gy+gh+1;
      for (int yy = gy-1; yy <= ymax; yy++) {
        float y = Grid.toScreen(yy), x = Grid.toScreen(gx-1);
        for (int xx = gx-1; xx <= xmax; xx++) {
          surf.setFillColor(zone.terrain(xx, yy).color);
          surf.fillRect(x, y, Grid.SIZE, Grid.SIZE);
          // TEMP?: render grid atop terrain
          surf.setFillColor(0xFF000000);
          surf.drawLine(x, y, x+Grid.SIZE, y, 1);
          surf.drawLine(x, y, x, y+Grid.SIZE, 1);
          // END TEMP
          x += Grid.SIZE;
        }
      }
    }
  };
}
