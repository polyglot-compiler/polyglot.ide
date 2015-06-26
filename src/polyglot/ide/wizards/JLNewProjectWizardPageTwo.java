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

import polyglot.ide.PluginInfo;
import polyglot.ide.common.BuildpathEntry;
import polyglot.ide.common.BuildpathUtil;

public class JLNewProjectWizardPageTwo extends NewElementWizardPage {
  protected final PluginInfo pluginInfo;
  protected LibrarySelector classpathSelector;
  protected IProject project;

  public JLNewProjectWizardPageTwo(PluginInfo pluginInfo, String name) {
    this(pluginInfo, name, null);
  }

  public JLNewProjectWizardPageTwo(PluginInfo pluginInfo, String name,
      IProject project) {
    super(name);
    this.pluginInfo = pluginInfo;
    this.project = project;
    setTitle(pluginInfo.langName() + " Settings");
    setDescription("Define the " + pluginInfo.langName() + " build settings.");
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

    classpathSelector = new LibrarySelector(tabFolder);
    if (project != null) classpathSelector.setItems(extractClasspathEntries());

    TabItem item = new TabItem(tabFolder, SWT.NONE);
    item.setText("&Classpath");
    item.setControl(classpathSelector);

    addExtraBuildPathTabs(tabFolder);

    Dialog.applyDialogFont(composite);
    setControl(composite);
  }

  protected void addExtraBuildPathTabs(TabFolder tabFolder) {
  }

  protected List<LibraryResource> extractClasspathEntries() {
    File classpathFile =
        project.getFile(BuildpathUtil.BUILDPATH_FILE_NAME).getRawLocation()
            .toFile();
    List<BuildpathEntry> entries =
        BuildpathUtil.getClasspathEntries(pluginInfo, classpathFile);
    List<LibraryResource> items = new ArrayList<>();

    for (BuildpathEntry entry : entries)
      items.add(new LibraryResource(entry.getPath()));

    return items;
  }

  /**
   * Converts the given list of resources into {@link BuildpathEntry} objects
   * that are appended to the given list of entries.
   *
   * @param kind
   *          the kind of entry to add.
   * @param type
   *          the type of entry to add.
   * @param resources
   *          the resources to convert.
   * @param entries
   *          an existing list of {@link BuildpathEntry} objects, to which the
   *          converted resources will be appended.
   * @return {@code entries}.
   */
  protected List<BuildpathEntry> addBuildpathEntries(BuildpathEntry.Kind kind,
      BuildpathEntry.Type type, List<LibraryResource> resources,
      List<BuildpathEntry> entries) {
    if (resources == null) return entries;

    for (LibraryResource resource : resources) {
      entries.add(new BuildpathEntry(kind, type, resource.getName()));
    }

    return entries;
  }

  /**
   * @return a list of BuildpathEntry objects for the path that the user has
   *         configured on this page.
   */
  public List<BuildpathEntry> getBuildpathEntries() {
    return addBuildpathEntries(BuildpathEntry.CLASSPATH, BuildpathEntry.LIB,
        classpathSelector.getItems(), new ArrayList<>());
  }
}
