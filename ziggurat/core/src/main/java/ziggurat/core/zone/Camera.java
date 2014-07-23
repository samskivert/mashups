//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

import playn.core.*;
import playn.core.util.Clock;
import pythagoras.f.*;
import react.Signal;
import static playn.core.PlayN.*;
import tripleplay.anim.Flicker;

/** Controls the view "camera". */
public class Camera {

  /** The layer whose scale and translation we control. */
  public final Layer layer;

  /** The minimum/maximum camera positions. */
  public final IPoint min, max;

  public Camera (Layer layer, IRectangle bounds) {
    this.layer = layer;

    this.max = new Point(bounds.x(), bounds.y());
    this.min = new Point(bounds.x() - bounds.width() + swidth(),
                         bounds.y() - bounds.height() + sheight());

    // create and wire up our x/y flickers
    layer.addListener(_xFlicker = new Flicker(0, min.x(), max.x()) {
      @Override protected float getPosition (Pointer.Event event) { return event.x(); }
    });
    layer.addListener(_yFlicker = new Flicker(0, min.y(), max.y()));
  }

  /** A slot emitted when the user clicks on the target layer. Since the camera handles scrolling
    * interactions, it ends up detecting clicks on the target layer as well. */
  public Signal<Pointer.Event> clicked () { return _xFlicker.clicked; }

  /** Sets the camera focus to the center of the specified grid cell.
    * The camera will disable interaction and smoothly scroll to that location. */
  public void focusG (int gx, int gy) {
    focusS(Grid.toScreen(gx)+Grid.SIZE/2f, Grid.toScreen(gy)+Grid.SIZE/2f);
  }
  /** Sets the camera focus to the specified screen coordinates.
    * The camera will disable interaction and smoothly scroll to that location. */
  public void focusS (float x, float y) {
    _foc.set(swidth()/2-x, sheight()/2-y);
    setInteractive(false);
  }

  /** Snaps the camera to the center of the specified grid cell. */
  public void snapG (int gx, int gy) {
    snapS(Grid.toScreen(gx)+Grid.SIZE/2f, Grid.toScreen(gy)+Grid.SIZE/2f);
  }
  /** Snaps the camera to the specified screen coordinates. */
  public void snapS (float x, float y) {
    focusS(x, y);
    setInteractive(true);
  }

  public void update (int delta) {
    // TODO: anything?
  }

  public void paint (Clock clock) {
    if (_interactive) {
      _xFlicker.paint(clock);
      _yFlicker.paint(clock);
      layer.setTranslation(_xFlicker.position, _yFlicker.position);

    } else {
      float cx = layer.tx(), cy = layer.ty(), tx = _foc.x, ty = _foc.y;
      float dx = cx-tx, dy = cy-ty;
      // if we're close enough to our destination then go back to interactive movement
      if (Math.abs(dx) < 1 && Math.abs(dy) < 1) setInteractive(true);
      else {
        float dt = clock.dt()/1000f;
        float vx = _vel.x, vy = _vel.y;
        // apply a spring force from the focus to the camera
        float fx = -W*W*dx - 2*W*vx, fy = -W*W*dy - 2*W*vy;
        float nvx = vx + fx*dt, nvy = vy + fy*dt;
        _vel.x = nvx;
        _vel.y = nvy;
        layer.setTranslation(cx + nvx*dt, cy + nvy*dt);
      }
    }
  }

  private void setInteractive (boolean interactive) {
    if (interactive != _interactive) {
      _interactive = interactive;
      if (interactive) {
        _xFlicker.freeze();
        _yFlicker.freeze();
        _xFlicker.position = _foc.x;
        _yFlicker.position = _foc.y;
        layer.setTranslation(_foc.x, _foc.y);
      }
    }
  }

  private final float swidth () { return graphics().width(); }
  private final float sheight () { return graphics().height(); }

  protected final float W = 7; // spring frequency
  protected final Vector _foc = new Vector(), _vel = new Vector();
  protected final Flicker _xFlicker, _yFlicker;
  protected boolean _interactive = true;
}
