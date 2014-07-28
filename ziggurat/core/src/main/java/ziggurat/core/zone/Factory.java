//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

import static ziggurat.core.zone.Unit.Attr.*;

/** Creates units. */
public class Factory {

  //                                            attack  defend  move    range
  public static Factory BALANCED  = new Factory(NORMAL, NORMAL, NORMAL, NORMAL);
  public static Factory MUP_DDOWN = new Factory(NORMAL, WEAK,   STRONG, NORMAL);
  public static Factory MUP_ADOWN = new Factory(WEAK,   NORMAL, STRONG, NORMAL);
  public static Factory AUP_DDOWN = new Factory(STRONG, WEAK,   NORMAL, NORMAL);
  public static Factory AUP_MDOWN = new Factory(STRONG, NORMAL, WEAK,   NORMAL);
  public static Factory DUP_MDOWN = new Factory(NORMAL, STRONG, WEAK,   NORMAL);
  public static Factory RUP_DDOWN = new Factory(NORMAL, WEAK,   NORMAL, STRONG);
  public static Factory RUP_MDOWN = new Factory(NORMAL, NORMAL, WEAK,   STRONG);

  public Unit create () {
    return new Unit(_attack, _defend, _move, _range);
  }

  private Factory (Unit.Attr attack, Unit.Attr defend, Unit.Attr move, Unit.Attr range) {
    _attack = attack;
    _defend = defend;
    _move = _move;
    _range = range;
  }

  private Unit.Attr _attack, _defend, _move, _range;
}
