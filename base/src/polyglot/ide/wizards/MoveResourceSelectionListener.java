package polyglot.ide.wizards;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

public class MoveResourceSelectionListener
    extends AbstractLibrarySelectionListener {

  private MoveDirection moveDirection;

  enum MoveDirection {
    UP, DOWN;
  }

  MoveResourceSelectionListener(Composite parent, TreeViewer treeViewer,
      MoveDirection moveDirection) {
    super(parent, treeViewer);
    this.moveDirection = moveDirection;
  }

  @Override
  protected void onSelect(SelectionEvent e) {
    @SuppressWarnings("unchecked")
    List<String> allItems = (List<String>) treeViewer.getTree().getData();

    StructuredSelection structuredSelection =
        (StructuredSelection) treeViewer.getSelection();
    List<LibraryResource> selectedItems = structuredSelection.toList();

    if (allItems == null || selectedItems == null || selectedItems.size() != 1)
      return;

    int i = allItems.indexOf(selectedItems.get(0));
    int j = -1;

    if (moveDirection.equals(MoveDirection.UP) && i != 0)
      j = i - 1;
    else
      if (moveDirection.equals(MoveDirection.DOWN) && i != allItems.size() - 1)
        j = i + 1;

    if (j != -1) {
      Collections.swap(allItems, i, j);
      treeViewer.setInput(allItems);
    }
  }
}
