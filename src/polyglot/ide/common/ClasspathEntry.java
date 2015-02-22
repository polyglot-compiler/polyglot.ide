package polyglot.ide.common;

import java.util.HashMap;
import java.util.Map;

public class ClasspathEntry {

  public enum ClasspathEntryKind {
    SRC, CON, LIB, OUTPUT;

    static Map<String, ClasspathEntryKind> map = buildMap();

    private static Map<String, ClasspathEntryKind> buildMap() {
      map = new HashMap<>();

      for (ClasspathEntryKind kind : ClasspathEntryKind.values())
        map.put(kind.name().toLowerCase(), kind);

      return map;
    }
  }

  public enum ClasspathEntryType {
    CLASSPATHENTRY, SIGPATHENTRY
  }

  private ClasspathEntryKind kind;
  private String path;
  private String sourcePath;
  private ClasspathEntryType classpathEntryType;

  public ClasspathEntry(ClasspathEntryKind kind, String path) {
    this.kind = kind;
    this.path = path;
    this.classpathEntryType = ClasspathEntryType.CLASSPATHENTRY;
  }

  public ClasspathEntry(ClasspathEntryKind kind, String path,
      ClasspathEntryType classpathEntryType) {
    this.kind = kind;
    this.path = path;
    this.classpathEntryType = classpathEntryType;
  }

  public ClasspathEntryKind getKind() {
    return kind;
  }

  public String getPath() {
    return path;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public ClasspathEntryType getClasspathEntryType() {
    return classpathEntryType;
  }

  @Override
  public String toString() {
    return "ClasspathEntry [kind=" + kind + ", path=" + path + ", sourcePath="
        + sourcePath + "]";
  }
}
