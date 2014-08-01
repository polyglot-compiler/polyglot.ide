package pide.editors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;

import pide.common.ErrorUtil;
import pide.common.ErrorUtil.Level;
import pide.common.ErrorUtil.Style;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.SilentErrorQueue;

/**
 * Hooks the parser into the UI. The reconcile methods are called whenever the
 * document is modified. Hooked in by
 * {@link SourceViewerConfiguration#getReconciler(ISourceViewer)}.
 */
public class ReconcilingStrategy implements IReconcilingStrategy {
  protected final Editor editor;
  protected IDocument document;

  public ReconcilingStrategy(Editor editor) {
    this.editor = editor;
  }

  @Override
  public void setDocument(IDocument document) {
    this.document = document;
    validate();
  }

  @Override
  public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
    validate();
  }

  @Override
  public void reconcile(IRegion partition) {
    validate();
  }

  /**
   * Runs all compiler passes up until the translation/serialization/output
   * passes.
   */
  protected void validate() {
    // Set up Polyglot.
    // We use a fresh ExtensionInfo each time, because the goals in the old
    // ExtensionInfo will have stale state that will cause passes to not be run.
    ExtensionInfo extInfo = editor.extInfo();
    SilentErrorQueue eq = new SilentErrorQueue(100, "parser");
    try {
      // TODO Need a better way of setting up these options.
      Options options = extInfo.getOptions();
      Options.global = options;
      options.parseCommandLine(new String[] { "-cp",
          "/home/jed/work/jif/rt-classes", "-sigcp",
          "/home/jed/work/jif/sig-classes", "-d", "/tmp", "/dev/null" },
          new HashSet<>());
    } catch (UsageError e) {
      ErrorUtil.handleError(Level.ERROR, "pide", "Compiler error",
          "An error occurred while configuring the compiler.", e, Style.LOG);
    }

    Compiler compiler = new Compiler(extInfo, eq);

    // Create a Source object out of the document's contents.
    Source source = new Source() {
      Source.Kind kind = Source.Kind.USER_SPECIFIED;
      final long lastModified = System.currentTimeMillis();

      @Override
      public URI toUri() {
        return editor.getFile().getLocationURI();
      }

      @Override
      public String getName() {
        return editor.getFile().getName();
      }

      @Override
      public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(document.get().getBytes("UTF-8"));
      }

      @Override
      public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return new StringReader(document.get());
      }

      @Override
      public CharSequence getCharContent(boolean ignoreEncodingErrors)
          throws IOException {
        return document.get();
      }

      @Override
      public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
      }

      @Override
      public long getLastModified() {
        return lastModified;
      }

      @Override
      public boolean delete() {
        throw new UnsupportedOperationException();
      }

      @Override
      public void setUserSpecified(boolean userSpecified) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean userSpecified() {
        return kind == Kind.USER_SPECIFIED;
      }

      @Override
      public boolean compilerGenerated() {
        return kind == Kind.COMPILER_GENERATED;
      }

      @Override
      public void setKind(Kind kind) {
        this.kind = kind;
      }

      @Override
      public Kind kind() {
        return kind;
      }

      @Override
      public String name() {
        return getName();
      }

      @Override
      public String path() {
        return editor.getFile().getLocation().toString();
      }
    };

    // Validate.
    boolean success;
    try {
      success = compiler.validate(Collections.singleton(source));
    } catch (Throwable t) {
      ErrorUtil.handleError(Level.ERROR, "pide", "Compiler error",
          "An internal compiler error occurred.", t, Style.LOG, Style.SHOW);
      return;
    }

    try {
      // Update problem markers.
      editor.clearProblemMarkers();
      if (success) return;
      editor.setProblemMarkers(eq);
    } catch (CoreException | BadLocationException e) {
      Level severity = Level.WARNING;
      if (e instanceof CoreException) {
        severity =
            ErrorUtil.toLevel(((CoreException) e).getStatus().getSeverity(),
                Level.ERROR);
      }

      ErrorUtil.handleError(severity, "pide", "Error updating problem markers",
          e.getMessage(), e, Style.SHOW);
    }
  }
}
