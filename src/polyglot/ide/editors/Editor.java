package polyglot.ide.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;

import polyglot.frontend.ExtensionInfo;
import polyglot.util.ErrorInfo;
import polyglot.util.SilentErrorQueue;

public interface Editor {
  /**
   * @return a new ExtensionInfo instance for the language associated with this
   *         editor.
   */
  ExtensionInfo extInfo();

  /**
   * @return the file associated with this editor.
   */
  IFile getFile();

  /**
   * Adds a marker to the editor, indicating the position and severity of a
   * problem.
   */
  void addProblemMarker(String message, Position pos, int severity)
      throws CoreException;

  /**
   * Removes all problem markers from the editor.
   */
  void clearProblemMarkers() throws CoreException;

  /**
   * Adds a problem marker for the given error.
   */
  void addProblemMarker(ErrorInfo error) throws CoreException,
      BadLocationException;

  /**
   * Adds problem markers for the errors on the given error queue.
   */
  void addProblemMarkers(SilentErrorQueue eq) throws CoreException,
      BadLocationException;

  /**
   * Replaces all problem markers in the editor with markers for the errors on
   * the given error queue.
   */
  void setProblemMarkers(SilentErrorQueue eq) throws CoreException,
      BadLocationException;
}
