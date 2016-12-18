//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import playn.core.*;
import playn.scene.Mouse;
import playn.scene.Pointer;
import playn.scene.SceneGame;
import playn.scene.Touch;

import tripleplay.game.ScreenStack;

public class Pokeros extends SceneGame {

  public final float cardScale;
  public final Media media = new Media(plat);
  public final Pointer pointer = new Pointer(plat, rootLayer, false);
  public final History history = new History(plat);

  public final ScreenStack screens = new ScreenStack(this, rootLayer) {
    protected Transition defaultPushTransition () {
      return slide();
    }
    protected Transition defaultPopTransition () {
      return slide().right();
    }
  };

  public Pokeros (Platform plat, float cardScale) {
    super(plat, 33); // call update every 33ms (30 times per second)
    this.cardScale = cardScale;
    plat.input().touchEvents.connect(new Touch.Dispatcher(rootLayer, false));
    plat.input().mouseEvents.connect(new Mouse.Dispatcher(rootLayer, false));
    screens.push(new MainMenuScreen(this));
  }
}
