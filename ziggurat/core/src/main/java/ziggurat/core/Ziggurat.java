//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core;

import playn.core.Game;
import playn.core.util.Clock;
import react.Value;
import rsp.State;

public class Ziggurat extends Game.Default {

  public final State root = new State();
  public final Value<Integer> update = Value.create(0);
  public final Value<Clock> paint = Value.create(null);

  public final ScreenStack<Ziggurat> screens = new ScreenStack(this, root);

  public Ziggurat() {
    super(33); // call update every 33ms (30 times per second)
  }

  @Override
  public void init() {
    if (screens.isEmpty()) screens.push(new MainMenuScreen.S());
  }

  @Override
  public void update(int delta) {
    update.update(delta);
    _clock.update(delta);
  }

  @Override
  public void paint(float alpha) {
    _clock.paint(alpha);
    paint.updateForce(_clock);
  }

  private final Clock.Source _clock = new Clock.Source(33);
}
