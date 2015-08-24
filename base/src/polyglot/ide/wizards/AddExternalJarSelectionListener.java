package polyglot.ide.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

public class AddExternalJarSelectionListener
    extends AbstractLibrarySelectionListener {

  AddExternalJarSelectionListener(Composite parent, TreeViewer treeViewer) {
    super(parent, treeViewer);
  }

  @Override
  protected void onSelect(SelectionEvent e) {
    FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.MULTI);
    fileDialog.setText("JAR Selection");
    fileDialog.setFilterExtensions(new String[] { "*.jar" });
    String fullFilePath = fileDialog.open();

    if (fullFilePath == null) return;

    String baseDir =
        fullFilePath.substring(0, fullFilePath.lastIndexOf(File.separatorChar));

    @SuppressWarnings("unchecked")
    List<LibraryResource> items =
        (List<LibraryResource>) treeViewer.getTree().getData();
    if (items == null) items = new ArrayList<>();

    for (String fileName : fileDialog.getFileNames()) {
      String name = baseDir + File.separatorChar + fileName;
      LibraryResource newItem = new LibraryResource(name);
      if (!items.contains(newItem)) items.add(newItem);
    }

    treeViewer.setInput(items);
  }
}
