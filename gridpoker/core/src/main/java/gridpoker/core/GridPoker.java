//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import playn.core.*;
import playn.core.util.Clock;
import static playn.core.PlayN.*;

import tripleplay.game.ScreenStack;

public class GridPoker extends Game.Default {

  public GridPoker () {
    super(33); // call update every 33ms (30 times per second)
  }

  @Override
  public void init () {
    _screens.push(new GameScreen());

    keyboard().setListener(new Keyboard.Adapter() {
      @Override public void onKeyDown (Keyboard.Event event) {
        if (event.key() == Key.R) {
          _screens.remove(_screens.top());
          _screens.push(new GameScreen());
        }
      }
    });
  }

  @Override
  public void update (int delta) {
    _clock.update(delta);
    _screens.update(delta);
  }

  @Override
  public void paint (float alpha) {
    _clock.paint(alpha);
    _screens.paint(_clock);
  }

  protected final Clock.Source _clock = new Clock.Source(33);
  protected final ScreenStack _screens = new ScreenStack();
}
