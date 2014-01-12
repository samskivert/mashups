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
import playn.core.Mouse;
import playn.core.util.Clock;
import static playn.core.PlayN.*;

import tripleplay.anim.AnimBuilder;
import tripleplay.anim.Animation;
import tripleplay.anim.Flicker;
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
  public final GroupLayer cardsBox = graphics().createGroupLayer();
  public final GroupLayer cardsL = graphics().createGroupLayer();
  public final GroupLayer movesL = graphics().createGroupLayer();
  public final StashView[] sviews;

  // scrolling
  public final Rectangle cardBounds = new Rectangle();
  public final Flicker flickX = new Flicker(0, 0, 1) {
    protected float getPosition (Pointer.Event event) { return event.x()/scale(); }
  };
  public final Flicker flickY = new Flicker(0, 0, 1) {
    protected float getPosition (Pointer.Event event) { return event.y()/scale(); }
  };
  public final Animation.XYValue flickPos = new Animation.XYValue() {
    public float initialX () { return flickX.position; }
    public float initialY () { return flickY.position; }
    public void set (float x, float y) {
      flickX.position = x;
      flickY.position = y;
    }
  };

  public GameScreen (Pokeros game) {
    this(game, Player.human(), Player.computer());
  }

  public GameScreen (Pokeros game, Player... players) {
    this.game = game;
    this.media = game.media;
    this.players = players;
    this.sviews = new StashView[players.length];
    for (int ii = 0; ii < sviews.length; ii++) {
      if (players[ii].isHuman()) {
        sviews[ii] = new StashView(media, players[ii].stash);
      }
    }
  }

  @Override public void wasAdded () {
    // CanvasImage center = graphics().createImage(20, 20);
    // center.canvas().setFillColor(0xFFFFCC99).fillCircle(10, 10, 10);
    // cardsBox.add(graphics().createImageLayer(center).setOrigin(10, 10));

    // create a layer to hold the cards layer which will put (0, 0) in the middle of the screen
    cardsBox.setTranslation(width()/2, height()/2);
    layer.add(cardsBox);
    updateScale(0.5f);
    cardsBox.add(cardsL);
    cardsL.add(_lastPlayed); // add our last played card indicator
    cardsL.add(movesL); // add our legal moves indicator layer

    // render the felt background such that it scrolls with the cards
    cardsL.add(graphics().createImmediateLayer(new ImmediateLayer.Renderer() {
      public void render (Surface surf) {
        int fwid = (int)media.feltTile.width(), fhei = (int)media.feltTile.height();
        float vwid = width()/scale(), vhei = height()/scale();
        float ctx = -cardsL.tx(), cty = -cardsL.ty();
        float left = ctx - vwid/2, top = cty - vhei/2, right = ctx + vwid/2, bot = cty + vhei/2;
        int qx = MathUtil.ifloor(left / fwid), qy = MathUtil.ifloor(top / fhei);
        for (float y = qy*fhei; y < bot; y += fhei) {
          for (float x = qx*fwid; x < right; x += fwid) {
            surf.drawImage(media.feltTile, x, y);
          }
        }
      }
    }).setDepth(-1));

    // add our stash views
    for (int ii = 0; ii < sviews.length; ii++) {
      if (sviews[ii] == null) continue;
      layer.add(sviews[ii].layer);
      sviews[ii].layer.setVisible(false);
    }

    // display the scores across the top
    Stylesheet sheet = UI.newBuilder().add(Label.class, UI.titleStyles).create();
    Root root = iface.createRoot(AxisLayout.horizontal().stretchByDefault(), sheet, layer);
    for (int ii = 0; ii < players.length; ii++) {
      root.add(new Group(AxisLayout.horizontal().gap(3), Style.VALIGN.bottom).add(
                 new Label(Player.WHO[ii] + ":").addStyles(Style.HALIGN.left),
                 new ValueLabel(players[ii].score).setConstraint(Constraints.minSize("000"))));
    }

    root.add(new Group(AxisLayout.horizontal().gap(3)).add(
               new Label("Cards:"), new ValueLabel(deck.cards.sizeView())));

    root.packToWidth(width()-10);
    root.layer.setTranslation(5, 25);

    Root scoring = iface.createRoot(AxisLayout.vertical(), sheet, layer);
    scoring.add(new Button("S").addStyles(UI.medButtonStyles).onClick(new UnitSlot() {
      public void onEmit () {
        game.screens.push(new ScoreScreen(game), game.screens.flip().duration(500).easeInOut());
      }
    }));
    scoring.pack();
    scoring.layer.setTranslation(15, height()-5-scoring.size().height());

    Root quit = iface.createRoot(AxisLayout.vertical(), sheet, layer);
    quit.add(new Button("Q").addStyles(UI.medButtonStyles).onClick(new UnitSlot() {
      public void onEmit () {
        game.screens.remove(GameScreen.this); // TODO: confirm dialog
      }
    }));
    quit.pack();
    quit.layer.setTranslation(width()-quit.size().width()-15, height()-5-quit.size().height());

    // listen for clicks and drags on the cards layer
    cardsL.setHitTester(new Layer.HitTester() {
      @Override public Layer hitTest (Layer layer, Point p) { return layer; }
    });

    // use a single entity to handle scrolling and scaling so that it can avoid scrolling while
    // scaling, as that's weird
    class ScrollScaler implements Pointer.Listener, Touch.LayerListener {
      @Override public void onPointerStart (Pointer.Event event) {
        _start.set(event.x(), event.y());
        _scrolling = false;
        // delegate to our flickers
        flickX.onPointerStart(event);
        flickY.onPointerStart(event);
      }
      @Override public void onPointerDrag (Pointer.Event event) {
        float scale = scale(), dx = (event.x() - _start.x) / scale,
          dy = (event.y() - _start.y) / scale;
        if (Math.abs(dx) > SCROLL_THRESH || Math.abs(dy) > SCROLL_THRESH) _scrolling = true;
        if (_scrolling && !isScaling()) {
          // delegate to our flickers
          flickX.onPointerDrag(event);
          flickY.onPointerDrag(event);
        }
      }
      @Override public void onPointerEnd (Pointer.Event event) {
        if (!_scrolling) {
          int cx = Math.round(event.localX() / GRID_X);
          int cy = Math.round(event.localY() / GRID_Y);
          click.emit(Coord.get(cx, cy));
        } else if (!isScaling()) {
          flickX.onPointerEnd(event);
          flickY.onPointerEnd(event);
        }
      }
      @Override public void onPointerCancel (Pointer.Event event) {
        onPointerEnd(event);
      }

      @Override public void onTouchStart (Touch.Event touch) {
        if (_firstId == 0) {
          _firstId = touch.id();
          _firstPos.set(touch.x(), touch.y());
        } else if (_secondId == 0) {
          _secondId = touch.id();
          _secondPos.set(touch.x(), touch.y());
          _baseDist = _firstPos.distance(_secondPos);
          _baseScale = scale();
          // TODO: set scale "origin" to halfway point between touch 1 and 2
        } // otherwise ignore
      }
      @Override public void onTouchMove (Touch.Event touch) {
        if (_firstId == touch.id()) {
          _firstPos.set(touch.x(), touch.y());
        } else if (_secondId == touch.id()) {
          _secondPos.set(touch.x(), touch.y());
        }
        if (isScaling()) {
          float dist = _firstPos.distance(_secondPos);
          // System.err.println("Movement " + dist + " / " + _baseDist);
          updateScale(MathUtil.clamp(dist/_baseDist*_baseScale, 0.25f, 1f));
        }
      }
      @Override public void onTouchEnd (Touch.Event touch) {
        // if either the first or second touch ends, end the gesture
        if (_firstId == touch.id() || _secondId == touch.id()) {
          _firstId = _secondId = 0;
        }
      }
      @Override public void onTouchCancel (Touch.Event touch) {
        onTouchEnd(touch);
      }

      private boolean isScaling () {
        return _firstId != 0 && _secondId != 0;
      }

      // scrolling bits
      protected Point _start = new Point();
      protected boolean _scrolling;
      protected static final float SCROLL_THRESH = 5;

      // scaling bits
      protected int _firstId, _secondId;
      protected Point _firstPos = new Point(), _secondPos = new Point();
      protected float _baseDist, _baseScale;
    }
    ScrollScaler sser = new ScrollScaler();
    cardsL.addListener((Pointer.Listener)sser);
    cardsL.addListener((Touch.LayerListener)sser);

    cardsL.addListener(new Mouse.LayerAdapter() {
      @Override public void onMouseWheelScroll (Mouse.WheelEvent event) {
        updateScale(MathUtil.clamp(scale() + event.velocity()/20, 0.25f, 1f));
      }
    });

    // add card sprites when cards are added to the board
    grid.cards.connect(new RMap.Listener<Coord,Card>() {
      @Override public void onPut (final Coord coord, Card card) {
        final int thIdx = turnHolder.get();

        // turn off the legal moves while we animate the play; it will be turned back on once the
        // turn holder index advances
        anim.tweenAlpha(movesL).to(0).in(300);

        // add a new sprite to display the placed card
        CardSprite sprite = new CardSprite(media, card);
        // slide the card into place from its location in the stash
        float tx = coord.x * GRID_X, ty = coord.y * GRID_Y;
        AnimBuilder then;
        if (thIdx == 0) {
          Point start = Layer.Util.screenToLayer(cardsL, sviews[thIdx].lastRemoved, new Point());
          cardsL.addAt(sprite.layer, start.x, start.y);
          then = anim.tweenXY(sprite.layer).to(tx, ty).in(300).easeIn().then();
        }
        // unless it's the computer playing, in which case just jam it in
        else then = anim.addAt(cardsL, sprite.layer, tx, ty).then();

        // include this card in our scroll bounds, then update our flick bounds
        cardBounds.add(tx, ty);
        updateFlickBounds();

        // position the last played sprite over this card
        then.action(new Runnable() { public void run () {
          position(_lastPlayed, coord);
        }});

        // we're notified once before the game starts, so ignore that one
        if (thIdx >= 0) {
          // score any valid hands made by this card
          scorePlacement(coord);
          anim.addBarrier();
          // wait for any animations to finish, then move to the next turn or end the game
          anim.action(new Runnable() {
            public void run () {
              int nextTH = (thIdx + 1) % players.length;
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
        anim.tweenAlpha(movesL).to(thIdx >= 0 ? 1 : 0).in(300);
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

  @Override public void paint (Clock clock) {
    super.paint(clock);
    flickX.paint(clock);
    flickY.paint(clock);
    cardsL.setTranslation(flickX.position, flickY.position);
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
      int thIdx = turnHolder.get();
      final IntValue score = players[thIdx].score;
      // glow the scoring hand, and then increment the player's score
      GroupLayer group = graphics().createGroupLayer();
      Rectangle rect = null;
      for (Cons<Coord> cs = hand.coords; cs != null; cs = cs.tail) {
        Layer glow = position(graphics().createImageLayer(media.glow), cs.head);
        glow.setOrigin(media.glow.width()/2, media.glow.height()/2);
        group.add(glow);
        if (rect == null) rect = new Rectangle(glow.tx(), glow.ty(), 0, 0);
        else rect.add(glow.tx(), glow.ty());
      }
      String scstr = hand.score + multSuff;
      log().info(Player.WHO[thIdx] + ": " + hand.descrip() + " " + scstr);
      ImageLayer label = UI.mkScore(hand.descrip(), scstr, width());
      // bound our scroll target into our flick bounds
      float tx = MathUtil.clamp(-rect.centerX(), flickX.min, flickX.max);
      float ty = MathUtil.clamp(-rect.centerY(), flickY.min, flickY.max);
      anim.delay(delay).then().
        tween(flickPos).to(tx, ty).in(300).easeInOut().then().
        add(cardsL, group).then().
        addAt(layer, label, (width()-label.width())/2, height()/3-label.height()/2).then().
        tweenAlpha(group).to(0).in(750).easeIn().then().
        tweenAlpha(label).to(0).in(750).easeIn().then().
        destroy(group).then().
        destroy(label).then().
        action(new Runnable() { public void run () { score.increment(hand.score*mult); }});
      delay += 1500;
    }

    // if we animated nothing, then still queue up a delay animation to ensure that the computer
    // doesn't play immediately after we do
    if (delay == 0) anim.delay(500);
  }

  protected void endGame () {
    turnHolder.update(-1);

    int maxScore = 0, winIdx = -1;
    int[] scores = new int[players.length];
    for (int ii = 0; ii < players.length; ii++) {
      int score = players[ii].score.get();
      scores[ii] = score;
      if (score < maxScore) continue;
      else if (score == maxScore) winIdx = -1;
      else { maxScore = score; winIdx = ii; }
    }

    // note the game for posterity
    game.history.noteGame(winIdx, scores);

    // display a celebratory (or conciliatory) message
    String winMsg;
    switch (winIdx) {
    case 0: winMsg = "You win!"; break;
    case 1: winMsg = "HAL wins!"; break;
    default: winMsg = "Tie game!"; break;
    }
    ImageLayer winLayer = UI.mkMarquee(winMsg);
    layer.addAt(winLayer, (width() - winLayer.width())/2, (height() - winLayer.height())/2);

    Root root = iface.createRoot(AxisLayout.vertical(), UI.stylesheet(), layer);
    String msg = (winIdx == 0) ? "Yay!" : "Alas";
    root.add(new Button(msg).addStyles(UI.bigButtonStyles).onClick(new UnitSlot() {
      public void onEmit () { game.screens.remove(GameScreen.this); }
    }));
    root.pack();
    root.layer.setTranslation((width()-root.size().width())/2, height()-root.size().height()-15);
  }

  protected void updateScale (float scale) {
    cardsBox.setScale(scale);
    // updating the scale changes the size of the viewport and thus our flick bounds
    updateFlickBounds();
  }

  protected float scale () {
    return cardsBox.scaleX();
  }

  protected void updateFlickBounds () {
    float swidth = width()/scale(), sheight = height()/scale();
    float leftAtLeft = -swidth/2 - (cardBounds.x - GRID_X*1.5f);
    float rightAtRight = swidth/2 - (cardBounds.x + cardBounds.width + GRID_X*1.5f);
    flickX.min = Math.min(leftAtLeft, rightAtRight);
    flickX.max = Math.max(leftAtLeft, rightAtRight);
    float topAtTop = -sheight/2 - (cardBounds.y - GRID_Y*1.5f);
    float botAtBot = sheight/2 - (cardBounds.y + cardBounds.height + GRID_Y*2.5f);
    flickY.min = Math.min(topAtTop, botAtBot);
    flickY.max = Math.max(topAtTop, botAtBot);
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
