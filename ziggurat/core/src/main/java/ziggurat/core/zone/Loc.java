//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

/** Represents a location in a zone. */
public final class Loc {

  /** The x coordinate of this location. */
  public final short x;
  /** The y coordinate of this location. */
  public final short y;

  public Loc (short x, short y) {
    this.x = x;
    this.y = y;
  }

  public Loc (int x, int y) {
    this((short)x, (short)y);
  }

  /** Returns a new loc offset by {@code dx, dy}. */
  public Loc delta (int dx, int dy) {
    return new Loc(x+dx, y+dy);
  }

  @Override public int hashCode () {
    return x ^ y;
  }

  @Override public boolean equals (Object other) {
    return (other instanceof Loc) && ((Loc)other).x == x && ((Loc)other).y == y;
  }
}
