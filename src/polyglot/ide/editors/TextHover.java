package polyglot.ide.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * Computes the information to be shown when the mouse cursor hovers over editor
 * text. This is hooked in by
 * {@link SourceViewerConfiguration#getTextHover(ISourceViewer, String)}.
 */
public class TextHover implements ITextHover {

  @Override
  public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
    // TODO Auto-generated method stub
    int XXX;
    return null;
  }

  @Override
  public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
    // TODO Auto-generated method stub
    int XXX;
    return null;
  }

}
