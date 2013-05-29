//
// Mashups - a series of mashed up game prototypes
// https://github.com/samskivert/mashups/blob/master/LICENSE

package pokeros.core;

public class Cons<T> {

  public static <T> Cons<T> root (T head) {
    return new Cons<T>(head, null);
  }

  public static <T> Cons<T> cons (T head, Cons<T> tail) {
    return new Cons<T>(head, tail);
  }

  public final T head;
  public final Cons<T> tail;

  public Cons<T> prepend (T head) {
    return new Cons<T>(head, this);
  }

  public T last () {
    return tail == null ? head : tail.last();
  }

  public int size () {
    int size = 0;
    for (Cons<T> c  = this; c != null; c = c.tail) size += 1;
    return size;
  }

  protected Cons (T head, Cons<T> next) {
    this.head = head;
    this.tail = next;
  }

  @Override public String toString () {
    StringBuilder buf = new StringBuilder("[");
    for (Cons<T> c = this; c != null; c = c.tail) {
      if (buf.length() > 1) buf.append(", ");
      buf.append(c.head);
    }
    return buf.append("]").toString();
  }
}
