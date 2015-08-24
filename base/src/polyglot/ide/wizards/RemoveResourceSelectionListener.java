package polyglot.ide.wizards;

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

public class RemoveResourceSelectionListener extends
AbstractLibrarySelectionListener {

  RemoveResourceSelectionListener(Composite parent, TreeViewer treeViewer) {
    super(parent, treeViewer);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void onSelect(SelectionEvent e) {
    List<String> allItems = (List<String>) treeViewer.getTree().getData();

    StructuredSelection structuredSelection =
        (StructuredSelection) treeViewer.getSelection();
    List<LibraryResource> selectedItems = structuredSelection.toList();

    if (allItems == null || selectedItems == null) return;

    for (LibraryResource selectedItem : selectedItems) {
      allItems.remove(selectedItem);
    }

    treeViewer.setInput(allItems);
  }
}
