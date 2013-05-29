//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import react.UnitSlot;

import playn.core.*;
import playn.core.util.Clock;
import static playn.core.PlayN.*;

import tripleplay.game.ScreenStack;

public class Pokeros extends Game.Default {

  public Pokeros () {
    super(33); // call update every 33ms (30 times per second)
  }

  @Override
  public void init () {
    keyboard().setListener(new Keyboard.Adapter() {
      @Override public void onKeyDown (Keyboard.Event event) {
        if (event.key() == Key.R) _restart.onEmit();
      }
    });

    _restart.onEmit();
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
  protected final UnitSlot _restart = new UnitSlot() { public void onEmit () {
    if (_screens.size() > 0) _screens.remove(_screens.top());
    _screens.push(new GameScreen(_restart, Player.human(), Player.computer()));
  }};
}
