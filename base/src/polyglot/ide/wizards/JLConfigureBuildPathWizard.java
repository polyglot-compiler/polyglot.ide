package polyglot.ide.wizards;

import java.util.List;

import org.eclipse.core.resources.IProject;

import polyglot.ide.PluginInfo;
import polyglot.ide.common.BuildpathEntry;

public class JLConfigureBuildPathWizard extends
AbstractConfigureBuildPathWizard {
  protected JLNewProjectWizardPageTwo buildConfigurationPage;

  protected JLConfigureBuildPathWizard(PluginInfo pluginInfo, IProject project) {
    super(pluginInfo, project);
  }

  @Override
  public void addPages() {
    buildConfigurationPage =
        new JLNewProjectWizardPageTwo(pluginInfo, "buildConfigWizardPage",
            project);
    addPage(buildConfigurationPage);
  }

  @Override
  protected List<BuildpathEntry> extraBuildpathEntries() {
    return buildConfigurationPage.getBuildpathEntries();
  }
}
