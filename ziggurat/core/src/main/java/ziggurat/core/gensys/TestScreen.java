//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.gensys;

import playn.core.*;
import playn.core.util.TextBlock;
import pythagoras.f.Point;
import static playn.core.PlayN.graphics;
import tripleplay.game.Screen;
import tripleplay.util.StyledText;
import tripleplay.util.TextStyle;
import ziggurat.core.Ziggurat;

public class TestScreen extends Screen {

  public final Ziggurat game;

  public TestScreen (Ziggurat game) {
    this.game = game;
  }

  @Override public void wasAdded () {
    final CanvasImage image = graphics().createImage(512, 512);
    layer.add(graphics().createImageLayer(image));
    final CanvasImage infoImage = graphics().createImage(128, 512);
    layer.addAt(graphics().createImageLayer(infoImage), 512, 0);
    update(image.canvas(), infoImage.canvas());

    layer.addListener(new Pointer.Adapter() {
      public void onPointerStart (Pointer.Event event) {
        update(image.canvas(), infoImage.canvas());
      }
    });
    layer.setHitTester(new Layer.HitTester() {
      public Layer hitTest (Layer layer, Point p) {
        return layer; // we're always a hit!
      }
    });
  }

  public void update (Canvas vizCanvas, Canvas infoCanvas) {
    vizCanvas.clear();
    Gene[] genes = Gene.random(game.rando, 7);
    // Generator gen = new Spiral1();
    Generator gen = new ColorMix();
    Generator.Info info = new Generator.Info();
    gen.generate(game.rando, genes, vizCanvas, info);

    StyledText.Block iblock = new StyledText.Block(
      info.buf.toString(), INFO_STYLE, INFO_WRAP, TextBlock.Align.LEFT);
    infoCanvas.setFillColor(0xFFFFFFFF).fillRect(0, 0, infoCanvas.width(), infoCanvas.height());
    iblock.render(infoCanvas, 5, 5);
  }

  // @Override public void wasRemoved () {
  // }

  protected final TextStyle INFO_STYLE = TextStyle.normal(
    graphics().createFont("Helvetica", Font.Style.PLAIN, 14), 0xFF000000);
  protected final TextWrap INFO_WRAP = new TextWrap(118);
}
