/**
 *
 */
package polyglot.ide.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * @author karthik
 */
public class JLHyperlinkDetector extends AbstractHyperlinkDetector {

  @Override
  public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region,
      boolean canShowMultipleHyperlinks) {
    JLHyperlink hyperlink =
        PolyglotASTUtil.getHyperlink(textViewer, region.getOffset());
    return hyperlink != null ? new IHyperlink[] { hyperlink } : null;
  }
}
