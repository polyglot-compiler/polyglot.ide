package pide.editors;

import static polyglot.util.Position.END_UNUSED;
import static polyglot.util.Position.UNKNOWN;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

import polyglot.util.ErrorInfo;

public class PolyglotUtil {
  /**
   * Converts a Polyglot position object to an Eclipse position object. The
   * given Polyglot position object {@code pos} is assumed to correspond to the
   * given IDocument {@code document}.
   * 
   * @throws BadLocationException
   *           if the given position information is invalid in the given
   *           document.
   */
  public static Position convert(IDocument document, polyglot.util.Position pos)
      throws BadLocationException {
    if (pos == null) return new Position(document.getLength()-1, 1);
    
    // Since line & col of a Polyglot Position seems to be more reliable
    // than its offset, use that to compute the offset information needed
    // to construct an Eclipse Position.
    int startLine = pos.line();
    int startCol = pos.column();
    int endLine = pos.endLine();
    int endCol = pos.endColumn();

    // Handle UNKNOWN values.
    if (startLine == UNKNOWN) startLine = 1;
    if (startCol == UNKNOWN) startCol = 1;

    // Calculate start offset.
    int offset = document.getLineOffset(startLine - 1) + startCol;

    int length = 1;
    if (endLine != END_UNUSED) {
      int endOffset = document.getLineOffset(endLine - 1) + endCol;
      length = endOffset - offset;
    }

    return new Position(offset, length);
  }

  /**
   * Converts a Polyglot error kind into an Eclipse IMarker severity level.
   */
  public static int convertErrorKind(int errorKind) {
    switch (errorKind) {
    case ErrorInfo.WARNING:
      return IMarker.SEVERITY_WARNING;
    case ErrorInfo.DEBUG:
      return IMarker.SEVERITY_INFO;
    default:
      return IMarker.SEVERITY_ERROR;
    }
  }
}
