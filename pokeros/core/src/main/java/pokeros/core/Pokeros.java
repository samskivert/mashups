//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import playn.core.*;
import playn.core.util.Clock;

import tripleplay.game.ScreenStack;

public class Pokeros extends Game.Default {

  public final Media media = new Media();
  public final ScreenStack screens = new ScreenStack() {
    protected Transition defaultPushTransition () {
      return slide();
    }
    protected Transition defaultPopTransition () {
      return slide().left();
    }
  };

  public Pokeros () {
    super(33); // call update every 33ms (30 times per second)
  }

  @Override
  public void init () {
    screens.push(new MainMenuScreen(this));
  }

  @Override
  public void update (int delta) {
    _clock.update(delta);
    screens.update(delta);
  }

  @Override
  public void paint (float alpha) {
    _clock.paint(alpha);
    screens.paint(_clock);
  }

  protected final Clock.Source _clock = new Clock.Source(33);
}
