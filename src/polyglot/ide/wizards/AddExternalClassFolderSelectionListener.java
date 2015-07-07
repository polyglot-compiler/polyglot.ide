package polyglot.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

public class AddExternalClassFolderSelectionListener
    extends AbstractLibrarySelectionListener {

  AddExternalClassFolderSelectionListener(Composite parent,
      TreeViewer treeViewer) {
    super(parent, treeViewer);
  }

  @Override
  protected void onSelect(SelectionEvent e) {
    DirectoryDialog directoryDialog = new DirectoryDialog(parent.getShell());
    directoryDialog.setText("External Class Folder Selection");

    String folderPath = directoryDialog.open();
    if (folderPath == null) return;

    LibraryResource newItem = new LibraryResource(folderPath);
    @SuppressWarnings("unchecked")
    List<LibraryResource> items =
        (List<LibraryResource>) treeViewer.getTree().getData();
    if (items == null) items = new ArrayList<>();

    if (!items.contains(newItem)) {
      items.add(newItem);
      treeViewer.setInput(items);
    }
  }
}
