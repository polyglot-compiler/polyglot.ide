package polyglot.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.Wizard;

import polyglot.ide.PluginInfo;
import polyglot.ide.common.BuildpathEntry;
import polyglot.ide.common.BuildpathUtil;
import polyglot.ide.common.ErrorUtil;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

public class JLConfigureBuildPathWizard extends Wizard {
  protected final PluginInfo pluginInfo;
  protected IProject project;
  protected JLNewProjectWizardPageTwo buildConfigurationPage;

  JLConfigureBuildPathWizard(PluginInfo pluginInfo, IProject project) {
    this.pluginInfo = pluginInfo;
    this.project = project;
  }

  @Override
  public void addPages() {
    buildConfigurationPage =
        new JLNewProjectWizardPageTwo(pluginInfo, "buildConfigWizardPage",
            project);
    buildConfigurationPage.setTitle(pluginInfo.langName() + " Settings");
    buildConfigurationPage.setDescription("Define the " + pluginInfo.langName()
        + " build settings.");
    addPage(buildConfigurationPage);
  }

  @Override
  public boolean performFinish() {
    updateClasspathFile();
    try {
      project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
      project.build(IncrementalProjectBuilder.FULL_BUILD,
          new NullProgressMonitor());

      return true;
    } catch (CoreException e) {
      return false;
    }
  }

  private boolean updateClasspathFile() {
    List<BuildpathEntry> classpathEntries = new ArrayList<>();
    classpathEntries.add(new BuildpathEntry(BuildpathEntry.SRC, "src"));

    List<LibraryResource> libraryResourceList =
        buildConfigurationPage.getClasspathEntries();
    if (libraryResourceList != null)
      for (LibraryResource libraryResource : libraryResourceList)
        classpathEntries.add(new BuildpathEntry(BuildpathEntry.LIB,
            libraryResource.getName()));

    classpathEntries.add(new BuildpathEntry(BuildpathEntry.OUTPUT, "bin"));

    try {
      BuildpathUtil.createBuildpathFile(project, classpathEntries);
      return true;
    } catch (Exception e) {
      ErrorUtil.handleError(pluginInfo, Level.WARNING,
          "Error updating .buildpath file. Please check file permissions",
          e.getCause(), Style.BLOCK);
      return false;
    }
  }
}
