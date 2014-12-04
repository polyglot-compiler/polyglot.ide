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

  private ClasspathEntryKind kind;
  private String path;
  private String sourcePath;

  public ClasspathEntry(ClasspathEntryKind kind, String path) {
    this.kind = kind;
    this.path = path;
  }

  public ClasspathEntryKind getKind() {
    return kind;
  }

  public void setKind(ClasspathEntryKind kind) {
    this.kind = kind;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getSourcePath() {
    return sourcePath;
  }

  public void setSourcePath(String sourcePath) {
    this.sourcePath = sourcePath;
  }

  @Override
  public String toString() {
    return "ClasspathEntry [kind=" + kind + ", path=" + path + ", sourcePath="
        + sourcePath + "]";
  }
}
