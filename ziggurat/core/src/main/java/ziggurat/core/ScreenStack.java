//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package ziggurat.core;

import java.util.NoSuchElementException;
import playn.core.util.Clock;
import react.RList;
import rsp.State;

public class ScreenStack<C> {

  public ScreenStack (C ctx, State state) {
    this(ctx, state, "screens");
  }

  public ScreenStack (C ctx, State state, String id) {
    _ctx = ctx;
    _screens = state.list(id);

    // react to changes in the screen stack
    _screens.connect(new RList.Listener<ScreenS<C>>() {
      public void onAdd (int index, ScreenS<C> ss) {
        if (index == _screens.size()-1) addTop();
      }
      public void onRemove (int index, ScreenS<C> ss) {
        if (index == _screens.size()) {
          _showing.remove();
          _showing = null;
          addTop();
        }
      }
    });
    // if there's already a screen on top, add it
    addTop();
  }

  public boolean isEmpty () {
    return _screens.isEmpty();
  }

  /** Pushes a screen onto the stack, replacing the current screen therewith. */
  public void push (ScreenS<C> ss) {
    _screens.add(ss);
  }

  /** Pops the top screen from the stack. */
  public void pop () {
    if (_screens.isEmpty()) throw new NoSuchElementException("Cannot pop() empty stack.");
    remove(_screens.get(_screens.size()-1));
  }

  /** Removes the specified screen from the stack.
    * @return true if the screen was found and removed, false if not. */
  public boolean remove (ScreenS<C> ss) {
    for (int ii = _screens.size()-1; ii >= 0; ii--) {
      if (!_screens.get(ii).equals(ss)) continue;
      _screens.remove(ii);
      return true;
    }
    return false;
  }

  private void addTop () {
    assert _showing == null;
    if (!_screens.isEmpty()) {
      _showing = _screens.get(_screens.size()-1).create(_ctx);
      _showing.add();
    }
  }

  private final C _ctx;
  private final RList<ScreenS<C>> _screens;
  private Screen _showing;
}
