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
import playn.scene.*;
import playn.scene.Mouse;
import playn.scene.Pointer;
import playn.scene.Touch;

import tripleplay.anim.AnimBuilder;
import tripleplay.anim.Animation;
import tripleplay.anim.Flicker;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;

public class GameScreen extends ScreenStack.UIScreen {

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
  public final GroupLayer cardsBox = new GroupLayer();
  public final GroupLayer cardsL = new GroupLayer();
  public final GroupLayer movesL = new GroupLayer();
  public final StashView[] sviews;

  // scrolling
  public final Rectangle cardBounds = new Rectangle();
  public final Flicker flickX = new Flicker(0, 0, 1) {
    protected float getPosition (Pointer.Event event) { return event.x()/scale(); }
  }.connect(paint);
  public final Flicker flickY = new Flicker(0, 0, 1) {
    protected float getPosition (Pointer.Event event) { return event.y()/scale(); }
  }.connect(paint);
  public final Animation.XYValue flickPos = new Animation.XYValue() {
    public float initialX () { return flickX.position; }
    public float initialY () { return flickY.position; }
    public void set (float x, float y) {
      flickX.position = x;
      flickY.position = y;
    }
  };

  // random UI
  public Root scoringRoot, quitRoot;

  public GameScreen (Pokeros game) {
    this(game, Player.human(), Player.computer());
  }

  public GameScreen (Pokeros game, Player... players) {
    super(game);
    this.game = game;
    this.media = game.media;
    this.players = players;
    this.sviews = new StashView[players.length];
    for (int ii = 0; ii < sviews.length; ii++) {
      if (players[ii].isHuman()) {
        sviews[ii] = new StashView(game, media, players[ii].stash);
      }
    }

    paint.connect(new Slot<Clock>() {
      public void onEmit (Clock clock) {
        cardsL.setTranslation(flickX.position, flickY.position);
      }
    });
  }

