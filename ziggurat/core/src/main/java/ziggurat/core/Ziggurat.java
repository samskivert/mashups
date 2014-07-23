//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core;

import java.util.Random;
import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.util.Clock;
import static playn.core.PlayN.*;
import tripleplay.game.ScreenStack;
import tripleplay.util.Randoms;

import ziggurat.core.gensys.TestScreen;
import ziggurat.core.zone.ZoneTestScreen;

public class Ziggurat extends Game.Default {

  public final Randoms rando = Randoms.with(new Random());

  public Ziggurat() {
    super(33); // call update every 33ms (30 times per second)
  }

  @Override
  public void init() {
    _stack.push(new ZoneTestScreen(this));
  }

  @Override
  public void update(int delta) {
    _clock.update(delta);
    _stack.update(delta);
  }

  @Override
  public void paint(float alpha) {
    _clock.paint(alpha);
    _stack.paint(_clock);
  }

  private final Clock.Source _clock = new Clock.Source(33);
  private final ScreenStack _stack = new ScreenStack();
}
