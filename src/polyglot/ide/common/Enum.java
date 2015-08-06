package polyglot.ide.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import polyglot.util.Pair;

/**
 * An enumerated type. Enums are identified by their dynamic class and a name.
 * They are interned and can be compared with ==.
 */
public class Enum {
  /**
   * Enums are identified by their dynamic class and a name.
   */
  private static ConcurrentMap<Pair<Class<? extends Enum>, String>, Enum> cache =
      new ConcurrentHashMap<>();

  private final String name;

  protected Enum(String name) {
    this.name = name;
  }

  /**
   * Obtains the canonical representation for the given Enum. If no canonical
   * representation already exists, the given instance will be used as the
   * canonical representation.
   */
  public static <T extends Enum> T get(T fresh) {
    Pair<Class<? extends Enum>, String> enumKey =
        new Pair<Class<? extends Enum>, String>(fresh.getClass(), fresh.name());

    final T result = (T) cache.get(enumKey);
    if (result != null) return result;

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
}
