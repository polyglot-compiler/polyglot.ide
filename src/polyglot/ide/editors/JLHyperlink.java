/**
 *
 */
package polyglot.ide.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * @author karthik
 */
public class JLHyperlink implements IHyperlink {

  IRegion region;
  String text;

  public JLHyperlink(IRegion region, String text) {
    this.region = region;
    this.text = text;
  }

  @Override
  public IRegion getHyperlinkRegion() {
    return region;
  }

  @Override
  public String getTypeLabel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getHyperlinkText() {
    return text;
  }

  @Override
  public void open() {
    System.out.println("Navigating to hyperlink ...");
  }

}
