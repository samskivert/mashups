//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package gridpoker.core;

/** Represents a position at which a card may be played. */
public class Coord {

  public static Coord get (int x, int y) {
    int cy = y + MAX - 1;
    Coord[] row = _coords[cy];
    if (row == null) row = (_coords[cy] = new Coord[2*MAX-1]);
    int cx = x + MAX - 1;
    Coord coord = row[cx];
    if (coord == null) coord = (row[cx] = new Coord(x, y));
    return coord;
  }

  public static Cons<Coord> span (int start, int length, int cx, int cy, int dx, int dy) {
    int x = dx == 0 ? cx : start, y = dy == 0 ? cy : start;
    Cons<Coord> coords = Cons.root(Coord.get(x, y));
    while (--length > 0) {
      x += dx;
      y += dy;
      coords = coords.prepend(Coord.get(x, y));
    }
    return coords;
  }

  public final int x, y;

  public Coord near (int dx, int dy) { return get(x+dx, y+dy); }

  public Coord left  () { return near(-1,  0); }
  public Coord right () { return near( 1,  0); }
  public Coord above () { return near( 0, -1); }
  public Coord below () { return near( 0,  1); }

  @Override public int hashCode () {
    return x ^ y;
  }

  @Override public boolean equals (Object other) {
    return other == this;
  }

  @Override public String toString () {
    return x + "/" + y;
  }

  private Coord (int x, int y) {
    this.x = x;
    this.y = y;
  }

  protected static final int MAX = 52;
  protected static Coord[][] _coords = new Coord[2*MAX-1][];
}
