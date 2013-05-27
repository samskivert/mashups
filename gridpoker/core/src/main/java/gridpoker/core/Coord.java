//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

/** Represents a position at which a card may be played. */
public class Coord {

  public final int x, y;

  public Coord (int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Coord left  () { return new Coord(x-1, y); }
  public Coord right () { return new Coord(x+1, y); }
  public Coord above () { return new Coord(x, y-1); }
  public Coord below () { return new Coord(x, y+1); }

  @Override public int hashCode () {
    return x ^ y;
  }

  @Override public boolean equals (Object other) {
    return other instanceof Coord && ((Coord)other).x == x && ((Coord)other).y == y;
  }

  @Override public String toString () {
    return x + "/" + y;
  }
}
