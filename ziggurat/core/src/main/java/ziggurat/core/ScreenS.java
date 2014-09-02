//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core;

import rsp.State;

/** Defines the state needed to create a screen. */
public abstract class ScreenS<C> {

  /** Creates the screen associated with this state. */
  public abstract Screen create (C ctx);
}
