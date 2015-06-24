package polyglot.ide.common;

import polyglot.ide.AbstractPlugin;
import polyglot.ide.JLPlugin;

public class BuildpathEntry {

  /**
   * Identifies the type of build-path entry (e.g., a source directory, a
   * library, an output directory).
   */
  public static final class Type extends Enum {
    private static String typeName(String pluginClassName, String shortName) {
      return pluginClassName + ":" + shortName;
    }

    public static Type get(Class<? extends AbstractPlugin> pluginClass,
        String name) {
      return get(typeName(pluginClass.getName(), name));
    }

    /**
     * Obtains the canonical Type object corresponding to the given name. A new
     * Type object will be constructed if the canonical object doesn't already
     * exist.
     *
     * @param fqName
     *          the name of the Type, qualified by the class of the base plug-in
     *          for which the Type is defined.
     * @return the requested Type object.
     */
    public static Type get(final String fqName) {
      return Enum.get(new Type(fqName));
    }

    protected Type(String name) {
      super(name);
    }
  }

  /**
   * Designates an entry that identifies a source directory.
   */
  public static final Type SRC = Type.get(JLPlugin.class, "src");

  public static final Type CON = Type.get(JLPlugin.class, "con");

  /**
   * Designates an entry that identifies a library file (e.g., a JAR).
   */
  public static final Type LIB = Type.get(JLPlugin.class, "lib");

  /**
   * Designates an entry that identifies an output directory for
   * compiler-generated files.
   */
  public static final Type OUTPUT = Type.get(JLPlugin.class, "output");

  /**
   * Identifies kind of the build path the entry is for. Some languages have
   * more than one kind of build path.
   */
  public static final class Kind extends Enum {
    private static String typeName(String pluginClassName, String shortName) {
      return pluginClassName + ":" + shortName;
    }

    public static Kind get(Class<? extends AbstractPlugin> pluginClass,
        String name) {
      return get(typeName(pluginClass.getName(), name));
    }

    /**
     * Obtains the canonical Kind object corresponding to the given name. A new
     * Kind object will be constructed if the canonical object doesn't already
     * exist.
     *
     * @param fqName
     *          the name of the Kind, qualified by the class of the base plug-in
     *          for which the Kind is defined.
     * @return the requested Kind object.
     */
    public static Kind get(final String fqName) {
      return Enum.get(new Kind(fqName));
    }

    protected Kind(String name) {
      super(name);
    }
  }

  public static final Kind CLASSPATH = Kind.get(JLPlugin.class, "classpath");

  private Kind kind;
  private Type type;
  private String path;
  private String sourcePath;

  public BuildpathEntry(Type type, String path) {
    this(CLASSPATH, type, path);
  }

  public BuildpathEntry(Kind kind, Type type, String path) {
    this.kind = kind;
    this.type = type;
    this.path = path;
  }

  public Kind getKind() {
    return kind;
  }

  public Type getType() {
    return type;
  }

  public String getPath() {
    return path;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  @Override
  public String toString() {
    return "ClasspathEntry [kind=" + kind + "type=" + type + ", path=" + path
        + ", sourcePath=" + sourcePath + "]";
  }
}
