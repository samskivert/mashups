//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import pythagoras.f.FloatMath;
import pythagoras.f.Point;
import react.RMap;
import react.Signal;
import react.Slot;

import playn.core.*;
import static playn.core.PlayN.*;

import tripleplay.game.Screen;

public class GameScreen extends Screen {

  public final Grid grid = new Grid();
  public final Deck deck = new Deck();
  public final Signal<Coord> click = Signal.create();

  // a layer that contains our cards, which we'll scroll
  public final GroupLayer cardsL = graphics().createGroupLayer();

  @Override public void wasAdded () {
    // render a solid green background
    layer.add(graphics().createImmediateLayer(new ImmediateLayer.Renderer() {
      public void render (Surface surf) {
        surf.setFillColor(0xFF336600);
        surf.fillRect(0, 0, graphics().width(), graphics().height());
      }
    }));

    // render our cards above the background
    layer.add(cardsL);
    // put 0, 0 in the middle of the screen to start
    cardsL.setTranslation((graphics().width()-GRID_X)/2, (graphics().height()-GRID_Y)/2);

    // TEMP: scale cards layer down by half
    cardsL.setScale(0.5f);

    // listen for clicks and drags on the cards layer
    cardsL.setHitTester(new Layer.HitTester() {
      @Override public Layer hitTest (Layer layer, Point p) { return layer; }
    });
    cardsL.addListener(new Pointer.Adapter() {
      @Override public void onPointerStart (Pointer.Event event) {
        _start.set(event.x(), event.y());
        _startO.set(cardsL.tx(), cardsL.ty());
        _scrolling = false;
      }

      @Override public void onPointerDrag (Pointer.Event event) {
        float dx = event.x() - _start.x, dy = event.y() - _start.y;
        if (Math.abs(dx) > SCROLL_THRESH || Math.abs(dy) > SCROLL_THRESH) _scrolling = true;
        if (_scrolling) cardsL.setTranslation(_startO.x + dx, _startO.y + dy);
      }

      @Override public void onPointerEnd (Pointer.Event event) {
        if (!_scrolling) {
          int cx = FloatMath.ifloor(event.localX() / GRID_X);
          int cy = FloatMath.ifloor(event.localY() / GRID_Y);
          click.emit(new Coord(cx, cy));
        }
      }

      protected Point _startO = new Point(), _start = new Point();
      protected boolean _scrolling;
      protected static final float SCROLL_THRESH = 5;
    });

    // render the deck sprite above that
    layer.addAt(new DeckSprite(deck).layer, 10, 10);

    // add card sprites when cards are added to the board
    grid.cards.connect(new RMap.Listener<Coord,Card>() {
      @Override public void onPut (Coord coord, Card card) {
        CardSprite sprite = new CardSprite(card);
        cardsL.addAt(sprite.layer, GRID_X*coord.x, GRID_Y*coord.y);
      }
      // TODO: track sprites by Coord, and remove in onRemove?
    });

    // take the top card off the deck and place it at 0, 0
    grid.cards.put(new Coord(0, 0), deck.cards.remove(0));

    // TEMP: just slap the next card down wherever we click
    click.connect(new Slot<Coord>() {
      public void onEmit (Coord coord) {
        if (!deck.cards.isEmpty() && !grid.cards.containsKey(coord) && grid.hasNeighbor(coord)) {
          grid.cards.put(coord, deck.cards.remove(0));
        }
      }
    });
  }

  @Override public void wasRemoved () {
    while (layer.size() > 0) {
      layer.get(0).destroy();
    }
  }

  protected final int GRID_X = Media.CARD_WID + 5, GRID_Y = Media.CARD_HEI + 5;
}
