//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.gensys;

import playn.core.Color;
import tripleplay.util.Colors;
import tripleplay.util.Randoms;

/** Models the seven variations which can occur in each of the seven slots. */
public enum Gene {

  RED    (0xFFD10000),
  ORANGE (0xFFFF6622),
  YELLOW (0xFFFFDA21),
  GREEN  (0xFF33DD00),
  BLUE   (0xFF1133CC),
  INDIGO (0xFF220066),
  VIOLET (0xFF330044);

  /** The primary color for this gene. */
  public final int primary;

  /** The secondary color for this gene. */
  public final int secondary;

  public String id () { return name().substring(0, 1); }

  public boolean isDark () {
    return this.ordinal() >= BLUE.ordinal();
  }

  Gene (int primary) {
    this.primary = primary;
    this.secondary = invert(primary);
  }

  /** Generates a random array of genes of length {@code count}. */
  public static Gene[] random (Randoms rando, int count) {
    Gene[] vs = values(), genes = new Gene[count];
    for (int ii = 0; ii < count; ii++) genes[ii] = vs[rando.getInt(vs.length)];
    return genes;
  }

  private static int invert (int color) {
    return Color.rgb(255-Color.red(color), 255-Color.green(color), 255-Color.blue(color));
  }
}
