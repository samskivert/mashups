//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

import com.google.common.base.Preconditions;
import react.Value;

/** Represents a playable unit.  */
public class Unit extends Actor {

  /** Defines attributes and adjustments. */
  public static enum Attr {
    //           damage vs.
    //     mv rg bo st nrm wk
    BONUS (4, 3, 4, 7, 10, 15),
    STRONG(3, 2, 2, 4,  7, 10),
    NORMAL(2, 1, 1, 2,  4,  7),
    WEAK  (1, 0, 0, 1,  2,  4);

    public final int move, range;
    public final int[] damage;

    Attr (int move, int range, int... damage) {
      this.move = move;
      this.range = range;
      this.damage = damage;
    }
  };

  /** This unit's attributes. */
  public final Attr attack, defend, move, range;

  /** This unit's damage vs a unit with the specified defense. */
  public int damage (Attr defend) { return attack.damage[defend.ordinal()]; }

  /** This unit's move value. */
  public int move () { return move.move; }

  /** This unit's range value. */
  public int range () { return range.range; }

  /** The maximum health for a unit. */ // TODO: make attribute?
  public static final int MAX_HEALTH = 10;

  /** This unit's current health. */
  public final Value<Integer> health = Value.create(MAX_HEALTH);

  public Unit (Attr attack, Attr defend, Attr move, Attr range) {
    this.attack = Preconditions.checkNotNull(attack);
    this.defend = Preconditions.checkNotNull(defend);
    this.move   = Preconditions.checkNotNull(move);
    this.range  = Preconditions.checkNotNull(range);
  }
}
