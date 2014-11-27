package polyglot.ide.common;

public class ClasspathEntry {

	public enum ClasspathEntryKind {
		SRC, CON, LIB, OUTPUT;
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
		System.out.println("");
		this.sourcePath = sourcePath;
	}

	@Override
	public String toString() {
		return "ClasspathEntry [kind=" + kind + ", path=" + path
				+ ", sourcePath=" + sourcePath + "]";
	}
}
