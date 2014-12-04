package polyglot.ide.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class AddJarSelectionListener extends AbstractLibrarySelectionListener {

  AddJarSelectionListener(Composite parent, TreeViewer treeViewer) {
    super(parent, treeViewer);
  }

  @Override
  protected void onSelect(SelectionEvent e) {
    ElementTreeSelectionDialog dialog =
        new ElementTreeSelectionDialog(parent.getShell(),
            new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
    dialog.setMessage("Choose the archives to be added to the build path:");
    dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
    dialog.setTitle("JAR Selection");
    dialog.open();

    if (dialog.getResult() == null) return;

    List<LibraryResource> items =
        (List<LibraryResource>) treeViewer.getTree().getData();
    if (items == null) items = new ArrayList<>();

    for (Object o : dialog.getResult()) {
      IFile file = (IFile) o;
      String name =
          file.getProject().getName() + File.separator
          + file.getProjectRelativePath().toString();
      LibraryResource newItem = new LibraryResource(name);
      if (!items.contains(newItem)) items.add(newItem);
    }

    treeViewer.setInput(items);
  }
}
