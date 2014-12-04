package polyglot.ide.wizards;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractLibrarySelectionListener implements
SelectionListener {

  protected Composite parent;
  TreeViewer treeViewer;

  AbstractLibrarySelectionListener(Composite parent, TreeViewer treeViewer) {
    this.parent = parent;
    this.treeViewer = treeViewer;
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    onSelect(e);
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    onSelect(e);
  }

  protected abstract void onSelect(SelectionEvent e);
}
