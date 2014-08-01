package pide.editors;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * A strategy for automatically adding closing quotes, parens, braces, etc. as
 * the user edits the document. Hooked in by
 * {@link SourceViewerConfiguration#getAutoEditStrategies(ISourceViewer, String)}.
 */
public class AutoEditStrategy implements IAutoEditStrategy {
  @Override
  public void customizeDocumentCommand(IDocument document,
      DocumentCommand command) {
    // TODO Make this smarter.
    int XXX;
    
    if (command.text.equals("\"")) {
      // Add a closing quote.
      command.text = "\"\"";
      configureCommand(command, 1);
      return;
    }
    
    if (command.text.equals("'")) {
      // Add a closing quote.
      command.text = "''";
      configureCommand(command, 1);
      return;
    }
    
    if (command.text.equals("(")) {
      // Add a closing paren.
      command.text = "()";
      configureCommand(command, 1);
      return;
    }
    
    if (command.text.equals("{")) {
      // Add a closing brace.
      command.text = "{}";
      configureCommand(command, 1);
      return;
    }
  }
    
  /**
   * Adjusts the caret position.
   */
  private void configureCommand(DocumentCommand command, int caratOffsetDelta) {
    command.caretOffset = command.offset + caratOffsetDelta;
    command.shiftsCaret = false;
  }

}
