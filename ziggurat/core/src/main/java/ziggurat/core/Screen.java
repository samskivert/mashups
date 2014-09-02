//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core;

import playn.core.GroupLayer;
import playn.core.PlayN;
import react.Value;
import rsp.State;

public abstract class Screen {

  public final GroupLayer layer = PlayN.graphics().createGroupLayer();
  public final State root, state;
  public final Value<Boolean> added = Value.create(false);

  public void add () {
    PlayN.graphics().rootLayer().add(layer);
    added.update(true);
  }

  public void remove () {
    added.update(false);
    layer.destroy();
    state.reset();
  }

  protected Screen (State root, String id) {
    this.root = root;
    this.state = root.child(id);
  }
}
