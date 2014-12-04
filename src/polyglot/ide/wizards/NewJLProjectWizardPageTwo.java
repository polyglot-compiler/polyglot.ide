package polyglot.ide.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import polyglot.ide.common.ClasspathEntry;
import polyglot.ide.common.ClasspathUtil;

public class NewJLProjectWizardPageTwo extends NewElementWizardPage {
  private LibrarySelector librarySelector;
  private IProject project;

  public NewJLProjectWizardPageTwo(String name) {
    super(name);
  }

  public NewJLProjectWizardPageTwo(String name, IProject project) {
    this(name);
    this.project = project;
  }

  @Override
  public void createControl(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());

    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    layout.numColumns = 1;
    composite.setLayout(layout);

    final TabFolder tabFolder = new TabFolder(composite, SWT.BORDER);
    tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
    tabFolder.setFont(composite.getFont());

    librarySelector = new LibrarySelector(tabFolder);
    if (project != null) librarySelector.setItems(getClasspathEntries());

    TabItem item = new TabItem(tabFolder, SWT.NONE);
    item.setText("&Libraries");
    item.setControl(librarySelector);

    Dialog.applyDialogFont(composite);
    setControl(composite);
  }

  private List<LibraryResource> getClasspathEntries() {
    File classpathFile =
        project.getFile(ClasspathUtil.CLASSPATH_FILE_NAME).getRawLocation()
        .toFile();
    List<ClasspathEntry> entries =
        ClasspathUtil.getClasspathEntries(classpathFile);
    List<LibraryResource> items = new ArrayList<>();

    for (ClasspathEntry entry : entries)
      items.add(new LibraryResource(entry.getPath()));

    return items;
  }

  public List<LibraryResource> getLibraries() {
    return librarySelector.getItems();
  }
}
