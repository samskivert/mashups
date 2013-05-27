//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import react.RMap;

/** Models the card grid; contains some logics. */
public class Grid {

  /** The grid of cards. */
  public final RMap<Coord,Card> cards = RMap.create();

  /** Returns true if {@code coord} has a non-empty neighbor. */
  public boolean hasNeighbor (Coord coord) {
    return (cards.containsKey(coord.left())  || cards.containsKey(coord.right()) ||
            cards.containsKey(coord.above()) || cards.containsKey(coord.below()));
  }

  /** Returns true if placing a card at {@code coord} is a legal move. */
  public boolean isLegalMove (Coord coord) {
    // TODO: disallow extending rows/cols of five cards
    return !cards.containsKey(coord) && hasNeighbor(coord);
  }

  public List<Hand> bestHands (Card card, Coord coord) {
    List<Hand> hands = new ArrayList<Hand>();
    bestHands(card, coord, true, hands);
    bestHands(card, coord, false, hands);
    return hands;
  }

  /** Returns the best hand or hands in the horizontal or vertical direction. The method computes
   * the best hand starting at {@code coord} and extending "backwards", as well as starting at
   * {@code coord} and extending "forwards", and the best hand that overlaps {@code coord}. If the
   * score of the overlapping hand exceeds the sum of the non-overlapping hands, it is returned,
   * otherwise the non-overlapping hands are returned. Only non-zero scoring hands will be
   * returned. */
  public void bestHands (Card card, Coord coord, boolean horiz, List<Hand> into) {
    Hand bestBack = horiz ? bestHandFrom(card, coord, -1, 0) : bestHandFrom(card, coord, 0, -1);
    Hand bestFwd = horiz ? bestHandFrom(card, coord, 1, 0) : bestHandFrom(card, coord, 0, 1);
    Hand bestLap = bestHandOver(card, coord, horiz);

    if (bestLap.score > bestFwd.score + bestBack.score) into.add(bestLap);
    else {
      if (bestFwd.score > 0) into.add(bestFwd);
      if (bestBack.score > 0) into.add(bestBack);
    }
  }

  /** Returns the best scoring hand starting at {@code coord} and extending into the grid in the
   * direction defined by {@code dx,dy}. */
  public Hand bestHandFrom (Card card, Coord coord, int dx, int dy) {
    Cons<Card> cards = Cons.root(card);
    Cons<Coord> coords = Cons.root(coord);
    Hand best = new Hand(coords, cards, Rules.scoreHand(cards));
    for (int ii = 1; ii < Hand.MAX; ii++) {
      coord = coord.near(dx, dy);
      Card cd = this.cards.get(coord);
      if (cd == null) return best;
      coords = coords.prepend(coord);
      cards = cards.prepend(cd);
      int score = Rules.scoreHand(cards);
      if (score > best.score) best = new Hand(coords, cards, score);
    }
    return best;
  }

  /** Creates and scores all hands from length two to five, extending either horizontally or
   * vertically (per {@code horiz}), which extend beyond {@code coord} by at least one card in both
   * directions. Returns the best scoring hand. */
  public Hand bestHandOver (Card card, Coord coord, boolean horiz) {
    // determine the minimum and maximum card in the run and its length
    int dx = horiz ? 1 : 0, dy = horiz ? 0 : 1;
    Coord min = coord, max = coord;
    for (int ii = 0; ii < Hand.MAX-2; ii++) {
      Coord before = min.near(-dx, -dy);
      if (cards.containsKey(before)) min = before;
    }
    if (min == coord) return Hand.ZERO;
    for (int ii = 0; ii < Hand.MAX-2; ii++) {
      Coord after = max.near(dx, dy);
      if (cards.containsKey(after)) max = after;
    }
    if (max == coord) return Hand.ZERO;
    int length = (horiz ? (max.x - min.x) : (max.y - min.y)) + 1;

    // now slide windows of length 5, 4, 3, 2 over this run to make hands
    int minv = horiz ? min.x : min.y, maxv = horiz ? max.x : max.y;
    int coordv = horiz ? coord.x : coord.y;
    Hand best = Hand.ZERO;
    for (int span = Hand.MAX; span >= Hand.MIN; span--) {
      if (length < span) continue; // run's not long enough for this window
      for (int vv = Math.max(coordv - span + 1, minv); vv <= coordv; vv++) {
        if (vv + span - 1 > maxv) continue;
        Cons<Coord> coords = Coord.span(vv, span, coord.x, coord.y, dx, dy);
        Cons<Card> cards = cards(coords, card, coord);
        int score = Rules.scoreHand(cards);
        if (score > best.score) best = new Hand(coords, cards, score);
      }
    }
    return best;
  }

  public boolean haveLegalMove () {
    for (Coord coord : cards.keySet()) {
      if (isLegalMove(coord.above()) ||
          isLegalMove(coord.below()) ||
          isLegalMove(coord.left()) ||
          isLegalMove(coord.right())) return true;
    }
    return false;
  }

  public Set<Coord> legalMoves () {
    Set<Coord> moves = new HashSet<Coord>();
    for (Coord coord : cards.keySet()) {
      Coord up = coord.above(), down = coord.below();
      Coord left = coord.left(), right = coord.right();
      if (isLegalMove(up)) moves.add(up);
      if (isLegalMove(down)) moves.add(down);
      if (isLegalMove(left)) moves.add(left);
      if (isLegalMove(right)) moves.add(right);
    }
    return moves;
  }

  public Coord computeMove (Player player, Card card) {
    // score all possible moves and pick the best one; brute force!
    Coord bestCoord = null;
    int bestScore = 0;
    for (Coord coord : legalMoves()) {
      int score = 0;
      for (Hand hand : bestHands(card, coord)) score += hand.score;
      if (score >= bestScore) {
        bestCoord = coord;
        bestScore = score;
      }
    }
    // this is only called if there's at least one legal move, so bestCoord will != null
    return bestCoord;
  }

  protected Cons<Card> cards (Cons<Coord> coords, Card card, Coord coord) {
    if (coords == null) return null;
    return Cons.cons(coords.head == coord ? card : cards.get(coords.head),
                     cards(coords.tail, card, coord));
  }
}
