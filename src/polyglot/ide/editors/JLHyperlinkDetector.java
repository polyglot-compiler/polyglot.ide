/**
 *
 */
package polyglot.ide.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
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

    IDocument document = textViewer.getDocument();
    String text = "";

    try {
      int offset = region.getOffset();
      IRegion lineInfo = document.getLineInformationOfOffset(offset);
      String line = document.get(offset, lineInfo.getLength());

      text = document.get(region.getOffset(), region.getLength());
      System.out.println(text);

    } catch (BadLocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return new IHyperlink[] { new JLHyperlink(region, text) };
  }

}