  @Override public void wasAdded () {
    // CanvasImage center = graphics().createImage(20, 20);
    // center.canvas().setFillColor(0xFFFFCC99).fillCircle(10, 10, 10);
    // cardsBox.add(graphics().createImageLayer(center).setOrigin(10, 10));

    // create a layer to hold the cards layer which will put (0, 0) in the middle of the screen
    cardsBox.setTranslation(size().width()/2, size().height()/2);
    layer.add(cardsBox);
    updateScale(game.cardScale);
    cardsBox.add(cardsL);
    cardsL.add(_lastPlayed); // add our last played card indicator
    cardsL.add(movesL); // add our legal moves indicator layer

    // render the felt background such that it scrolls with the cards
    cardsL.add(new Layer() {
      @Override protected void paintImpl (Surface surf) {
        if (!media.feltTile.isLoaded()) return;
        int fwid = (int)media.feltTile.width(), fhei = (int)media.feltTile.height();
        float scale = GameScreen.this.scale();
        float vwid = size().width()/scale, vhei = size().height()/scale;
        float ctx = -cardsL.tx(), cty = -cardsL.ty();
        float left = ctx - vwid/2, top = cty - vhei/2, right = ctx + vwid/2, bot = cty + vhei/2;
        int qx = MathUtil.ifloor(left / fwid), qy = MathUtil.ifloor(top / fhei);
        for (float y = qy*fhei; y < bot; y += fhei) {
          for (float x = qx*fwid; x < right; x += fwid) {
            surf.draw(media.feltTile.texture(), x, y);
          }
        }
      }
    }.setDepth(-1));

    // add our stash views
    for (int ii = 0; ii < sviews.length; ii++) {
      if (sviews[ii] == null) continue;
      layer.add(sviews[ii].layer);
      sviews[ii].layer.setVisible(false);
    }

    // display the scores across the top
    Stylesheet sheet = UI.newBuilder(game().plat).add(Label.class, UI.titleStyles).create();
    Root root = iface.createRoot(AxisLayout.horizontal().stretchByDefault(), sheet, layer);
    for (int ii = 0; ii < players.length; ii++) {
      root.add(new Group(AxisLayout.horizontal().gap(3), Style.VALIGN.bottom).add(
        new Label(Player.WHO[ii] + ":").addStyles(Style.HALIGN.left),
        new Label(players[ii].score).setConstraint(
          Constraints.minSize(game().plat.graphics(), "000"))));
    }

    root.add(new Group(AxisLayout.horizontal().gap(3)).add(
               new Label("Cards:"), new Label(deck.cards.sizeView())));

    root.packToWidth(size().width()-10);
    root.layer.setTranslation(5, 25);

    scoringRoot = iface.createRoot(AxisLayout.vertical(), sheet, layer);
    scoringRoot.add(new Button("S").addStyles(UI.medButtonStyles).onClick(new UnitSlot() {
      public void onEmit () {
        game.screens.push(new ScoreScreen(game), game.screens.flip().duration(500).easeInOut());
      }
    }));
    scoringRoot.pack();
    scoringRoot.layer.setTranslation(15, size().height()-5-scoringRoot.size().height());

    quitRoot = iface.createRoot(AxisLayout.vertical(), sheet, layer);
    quitRoot.add(new Button("Q").addStyles(UI.medButtonStyles).onClick(new UnitSlot() {
      public void onEmit () {
        final Root root = iface.createRoot(
          AxisLayout.vertical(), UI.stylesheet(game().plat), layer);
        root.addStyles(Style.BACKGROUND.is(Background.solid(0xAA000000)));
        root.add(
          new Label("Really Quit?").addStyles(UI.titleStyles).addStyles(UI.bigButtonStyles),
          new Shim(20, 20),
          new Button("Yes, Quit").addStyles(UI.medButtonStyles).onClick(new UnitSlot() {
            public void onEmit () { game.screens.remove(GameScreen.this); }
          }),
          new Shim(20, 20),
          new Button("No, Resume").addStyles(UI.medButtonStyles).onClick(new UnitSlot() {
            public void onEmit () { iface.disposeRoot(root); }
          }));
        root.setSize(size());
      }
    }));
    quitRoot.pack();
    quitRoot.layer.setTranslation(size().width()-quitRoot.size().width()-15,
                                  size().height()-quitRoot.size().height()-5);

    // listen for clicks and drags on the cards layer
    cardsL.setHitTester(new Layer.HitTester() {
      @Override public Layer hitTest (Layer layer, Point p) { return layer; }
    });

    // use a single entity to handle scrolling and scaling so that it can avoid scrolling while
    // scaling, as that's weird
    class ScrollScaler {
      public final Pointer.Listener pointer = new Pointer.Listener() {
        @Override public void onStart (Pointer.Interaction iact) {
          // System.out.println(this + ".onStart " + iact);
          _start.set(iact.x(), iact.y());
          _scrolling = false;
          // delegate to our flickers
          flickX.onStart(iact);
          flickY.onStart(iact);
        }
        @Override public void onDrag (Pointer.Interaction iact) {
          float dx = (iact.x() - _start.x), dy = (iact.y() - _start.y);
          if (Math.abs(dx) > SCROLL_THRESH || Math.abs(dy) > SCROLL_THRESH) _scrolling = true;
          if (_scrolling && !isScaling()) {
            // delegate to our flickers
            flickX.onDrag(iact);
            flickY.onDrag(iact);
          }
        }
        @Override public void onEnd (Pointer.Interaction iact) {
          if (!_scrolling) {
            int cx = Math.round(iact.local.x / GRID_X);
            int cy = Math.round(iact.local.y / GRID_Y);
            click.emit(Coord.get(cx, cy));
          } else if (!isScaling()) {
            flickX.onEnd(iact);
            flickY.onEnd(iact);
          }
        }
        @Override public void onCancel (Pointer.Interaction iact) {
          onEnd(iact);
        }
      };
      public final Touch.Listener touch = new Touch.Listener() {
        @Override public void onStart (Touch.Interaction touch) {
          if (_firstId < 0) {
            _firstId = touch.event.id;
            _firstPos.set(touch.x(), touch.y());
          } else if (_secondId < 0) {
            _secondId = touch.event.id;
            _secondPos.set(touch.x(), touch.y());
            _baseDist = _firstPos.distance(_secondPos);
            _baseScale = scale();
            // TODO: set scale "origin" to halfway point between touch 1 and 2
          } // otherwise ignore
        }
        @Override public void onMove (Touch.Interaction touch) {
          if (_firstId == touch.event.id) {
            _firstPos.set(touch.x(), touch.y());
          } else if (_secondId == touch.event.id) {
            _secondPos.set(touch.x(), touch.y());
          }
          if (isScaling()) {
            float dist = _firstPos.distance(_secondPos);
            // System.err.println("Movement " + dist + " / " + _baseDist);
            updateScale(MathUtil.clamp(dist/_baseDist*_baseScale,
                                       game.cardScale/2, game.cardScale*2));
          }
        }
        @Override public void onEnd (Touch.Interaction touch) {
          // if either the first or second touch ends, end the gesture
          if (_firstId == touch.event.id || _secondId == touch.event.id) {
            _firstId = _secondId = -1;
          }
        }
        @Override public void onCancel (Touch.Interaction touch) {
          onEnd(touch);
        }
      };

      private boolean isScaling () {
        return _firstId >= 0 && _secondId >= 0;
      }

      // scrolling bits
      protected Point _start = new Point();
      protected boolean _scrolling;
      protected static final float SCROLL_THRESH = 10;

      // scaling bits
      protected int _firstId = -1, _secondId = -1;
      protected Point _firstPos = new Point(), _secondPos = new Point();
      protected float _baseDist, _baseScale;
    }
    ScrollScaler sser = new ScrollScaler();
    cardsL.events().connect(sser.pointer);
    cardsL.events().connect(sser.touch);

    cardsL.events().connect(new Mouse.Listener() {
      @Override public void onWheel (Mouse.WheelEvent event, Mouse.Interaction iact) {
        System.out.println("wheel " + event);
        updateScale(MathUtil.clamp(scale() + event.velocity/20, 0.25f, 1f));
      }
    });

    // add card sprites when cards are added to the board
    grid.cards.connect(new RMap.Listener<Coord,Card>() {
      @Override public void onPut (final Coord coord, Card card) {
        final int thIdx = turnHolder.get();

        // turn off the legal moves while we animate the play; it will be turned back on once the
        // turn holder index advances
        iface.anim.tweenAlpha(movesL).to(0).in(300);

        // add a new sprite to display the placed card
        CardSprite sprite = new CardSprite(media, card);
        // slide the card into place from its location in the stash
        float tx = coord.x * GRID_X, ty = coord.y * GRID_Y;
        AnimBuilder then;
        if (thIdx == 0) {
          Point start = LayerUtil.screenToLayer(cardsL, sviews[thIdx].lastRemoved, new Point());
          cardsL.addAt(sprite.layer, start.x, start.y);
          then = iface.anim.tweenXY(sprite.layer).to(tx, ty).in(300).easeIn().then();
        }
        // unless it's the computer playing, in which case just jam it in
        else then = iface.anim.addAt(cardsL, sprite.layer, tx, ty).then();

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
          iface.anim.addBarrier();
          // wait for any animations to finish, then move to the next turn or end the game
          iface.anim.action(new Runnable() {
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
        iface.anim.tweenAlpha(movesL).to(thIdx >= 0 ? 1 : 0).in(300);
        int ii = 0, ll = movesL.children();
        java.util.Set<Coord> moves = grid.legalMoves();
        for (Coord coord : moves) {
          Layer move;
          if (ii < ll) move = movesL.childAt(ii).setVisible(true);
          else {
            move = new ImageLayer(media.move);
            move.setOrigin(media.move.width()/2, media.move.height()/2);
            movesL.add(move);
          }
          move.setTranslation(coord.x * GRID_X, coord.y * GRID_Y);
          ii++;
        }
        for (; ii < ll; ii++) movesL.childAt(ii).setVisible(false);
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
    final ImageLayer step1 = UI.mkTip(game(), "1. Tap a card to select it");
    final ImageLayer step2 = UI.mkTip(game(), "2. Tap a white square to play the card.");
    final ImageLayer step3 = UI.mkTip(game(), "3. Try to make, pairs, 3 of a kind, straights, etc.");
    layer.addAt(step1, 10, size().height() - Media.CARD_HHEI - step1.height() - 15);
    layer.addAt(step2, 10, size().height()/2 + Media.CARD_HHEI - 10);
    layer.addAt(step3, 10, size().height()/2 - Media.CARD_HHEI/2 - step3.height());
    turnHolder.connect(new UnitSlot() { public void onEmit () {
      step1.close();
      step2.close();
      step3.close();
    }}).once();
  }

  @Override public void wasRemoved () {
    super.wasRemoved();
    iface.disposeRoots(); // clear all UI stuff
    layer.disposeAll(); // clear anything else
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
      GroupLayer group = new GroupLayer();
      Rectangle rect = null;
      for (Cons<Coord> cs = hand.coords; cs != null; cs = cs.tail) {
        Layer glow = position(new ImageLayer(media.glow.image), cs.head);
        glow.setOrigin(media.glow.width/2, media.glow.height/2);
        group.add(glow);
        if (rect == null) rect = new Rectangle(glow.tx(), glow.ty(), 0, 0);
        else rect.add(glow.tx(), glow.ty());
      }
      String scstr = hand.score + multSuff;
      game().plat.log().info(Player.WHO[thIdx] + ": " + hand.descrip() + " " + scstr);
      ImageLayer label = UI.mkScore(game(), hand.descrip(), scstr, size().width());
      // bound our scroll target into our flick bounds
      float tx = MathUtil.clamp(-rect.centerX(), flickX.min, flickX.max);
      float ty = MathUtil.clamp(-rect.centerY(), flickY.min, flickY.max);
      iface.anim.delay(delay).then().
        tween(flickPos).to(tx, ty).in(300).easeInOut().then().
        add(cardsL, group).then().
        addAt(layer, label, (size().width()-label.width())/2,
              2*size().height()/3-label.height()/2).then().
        tweenAlpha(group).to(0).in(750).easeIn().then().
        tweenAlpha(label).to(0).in(750).easeIn().then().
        dispose(group).then().
        dispose(label).then().
        action(new Runnable() { public void run () { score.increment(hand.score*mult); }});
      delay += 1500;
    }

    // if we animated nothing, then still queue up a delay animation to ensure that the computer
    // doesn't play immediately after we do
    if (delay == 0) iface.anim.delay(500);
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

    // remove the scoring and quit buttons
    iface.disposeRoot(scoringRoot);
    iface.disposeRoot(quitRoot);

    // note the game for posterity
    game.history.noteGame(winIdx, scores);

    // fade the rest of the screen out
    layer.add(new Layer() {
      @Override protected void paintImpl (Surface surf) {
        surf.setFillColor(0xAA000000).fillRect(0, 0, width(), height());
      }
    });

    // display a celebratory (or conciliatory) message
    String winMsg;
    switch (winIdx) {
    case 0: winMsg = "You win!"; break;
    case 1: winMsg = "HAL wins!"; break;
    default: winMsg = "Tie game!"; break;
    }
    ImageLayer winLayer = UI.mkMarquee(game(), winMsg);
    layer.addCenterAt(winLayer, size().width()/2, size().height()/2);

    Root root = iface.createRoot(AxisLayout.vertical(), UI.stylesheet(game().plat), layer);
    String msg = (winIdx == 0) ? "Yay!" : "Alas";
    root.add(new Button(msg).addStyles(UI.bigButtonStyles).addStyles(Style.SHADOW.is(0xFF000000)).
             onClick(new UnitSlot() {
               public void onEmit () { game.screens.remove(GameScreen.this); }
             }));
    root.pack();
    root.layer.setTranslation((size().width()-root.size().width())/2,
                              size().height()-root.size().height()-15);
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
    float swidth = size().width()/scale(), sheight = size().height()/scale();
    float leftAtLeft = -swidth/2 - (cardBounds.x - GRID_X*1.5f);
    float rightAtRight = swidth/2 - (cardBounds.x + cardBounds.width + GRID_X*1.5f);
    flickX.min = Math.min(leftAtLeft, rightAtRight);
    flickX.max = Math.max(leftAtLeft, rightAtRight);
    float topAtTop = -sheight/2 - (cardBounds.y - GRID_Y*1.5f);
    float botAtBot = sheight/2 - (cardBounds.y + cardBounds.height + GRID_Y*2.5f);
    flickY.min = Math.min(topAtTop, botAtBot);
    flickY.max = Math.max(topAtTop, botAtBot);
  }

  protected final Layer _lastPlayed = new Layer() {
    @Override protected void paintImpl (Surface surf) {
      surf.setFillColor(0xFF0000FF).fillRect(-Media.CARD_HWID-2, -Media.CARD_HHEI-2,
                                             Media.CARD_WID+4, Media.CARD_HEI+4);
    }
  };

  protected final float GRID_X = Media.CARD_WID + 5, GRID_Y = Media.CARD_HEI + 5;
}
