//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import pythagoras.f.FloatMath;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import react.*;

import playn.core.*;
import static playn.core.PlayN.*;

import tripleplay.game.UIAnimScreen;
import tripleplay.ui.*;
import tripleplay.ui.layout.TableLayout;
import tripleplay.util.TextConfig;

public class GameScreen extends UIAnimScreen {

  // state
  public final Grid grid = new Grid();
  public final Deck deck = new Deck();
  public final Value<Integer> turnHolder = Value.create(-1);
  public final Player[] players;
  public final IntValue[] scores;

  // interaction
  public final Signal<Coord> click = Signal.create();

  // rendering
  public final Media media = new Media();
  public final GroupLayer cardsL = graphics().createGroupLayer();
  public final GroupLayer movesL = graphics().createGroupLayer();

  public GameScreen (Player[] players) {
    this.players = players;
    this.scores = new IntValue[players.length];
    for (int ii = 0; ii < scores.length; ii++) scores[ii] = new IntValue(0);
  }

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
    // add our last played card indicator
    cardsL.add(_lastPlayed);
    // add our legal moves indicator layer
    cardsL.add(movesL);

    // TEMP: scale cards layer down
    // cardsL.setScale(0.5f);

    // render the deck sprite in the upper left
    layer.addAt(new DeckSprite(media, deck).layer, 10, 10);

