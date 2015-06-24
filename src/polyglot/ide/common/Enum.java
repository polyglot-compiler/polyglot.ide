package polyglot.ide.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import polyglot.util.Pair;

/**
 * An enumerated type. Enums are interned and can be compared with ==.
 */
public class Enum {
  /**
   * Enums are identified by their dynamic class and a name.
   */
  private static ConcurrentMap<Pair<Class<? extends Enum>, String>, Enum> cache =
      new ConcurrentHashMap<>();

  private String name;

  protected Enum(String name) {
    this.name = name;
  }

  public static <T extends Enum> T get(Class<T> enumClass, String name,
      Constructor<T> ctor) {
    Pair<Class<? extends Enum>, String> enumKey = new Pair<>(enumClass, name);

    @SuppressWarnings("unchecked")
    final T result = (T) cache.get(enumKey);
    if (result != null) return result;

    final T fresh = ctor.make();

    @SuppressWarnings("unchecked")
    final T existing = (T) cache.putIfAbsent(enumKey, fresh);
    return existing == null ? fresh : existing;
  }

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return name();
  }

  /**
   * A closure for constructing instances of Enum.
   */
  public static interface Constructor<T extends Enum> {
    /**
     * Creates an instance of T.
     */
    T make();
  }
}
