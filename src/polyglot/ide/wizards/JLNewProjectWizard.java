/*
 * Some of this code was cribbed from BasicNewProjectResourceWizard. Good
 * practice says we should subclass, but BasicNewProjectResourceWizard is not
 * intended to be subclassed.
 */
package polyglot.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import polyglot.ide.JLPluginInfo;
import polyglot.ide.PluginInfo;
import polyglot.ide.common.BuildpathEntry;

public class JLNewProjectWizard extends AbstractNewProjectWizard {

  protected JLNewProjectWizardPageTwo pageTwo;

  /**
   * A hook for Eclipse to instantiate this class.
   */
  public JLNewProjectWizard() {
    this(JLPluginInfo.INSTANCE);
  }

  /**
   * A hook for extensions to instantiate this class.
   */
  public JLNewProjectWizard(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

  @Override
  protected void addExtraPages() {
    pageTwo =
        new JLNewProjectWizardPageTwo(pluginInfo, "new"
            + pluginInfo.langShortName() + "ProjectPageTwo");
    pageTwo.setTitle(pluginInfo.langName() + " Settings");
    pageTwo.setDescription("Define the " + pluginInfo.langName()
        + " build settings.");

    addPage(pageTwo);
  }

  @Override
  protected List<BuildpathEntry> extraBuildpathEntries() {
    List<BuildpathEntry> result = new ArrayList<>();
    List<LibraryResource> libraryResourceList = pageTwo.getClasspathEntries();

    if (libraryResourceList != null) {
      for (LibraryResource libraryResource : libraryResourceList)
        result.add(new BuildpathEntry(BuildpathEntry.LIB, libraryResource
            .getName()));
    }

    return result;
  }
}
