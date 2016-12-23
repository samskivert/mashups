//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.gensys;

import java.util.Random;
import playn.core.*;
import playn.scene.CanvasLayer;
import playn.scene.Pointer;
import pythagoras.f.Point;

import tripleplay.game.ScreenSpace;
import tripleplay.util.Randoms;
import tripleplay.util.StyledText;
import tripleplay.util.TextStyle;

import ziggurat.core.Ziggurat;

public class TestScreen extends ScreenSpace.Screen {

  public final Randoms rando = Randoms.with(new Random());

  public TestScreen (Ziggurat game) {
    super(game);
  }

  @Override public void init () {
    super.init();
    final CanvasLayer vizLayer = new CanvasLayer(_game.plat.graphics(), 512, 512);
    layer.add(vizLayer);
    final CanvasLayer infoLayer = new CanvasLayer(_game.plat.graphics(), 128, 512);
    layer.addAt(infoLayer, 512, 0);
    update(vizLayer.begin(), infoLayer.begin());
    vizLayer.end();
    infoLayer.end();

    layer.events().connect(new Pointer.Listener() {
      public void onStart (Pointer.Interaction iact) {
        update(vizLayer.begin(), infoLayer.begin());
        vizLayer.end();
        infoLayer.end();
      }
    });
  }

  public void update (Canvas vizCanvas, Canvas infoCanvas) {
    vizCanvas.clear();
    Gene[] genes = Gene.random(rando, 7);
    // Generator gen = new Spiral1();
    Generator gen = new ColorMix();
    Generator.Info info = new Generator.Info();
    gen.generate(rando, genes, vizCanvas, info);

    StyledText.Block iblock = StyledText.block(
      _game.plat.graphics(), info.buf.toString(), INFO_STYLE, INFO_WRAP);
    infoCanvas.setFillColor(0xFFFFFFFF).fillRect(0, 0, infoCanvas.width, infoCanvas.height);
    iblock.render(infoCanvas, 5, 5);
  }

  // @Override public void wasRemoved () {
  // }

  protected final TextStyle INFO_STYLE = TextStyle.normal(new Font("Helvetica", 14), 0xFF000000);
  protected final float INFO_WRAP = 118;
}
