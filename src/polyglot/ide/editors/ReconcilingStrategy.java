package polyglot.ide.editors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;

import polyglot.ast.SourceFile;
import polyglot.frontend.Compiler;
import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.ide.PluginInfo;
import polyglot.ide.common.ErrorUtil;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;
import polyglot.main.Options;
import polyglot.main.UsageError;
import polyglot.util.SilentErrorQueue;

/**
 * Hooks the parser into the UI. The reconcile methods are called whenever the
 * document is modified. Hooked in by
 * {@link SourceViewerConfiguration#getReconciler(ISourceViewer)}.
 */
public class ReconcilingStrategy implements IReconcilingStrategy {
  protected final PluginInfo pluginInfo;
  protected final Editor editor;
  protected IDocument document;

  private static Map<String, SourceFile> outputMap = new HashMap<>();

  public ReconcilingStrategy(Editor editor) {
    this.pluginInfo = editor.pluginInfo();
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

  protected void setupCompilerOptions(ExtensionInfo extInfo) {
    IProject project = editor.getFile().getProject();
    String[] compilerArgs = pluginInfo
        .compilerArgs(true, project, Collections.singletonList("/dev/null"))
        .toArray(new String[0]);

    try {
      // TODO Need a better way of setting up these options.
      Options options = extInfo.getOptions();
      Options.global = options;
      options.parseCommandLine(compilerArgs, new HashSet<String>());
    } catch (UsageError e) {
      ErrorUtil.handleError(pluginInfo, Level.ERROR, "Compiler error",
          "An error occurred while configuring the compiler.", e, Style.LOG);
    }
  }

  /**
   * Runs all compiler passes up until the translation/serialization/output
   * passes.
   */
  protected void validate() {
    // Set up Polyglot.
    // We use a fresh ExtensionInfo each time, because the goals in the old
    // ExtensionInfo will have stale state that will cause passes to not be
    // run.

    IProject project = editor.getFile().getProject();
    if (project == null || !project.isAccessible() || !checkNature(project))
      return;

    ExtensionInfo extInfo = editor.makeExtInfo();
    SilentErrorQueue eq = new SilentErrorQueue(100, "parser");
    setupCompilerOptions(extInfo);
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
      public Reader openReader(boolean ignoreEncodingErrors)
          throws IOException {
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
      if (success) addToOutputMap(compiler.jobs());
    } catch (Throwable t) {
      ErrorUtil.handleError(pluginInfo, Level.ERROR, "Compiler error",
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
        severity = ErrorUtil.toLevel(
            ((CoreException) e).getStatus().getSeverity(), Level.ERROR);
      }

      ErrorUtil.handleError(pluginInfo, severity,
          "Error updating problem markers", e.getMessage(), e, Style.SHOW);
    }
  }

  protected boolean checkNature(IProject project) {
    try {
      return Arrays.asList(project.getDescription().getNatureIds())
          .contains(pluginInfo.natureID());
    } catch (CoreException e) {
      e.printStackTrace();
      return false;
    }
  }

  protected void addToOutputMap(List<Job> jobs) {
    if (jobs != null) {
      for (Job job : jobs) {
        SourceFile sourceFile = (SourceFile) job.ast();
        if (sourceFile != null)
          outputMap.put(sourceFile.position().path(), sourceFile);
      }
    }
  }

  public static SourceFile getAST(String filename) {
    return outputMap.get(filename);
  }
}
