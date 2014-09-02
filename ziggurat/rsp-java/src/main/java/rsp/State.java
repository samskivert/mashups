//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package rsp;

import java.util.HashMap;
import java.util.Map;
import react.RList;
import react.RMap;
import react.RSet;
import react.Reactor;
import react.Value;

/** Manages a collection of reactive state. Reactive state is arranged into a tree where each node
  * of the tree is a {@code State} instance and manages a (flat) collection of related reactive
  * state. In general, a new node in the state tree is created and all reactive state elements in
  * that node are created immediately, but this is not a strict requirement.
  *
  * <p>State is not thread-safe and presently one must structure their application such that all
  * state manipulation takes place on the same thread. In the future, RSP may be merged with an
  * Actor-like mechanism which will allow each state node to be managed by a different (logical)
  * thread, though all state in a single node will remain bound to a single (logical) thread.</p>
  */
public class State {

  /** Returns a reactive value identified by {@code id}.
    * If no value exists, one will be created with default value {@code defval}. */
  public <T> Value<T> value (String id, T defval) {
    @SuppressWarnings("unchecked") Value<T> value = (Value<T>)_state.get(id);
    if (value == null) _state.put(id, value = Value.create(defval));
    return value;
  }

  /** Returns a reactive list identified by {@code id}.
    * If no set exists, an new empty list is created. */
  public <T> RList<T> list (String id) {
    @SuppressWarnings("unchecked") RList<T> value = (RList<T>)_state.get(id);
    if (value == null) _state.put(id, value = RList.create());
    return value;
  }

  /** Returns a reactive set identified by {@code id}.
    * If no set exists, an new empty set is created. */
  public <T> RSet<T> set (String id) {
    @SuppressWarnings("unchecked") RSet<T> value = (RSet<T>)_state.get(id);
    if (value == null) _state.put(id, value = RSet.create());
    return value;
  }

  /** Returns a reactive map identified by {@code id}.
    * If no map exists, an new empty map is created. */
  public <K,V> RMap<K,V> map (String id) {
    @SuppressWarnings("unchecked") RMap<K,V> value = (RMap<K,V>)_state.get(id);
    if (value == null) _state.put(id, value = RMap.create());
    return value;
  }

  /** Returns the child state identified by {@code id}. */
  public State child (String id) {
    State state = _children.get(id);
    if (state == null) _children.put(id, state = new State());
    return state;
  }

  /** Clears listeners from all reactive state in this node and its children. This is used when we
    * want to "reboot" the application and start it fresh from the current state. */
  public void reset () {
    for (State c : _children.values()) c.reset();
    // TODO: perhaps instead of resetting state we should copy the current state of everything and
    // start a "new" app on that cloned state; this would ensure that any deferred computations
    // don't come back and do weird things to the ostensibly rebooted app (e.g. if an HTTP request
    // is in flight and we reset the app before the response comes in, the request handler may turn
    // around and update the reset state which would be somewhat unexpected)
    for (Reactor r : _state.values()) r.clearConnections();
    // TODO: perhaps emit a signal so that client code which needs to do something special on reset
    // can hook into this process
  }

  /** Disposes this state node: its contents and children. This simply removes this state node from
    * its parent and allows it, and everything reachable from it, to be collected. This is only
    * legal on a child node, attempting to dispose the root node will throw an exception. */
  public void dispose () {
    if (_parent == null) throw new IllegalStateException("Cannot dispose the root state.");
    _parent._children.remove(_id); // TODO: warn on NOOP/repeated disposal?
  }

  public State () {
    this(null, null);
  }

  protected State (State parent, String id) {
    _parent = parent;
    _id = id;
  }

  private final State _parent;
  private final String _id;
  private final Map<String,Reactor> _state = new HashMap<String,Reactor>();
  private final Map<String,State> _children = new HashMap<String,State>();
}
