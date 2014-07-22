//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.gensys;

import playn.core.Canvas;
import tripleplay.util.Randoms;

public abstract class Generator {

  public static class Info {
    public final StringBuilder buf = new StringBuilder();

    public void add (String label, Object value) {
      buf.append(label).append(": ").append(value).append("\n");
    }

    public void add (Gene gene, String label, Object value) {
      buf.append(gene.id()).append(" ").append(label).append(": ").append(value).append("\n");
    }

    public void addColor (Gene gene, String label, int color) {
      add(gene, label, Integer.toHexString(color & 0xFFFFFF).toUpperCase());
    }
  }

  // TODO: we may need to break this into individual iterations, in which case the generator will
  // probably retain state, etc. etc.
  public abstract void generate (Randoms rando, Gene[] genes, Canvas target, Info info);
}
