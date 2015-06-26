package polyglot.ide.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;

import polyglot.ide.JLPluginInfo;
import polyglot.ide.PluginInfo;

public class ConfigureJLBuildPathCommandHandler extends
    AbstractConfigureBuildpathCommandHandler {

  /**
   * A hook for Eclipse to instantiate this class.
   */
  public ConfigureJLBuildPathCommandHandler() {
    this(JLPluginInfo.INSTANCE);
  }

  /**
   * A hook for extensions to instantiate this class.
   */
  public ConfigureJLBuildPathCommandHandler(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

  @Override
  protected Wizard getWizard(IProject project) {
    return new ConfigureJLBuildPathWizard(pluginInfo, project);
  }
}