    // display the scores in the upper right
    Root root = iface.createRoot(new TableLayout(2).gaps(5, 15), SimpleStyles.newSheet(), layer);
    root.addStyles(Style.BACKGROUND.is(Background.solid(0xFF99CCFF).inset(5)));
    root.add(TableLayout.colspan(new Label("Scores").addStyles(Style.FONT.is(HEADER_FONT)), 2));
    Layout.Constraint sizer = Constraints.minSize("000");
    for (int ii = 0; ii < scores.length; ii++) {
      root.add(new Label("Player " + (ii+1)),
               new ValueLabel(scores[ii]).setConstraint(sizer).addStyles(Style.HALIGN.right));
    }
    root.pack();
    root.layer.setTranslation(graphics().width()-root.size().width()-5, 5);

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
          click.emit(Coord.get(cx, cy));
        }
      }

      protected Point _startO = new Point(), _start = new Point();
      protected boolean _scrolling;
      protected static final float SCROLL_THRESH = 5;
    });

    // add card sprites when cards are added to the board
    grid.cards.connect(new RMap.Listener<Coord,Card>() {
      @Override public void onPut (Coord coord, Card card) {
        // add a new sprite to display the placed card
        CardSprite sprite = new CardSprite(media, card);
        cardsL.add(position(sprite.layer, coord));
        // position the last played sprite over this card
        position(_lastPlayed, coord);
        // we're notified once before the game starts, so ignore that one
        if (turnHolder.get() >= 0) {
          // score any valid hands made by this card
          scorePlacement(coord);
          anim.addBarrier();
          // wait for any animations to finish, then move to the next turn or end the game
          anim.action(new Runnable() {
            public void run () {
              if (deck.cards.isEmpty() || !grid.haveLegalMove()) endGame();
              else turnHolder.update((turnHolder.get() + 1) % scores.length);
            }
          });
        }
      }
      // TODO: track sprites by Coord, and remove in onRemove?
    });

    // wire up some behavior when we click
    click.connect(new Slot<Coord>() {
      public void onEmit (Coord coord) {
        int thIdx = turnHolder.get();
        if (thIdx >= 0 && // the game is not over
            players[thIdx] == Player.HUMAN && // it's a human's turn
            !deck.cards.isEmpty() && // there are cards left to play
            grid.isLegalMove(coord)) { // there's a card neighboring this spot
          grid.cards.put(coord, deck.cards.remove(0));
        }
      }
    });

    // wire up AI opponents
    turnHolder.connect(new Slot<Integer>() {
      public void onEmit (Integer thIdx) {
        if (thIdx < 0 || players[thIdx] == Player.HUMAN) return;
        grid.cards.put(grid.computeMove(players[thIdx], deck.cards.get(0)), deck.cards.remove(0));
      }
    });

    // add a display of legal moves
    turnHolder.connect(new Slot<Integer>() {
      public void onEmit (Integer thIdx) {
        movesL.setVisible(thIdx >= 0);
        int ii = 0, ll = movesL.size();
        java.util.Set<Coord> moves = grid.legalMoves();
        for (Coord coord : moves) {
          Layer move;
          if (ii < ll) move = movesL.get(ii).setVisible(true);
          else {
            move = graphics().createImageLayer(media.move);
            move.setOrigin(media.move.width()/2, media.move.height()/2);
            movesL.add(move);
          }
          move.setTranslation(coord.x * GRID_X + GRID_X/2, coord.y * GRID_Y + GRID_Y/2);
          ii++;
        }
        for (; ii < ll; ii++) movesL.get(ii).setVisible(false);
      }
    });

    // take the top card off the deck and place it at 0, 0; tell player 0 it's their turn
    grid.cards.put(Coord.get(0, 0), deck.cards.remove(0));
    turnHolder.update(0);
  }

  @Override public void wasRemoved () {
    while (layer.size() > 0) {
      layer.get(0).destroy();
    }
  }

  protected Layer position (Layer layer, Coord coord) {
    return layer.setTranslation(coord.x * GRID_X, coord.y * GRID_Y);
  }

  protected void scorePlacement (Coord coord) {
    int delay = 0;
    for (final Hand hand : grid.bestHands(Hand.byScore, grid.cards.get(coord), coord)) {
      if (hand.score == 0) continue;
      System.err.println(hand);
      final IntValue score = scores[turnHolder.get()];
      // glow the scoring hand, and then increment the player's score
      GroupLayer group = graphics().createGroupLayer();
      Rectangle rect = null;
      for (Cons<Coord> cs = hand.coords; cs != null; cs = cs.tail) {
        Layer glow = position(graphics().createImageLayer(media.glow), cs.head);
        group.add(glow);
        if (rect == null) {
          rect = new Rectangle(glow.tx(), glow.ty(), Media.CARD_WID, Media.CARD_HEI);
        } else {
          rect.add(glow.tx(), glow.ty());
          rect.add(glow.tx() + Media.CARD_WID, glow.ty() + Media.CARD_HEI);
        }
      }
      ImageLayer label = SCORE_CFG.toLayer(hand.descrip());
      label.setTranslation(rect.x + (rect.width - label.width())/2,
                           rect.y + (rect.height - label.height())/2);
      anim.delay(delay).then().
        add(cardsL, group).then().
        add(cardsL, label).then().
        tweenAlpha(group).to(0).in(500).easeIn().then().
        tweenAlpha(label).to(0).in(500).easeIn().then().
        destroy(group).then().
        destroy(label).then().
        action(new Runnable() {
          public void run () { score.increment(hand.score); }
        });
      delay += 750;
    }
  }

  protected void endGame () {
    turnHolder.update(-1);

    int maxScore = 0, winIdx = -1;
    for (int ii = 0; ii < scores.length; ii++) {
      int score = scores[ii].get();
      if (score < maxScore) continue;
      else if (score == maxScore) winIdx = -1;
      else { maxScore = score; winIdx = ii; }
    }

    String msg = (winIdx < 0) ? "Tie game!" : ("Player " + (winIdx+1) + " wins!");
    ImageLayer winLayer = GAME_OVER_CFG.toLayer(msg);
    layer.addAt(winLayer, (graphics().width() - winLayer.width())/2,
                (graphics().height() - winLayer.height())/2);
  }

  protected final ImmediateLayer _lastPlayed =
    graphics().createImmediateLayer(new ImmediateLayer.Renderer() {
      public void render (Surface surf) {
        surf.setFillColor(0xFF0000FF).fillRect(-2, -2, Media.CARD_WID+4, Media.CARD_HEI+4);
      }
    });

  protected final int GRID_X = Media.CARD_WID + 5, GRID_Y = Media.CARD_HEI + 5;
  protected final Font HEADER_FONT = graphics().createFont("Helvetica", Font.Style.BOLD, 16);

  protected final TextConfig SCORE_CFG = new TextConfig(0xFFFFFFFF).withOutline(0xFF000000, 1f).
    withFont(graphics().createFont("Helvetica", Font.Style.BOLD, 24));

  protected final TextConfig GAME_OVER_CFG = new TextConfig(0xFFFFFFFF).withOutline(0xFF000000, 3f).
    withFont(graphics().createFont("Helvetica", Font.Style.BOLD, 32));
}
