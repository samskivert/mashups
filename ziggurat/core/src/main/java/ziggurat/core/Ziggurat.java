//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core;

import playn.core.Platform;
import playn.scene.Pointer;
import playn.scene.SceneGame;

import tripleplay.game.ScreenSpace;

import ziggurat.core.gensys.TestScreen;
import ziggurat.core.zone.ZoneTestScreen;

public class Ziggurat extends SceneGame {

  public final Pointer pointer;
  public final ScreenSpace screens;

  public Ziggurat (Platform plat) {
    super(plat, 33);

    // wire up pointer and mouse event dispatch
    pointer = new Pointer(plat, rootLayer, true);
    // plat.input().mouseEvents.connect(new Mouse.Dispatcher(rootLayer, false));

    screens = new ScreenSpace(this, rootLayer);
    // screens.add(new ZoneTestScreen(this), ScreenSpace.DOWN);
    screens.add(new TestScreen(this), ScreenSpace.DOWN);
  }
}
