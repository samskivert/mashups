//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

import java.util.List;
import pythagoras.f.MathUtil;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import react.*;

import playn.core.*;
import static playn.core.PlayN.*;
import playn.core.Mouse;

import tripleplay.game.UIAnimScreen;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

public class GameScreen extends UIAnimScreen {

  // state
  public final Grid grid = new Grid();
  public final Deck deck = new Deck();
  public final Value<Integer> turnHolder = Value.create(-1);
  public final Player[] players;

  // interaction
  public final Signal<Coord> click = Signal.create();

  // rendering
  public final Pokeros game;
  public final Media media;
  public final GroupLayer cardsL = graphics().createGroupLayer();
  public final GroupLayer movesL = graphics().createGroupLayer();
  public final StashView[] sviews;

  public GameScreen (Pokeros game, Player... players) {
    this.game = game;
    this.media = game.media;
    // this.media = new Media(); // temp for testing
    this.players = players;
    this.sviews = new StashView[players.length];
    for (int ii = 0; ii < sviews.length; ii++) {
      if (players[ii].isHuman()) {
        sviews[ii] = new StashView(media, players[ii].stash);
      }
    }
  }

  @Override public void wasAdded () {
    // render a solid green background
    ImageLayer bg = graphics().createImageLayer(media.felt);
    bg.setWidth(graphics().width());
    bg.setHeight(graphics().height());
    layer.add(bg);

    // render our cards above the background
    layer.add(cardsL);
    cardsL.setScale(0.5f);
    // put 0, 0 in the middle of the screen to start
    cardsL.setTranslation(graphics().width()/2, graphics().height()/2);
    cardsL.add(_lastPlayed); // add our last played card indicator
    cardsL.add(movesL); // add our legal moves indicator layer
    // add our stash views
    for (int ii = 0; ii < sviews.length; ii++) {
      if (sviews[ii] == null) continue;
      layer.add(sviews[ii].layer);
      sviews[ii].layer.setVisible(false);
    }

    // display the scores across the top
    Stylesheet sheet = SimpleStyles.newSheetBuilder().
      add(Element.class, Style.FONT.is(UI.defaultFont)).
      add(Label.class, Style.TEXT_EFFECT.shadow, Style.COLOR.is(0xFFFFFFFF),
          Style.SHADOW.is(0x99000000), Style.SHADOW_X.is(1f), Style.SHADOW_Y.is(1f)).
      create();
    Root root = iface.createRoot(AxisLayout.horizontal().stretchByDefault(), sheet, layer);
    for (int ii = 0; ii < players.length; ii++) {
      final int idx = ii;
      ValueView<String> turnInd = turnHolder.map(new Function<Integer,String>() {
        public String apply (Integer turnIdx) { return (idx == turnIdx) ? "★" : ""; }
      });
      root.add(new Group(AxisLayout.horizontal().gap(3), Style.VALIGN.bottom).add(
                 new ValueLabel(turnInd).setConstraint(Constraints.minSize("★")),
                 new Label(players[ii].name(ii) + ":").addStyles(Style.HALIGN.left),
                 new ValueLabel(players[ii].score).setConstraint(Constraints.minSize("000"))));
    }

    root.add(new Group(AxisLayout.horizontal().gap(3)).add(
               new Label("Cards:"), new ValueLabel(deck.cards.sizeView())));

    root.packToWidth(graphics().width()-10);
    root.layer.setTranslation(5, 25);

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
          int cx = Math.round(event.localX() / GRID_X);
          int cy = Math.round(event.localY() / GRID_Y);
          click.emit(Coord.get(cx, cy));
        }
      }

      protected Point _startO = new Point(), _start = new Point();
      protected boolean _scrolling;
      protected static final float SCROLL_THRESH = 5;
    });
    // TODO: handle pinch to zoom
    cardsL.addListener(new Mouse.LayerAdapter() {
      @Override public void onMouseWheelScroll (Mouse.WheelEvent event) {
        cardsL.setScale(MathUtil.clamp(cardsL.scaleX() + event.velocity()/20, 0.25f, 1f));
      }
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
              int nextTH = (turnHolder.get() + 1) % players.length;
              boolean outOfCards = deck.cards.isEmpty() && players[nextTH].stash.isEmpty();
              if (outOfCards || !grid.haveLegalMove()) endGame();
              else turnHolder.update(nextTH);
            }
          });
        }
      }
      // TODO: track sprites by Coord, and remove in onRemove?
    });

    // wire up some behavior when we click
    click.connect(new Slot<Coord>() { public void onEmit (Coord coord) {
      int thIdx = turnHolder.get();
      if (thIdx >= 0 && // the game is not over
          players[thIdx].isHuman() && // it's a human's turn
          grid.isLegalMove(coord) && // there's a card neighboring this spot
          sviews[thIdx].selection.get() != null) { // they have a card selected in their stash
        Card card = sviews[thIdx].selection.get().card;
        if (players[thIdx].stash.remove(card)) {
          grid.cards.put(coord, card);
        } else throw new AssertionError("Player lacks card " + card + " " + players[thIdx]);
      }
    }});

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
          move.setTranslation(coord.x * GRID_X, coord.y * GRID_Y);
          ii++;
        }
        for (; ii < ll; ii++) movesL.get(ii).setVisible(false);
      }
    });

    // update the current player's stash, run AI opponents, etc.
    turnHolder.connect(new Slot<Integer>() { public void onEmit (Integer thIdx) {
      // don't hide a human's cards while the AI is playing because it just flashes annoyingly
      if (thIdx < 0 || players[thIdx].isHuman()) {
        for (int ii = 0; ii < sviews.length; ii++) {
          if (sviews[ii] != null) sviews[ii].layer.setVisible(ii == thIdx);
        }
      }
      if (thIdx >= 0) {
        players[thIdx].upStash(deck);
        if (!players[thIdx].isHuman()) grid.makeMove(players[thIdx]);
      }
    }});

    // take the top card off the deck and place it at 0, 0; tell player 0 it's their turn
    grid.cards.put(Coord.get(0, 0), deck.cards.remove(0));
    turnHolder.update(0);

    // display some tips before the first turn and make them disappear once the first move is made
    final ImageLayer step1 = UI.mkTip("1. Tap a card to select it");
    final ImageLayer step2 = UI.mkTip("2. Tap a white square to play the card.");
    final ImageLayer step3 = UI.mkTip("3. Try to make, pairs, 3 of a kind, straights, etc.");
    layer.addAt(step1, 10, graphics().height() - Media.CARD_HHEI - step1.height() - 15);
    layer.addAt(step2, 10, graphics().height()/2 + Media.CARD_HHEI - 10);
    layer.addAt(step3, 10, graphics().height()/2 - Media.CARD_HHEI/2 - step3.height());
    turnHolder.connect(new UnitSlot() { public void onEmit () {
      step1.destroy();
      step2.destroy();
      step3.destroy();
    }}).once();
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
    List<Hand> hands = grid.bestHands(Hand.byScore, grid.cards.get(coord), coord);
    final int mult = hands.size();
    String multSuff = (mult > 1) ? (" x" + mult) : "";
    for (final Hand hand : hands) {
      if (hand.score == 0) continue;
      // System.err.println(hand);
      int thIdx = turnHolder.get();
      final IntValue score = players[thIdx].score;
      // glow the scoring hand, and then increment the player's score
      GroupLayer group = graphics().createGroupLayer();
      Rectangle rect = null;
      for (Cons<Coord> cs = hand.coords; cs != null; cs = cs.tail) {
        Layer glow = position(graphics().createImageLayer(media.glow), cs.head);
        glow.setOrigin(media.glow.width()/2, media.glow.height()/2);
        group.add(glow);
        if (rect == null) {
          rect = new Rectangle(glow.tx(), glow.ty(), Media.CARD_WID, Media.CARD_HEI);
        } else {
          rect.add(glow.tx(), glow.ty());
          rect.add(glow.tx() + Media.CARD_WID, glow.ty() + Media.CARD_HEI);
        }
      }
      String scstr = hand.score + multSuff;
      log().info(players[thIdx].name(thIdx) + ": " + hand.descrip() + " " + scstr);
      ImageLayer label = UI.mkScore(hand.descrip(), scstr, width());
      anim.delay(delay).then().
        add(cardsL, group).then().
        addAt(layer, label, (width()-label.width())/2, (height()-label.height())/2).then().
        tweenAlpha(group).to(0).in(1000).easeIn().then().
        tweenAlpha(label).to(0).in(1000).easeIn().then().
        destroy(group).then().
        destroy(label).then().
        action(new Runnable() { public void run () { score.increment(hand.score*mult); }});
      delay += 1250;
    }
  }

  protected void endGame () {
    turnHolder.update(-1);

    int maxScore = 0, winIdx = -1;
    for (int ii = 0; ii < players.length; ii++) {
      int score = players[ii].score.get();
      if (score < maxScore) continue;
      else if (score == maxScore) winIdx = -1;
      else { maxScore = score; winIdx = ii; }
    }

    ImageLayer winLayer = UI.mkMarquee(
      (winIdx < 0) ? "Tie game!" : (players[winIdx].name(winIdx) + " wins!"));
    layer.addAt(winLayer, (graphics().width() - winLayer.width())/2,
                (graphics().height() - winLayer.height())/2);

    Root root = iface.createRoot(AxisLayout.vertical(), UI.stylesheet(), layer);
    String msg = (winIdx == 0) ? "Yay!" : "Alas";
    root.add(new Button(msg).addStyles(UI.bigButtonStyles).onClick(new UnitSlot() {
      public void onEmit () { game.screens.remove(GameScreen.this); }
    }));
    root.pack();
    root.layer.setTranslation((width()-root.size().width())/2, height()-root.size().height()-15);
  }

  protected final ImmediateLayer _lastPlayed =
    graphics().createImmediateLayer(new ImmediateLayer.Renderer() {
      public void render (Surface surf) {
        surf.setFillColor(0xFF0000FF).fillRect(-Media.CARD_HWID-2, -Media.CARD_HHEI-2,
                                               Media.CARD_WID+4, Media.CARD_HEI+4);
      }
    });

  protected final float GRID_X = Media.CARD_WID + 5, GRID_Y = Media.CARD_HEI + 5;
}
