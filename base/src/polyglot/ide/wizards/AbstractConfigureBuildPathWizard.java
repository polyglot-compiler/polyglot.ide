package polyglot.ide.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import polyglot.ide.PluginInfo;

public abstract class AbstractConfigureBuildPathWizard extends
    AbstractBuildPathWizard {

  protected AbstractConfigureBuildPathWizard(PluginInfo pluginInfo,
      IProject project) {
    super(pluginInfo, project);
  }

  @Override
  public boolean performFinish() {
    if (!writeBuildpathFile()) return false;

    try {
      refreshAndBuild();
      return true;
    } catch (CoreException e) {
      return false;
    }
  }

  /**
   * Refreshes the project and initiates a build.
   *
   * @throws CoreException
   *           if the refresh or build fails.
   */
  protected void refreshAndBuild() throws CoreException {
    project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
    project.build(IncrementalProjectBuilder.FULL_BUILD,
        new NullProgressMonitor());
  }
}
