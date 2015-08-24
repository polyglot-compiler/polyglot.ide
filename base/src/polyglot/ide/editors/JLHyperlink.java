/**
 *
 */
package polyglot.ide.editors;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import polyglot.util.Position;

/**
 * @author karthik
 */
public class JLHyperlink implements IHyperlink {

  private String text;
  private Position position;
  private IRegion selectedRegion;

  public JLHyperlink(IRegion selectedRegion, String text, Position position) {
    this.selectedRegion = selectedRegion;
    this.text = text;
    this.position = position;
  }

  @Override
  public IRegion getHyperlinkRegion() {
    return selectedRegion;
  }

  @Override
  public String getTypeLabel() {
    return text;
  }

  @Override
  public String getHyperlinkText() {
    return text;
  }

  public Position getPosition() {
    return position;
  }

  @Override
  public void open() {
    if (position == null || position.path() == null) return;

    File fileToOpen = new File(position.path());

    if (fileToOpen.exists() && fileToOpen.isFile()) {
      IFileStore fileStore =
          EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
      IWorkbenchPage page =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

      try {
        IDE.openEditorOnFileStore(page, fileStore);

        JLEditor editor =
            (JLEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().getActiveEditor();

        editor.selectAndReveal(position.offset(), 0);

      } catch (PartInitException e) {
        e.printStackTrace();
      }
    }
  }
}
