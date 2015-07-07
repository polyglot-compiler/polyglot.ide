package polyglot.ide.wizards;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import polyglot.ide.wizards.MoveResourceSelectionListener.MoveDirection;

public class LibrarySelector extends Composite {
  private TreeViewer treeViewer;

  public LibrarySelector(Composite parent) {
    super(parent, SWT.NONE);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    setLayout(gridLayout);

    final Label label = new Label(this, SWT.NONE);
    label.setText("JARs and class folders on the build path:");
    label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));

    treeViewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    treeViewer.getControl()
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 8));
    treeViewer.setContentProvider(new TreeViewContentProvider());
    treeViewer.setLabelProvider(new TreeViewLabelProvider());

    Button addJar = new Button(this, SWT.PUSH);
    addJar.setText("Add JARs...");
    addJar
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    addJar.addSelectionListener(new AddJarSelectionListener(this, treeViewer));

    Button addExternalJar = new Button(this, SWT.PUSH);
    addExternalJar.setText("Add External JARs...");
    addExternalJar
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    addExternalJar.addSelectionListener(
        new AddExternalJarSelectionListener(this, treeViewer));

    Button addClassFolder = new Button(this, SWT.PUSH);
    addClassFolder.setText("Add Class Folder...");
    addClassFolder
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    addClassFolder.addSelectionListener(
        new AddClassFolderSelectionListener(this, treeViewer));

    Button addExternalClassFolder = new Button(this, SWT.PUSH);
    addExternalClassFolder.setText("Add External Class Folder...");
    addExternalClassFolder
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    addExternalClassFolder.addSelectionListener(
        new AddExternalClassFolderSelectionListener(this, treeViewer));

    Button removeButton = new Button(this, SWT.PUSH);
    removeButton.setText("Remove");
    removeButton
        .setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    removeButton.addSelectionListener(
        new RemoveResourceSelectionListener(this, treeViewer));

    Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
    separator
        .setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

    Button moveUpButton = new Button(this, SWT.PUSH);
    moveUpButton.setText("Move Up");
    moveUpButton
        .setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
    moveUpButton.addSelectionListener(new MoveResourceSelectionListener(parent,
        treeViewer, MoveDirection.UP));

    Button moveDownButton = new Button(this, SWT.PUSH);
    moveDownButton.setText("Move Down");
    moveDownButton
        .setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
    moveDownButton.addSelectionListener(new MoveResourceSelectionListener(
        parent, treeViewer, MoveDirection.DOWN));

  }

  public List<LibraryResource> getItems() {
    return ((TreeViewContentProvider) treeViewer.getContentProvider()).items;
  }

  public void setItems(List<LibraryResource> items) {
    treeViewer.setInput(items);
  }

  private class TreeViewContentProvider implements ITreeContentProvider {
    List<LibraryResource> items;

    @Override
    public void dispose() {
      // TODO Auto-generated method stub

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      @SuppressWarnings("unchecked")
      List<LibraryResource> items = (List<LibraryResource>) newInput;
      this.items = items;
    }

    @Override
    public Object[] getElements(Object inputElement) {
      return items.toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public Object getParent(Object element) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean hasChildren(Object element) {
      // TODO Auto-generated method stub
      return false;
    }
  }

  private class TreeViewLabelProvider extends LabelProvider {
    @Override
    public String getText(Object element) {
      return ((LibraryResource) element).getName();
    }
  }
}
