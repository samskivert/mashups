//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core.zone;

/** Enumerates the differet types of terrain. */
public enum Terrain {

  VOID   (false, 0xFF000000),
  RIVER  (false, 0xFF1899C4),
  OCEAN  (false, 0xFF0640BD),
  GRASS  (true,  0xFF19BA04),
  DIRT   (true,  0xFFBA7704),
  STONE  (true,  0xFF4C5453),
  BRIDGE (true,  0xFFDEC559);

  /** Whether this terrain can be walked upon. */
  public final boolean passable;

  /** A color to use when rendering this terrain. */
  public final int color;

  Terrain (boolean passable, int color) {
    this.passable = passable;
    this.color = color;
  }
}
