package polyglot.ide.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class AddClassFolderSelectionListener extends
AbstractLibrarySelectionListener {

  AddClassFolderSelectionListener(Composite parent, TreeViewer treeViewer) {
    super(parent, treeViewer);
  }

  @Override
  protected void onSelect(SelectionEvent e) {
    CheckedTreeSelectionDialog dialog =
        new CheckedTreeSelectionDialog(parent.getShell(),
            new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
    dialog.setTitle("Class Folder Selection");
    dialog.setMessage("Choose class folders to be added to the build path:");
    dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
    dialog.open();

    if (dialog.getResult() == null) return;

    List<LibraryResource> items =
        (List<LibraryResource>) treeViewer.getTree().getData();
    if (items == null) items = new ArrayList<>();

    for (Object o : dialog.getResult()) {
      IFolder folder = ((IFolder) o);
      String name =
          folder.getProject().getName() + File.separator
          + folder.getProjectRelativePath().toString();
      LibraryResource newItem = new LibraryResource(name);
      if (!items.contains(newItem)) items.add(newItem);
    }

    treeViewer.setInput(items);
  }
}
