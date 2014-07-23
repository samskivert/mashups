//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

/** Methods and constants relating to the game grid. */
public class Grid {

  /** The size of a grid element, in pixels. */
  public final static int SIZE = 45;

  /** Returns the grid position for the specified screen coordinate. */
  public static int toGrid (float x) {
    return (int)x/SIZE;
  }

  /** Returns the screen coordinate for the specified grid position. */
  public static float toScreen (int gx) {
    return gx * SIZE;
  }
}
