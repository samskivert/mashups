//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core;

import rsp.State;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.layout.AxisLayout;

public class MainMenuScreen extends Screen {

  public static class S extends ScreenS<Ziggurat> {
    public Screen create (Ziggurat ctx) {
      return new MainMenuScreen(ctx);
    }
  }

  public MainMenuScreen (Ziggurat ctx) {
    super(ctx.root, "main");
  }

  // @Override public void wasShown () {
  //   super.wasShown();
  //   _root = iface.createRoot(AxisLayout.vertical(), UI.stylesheet(), layer);
  //   // _root.addStyles(Style.BACKGROUND.is(background()));
  //   _root.setSize(width(), height());
  //   // create your user interface
  //   _root.add(new Label("Ziggurat"));
  // }

  // // destroy your user interface in wasHidden (or wasRemoved)
  // @Override public void wasHidden () {
  //   super.wasHidden();
  //   iface.destroyRoot(_root);
  // }

  // protected Root _root;
}
