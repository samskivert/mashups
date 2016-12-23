//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core;

import playn.core.Platform;

import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Stylesheet;

public class UI {

  public static Stylesheet stylesheet (Platform plat) {
    return SimpleStyles.newSheet(plat.graphics());
  }
}
