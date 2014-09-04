package polyglot.ide.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import polyglot.util.ErrorInfo;
import polyglot.util.SilentErrorQueue;

/**
 * The controller in MVC lingo. This is hooked in by the configuration specified
 * in plugin.xml.
 */
public abstract class AbstractEditor extends TextEditor implements Editor {

  private ColorManager colorManager;

  public AbstractEditor() {
    // Hook in the document provider.
    setDocumentProvider(new DocumentProvider());

    // Hook in the source viewer configuration.
    colorManager = new ColorManager();
    setSourceViewerConfiguration(new SourceViewerConfiguration(this,
        colorManager));
  }

  @Override
  public void dispose() {
    colorManager.dispose();

    super.dispose();
  }

  @Override
  public IFileEditorInput getEditorInput() {
    return (IFileEditorInput) super.getEditorInput();
  }

  @Override
  public IFile getFile() {
    return getEditorInput().getFile();
  }

  @Override
  public void addProblemMarker(String message, Position pos, int severity)
      throws CoreException {
    IFile file = getFile();
    IMarker marker = file.createMarker(IMarker.PROBLEM);

    marker.setAttribute(IMarker.SEVERITY, severity);
    marker.setAttribute(IMarker.MESSAGE, message);
    marker.setAttribute(IMarker.CHAR_START, pos.offset);
    marker.setAttribute(IMarker.CHAR_END, pos.offset + pos.length);

    getSourceViewer().getAnnotationModel().addAnnotation(
        new MarkerAnnotation(marker), pos);
  }

  @Override
  public void clearProblemMarkers() throws CoreException {
    getFile().deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
  }

  @Override
  public void addProblemMarker(ErrorInfo error) throws CoreException,
      BadLocationException {
    addProblemMarker(
        error.getMessage(),
        PolyglotUtil.convert(getSourceViewer().getDocument(),
            error.getPosition()),
        PolyglotUtil.convertErrorKind(error.getErrorKind()));
  }

  @Override
  public void addProblemMarkers(SilentErrorQueue eq) throws CoreException,
      BadLocationException {
    for (ErrorInfo error : eq) {
      addProblemMarker(error);
    }
  }

  @Override
  public void setProblemMarkers(SilentErrorQueue eq) throws CoreException,
      BadLocationException {
    clearProblemMarkers();
    addProblemMarkers(eq);
  }
}
