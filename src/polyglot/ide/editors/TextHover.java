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
  JLHyperlink hyperlink;

  @Override
  public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
    return (hyperlink != null) ? PolyglotASTUtil.getJavadoc(hyperlink
        .getPosition()) : null;
  }

  @Override
  public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
    hyperlink = PolyglotASTUtil.getHyperlink(textViewer, offset);
    return hyperlink != null ? hyperlink.getHyperlinkRegion() : null;
  }
}
