package polyglot.ide.wizards;

import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

public abstract class LibrarySelectionListener implements SelectionListener {
  private Composite parent;
  private List<String> selectedItems;

  public LibrarySelectionListener(Composite parent) {
    this.parent = parent;
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    onSelect(e, parent);
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    onSelect(e, parent);
  }

  public List<String> getSelectedItems() {
    return selectedItems;
  }

  protected abstract void onSelect(SelectionEvent e, Composite parent);
}
