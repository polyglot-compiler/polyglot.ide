package polyglot.ide.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;

import polyglot.ide.JLPluginInfo;
import polyglot.ide.PluginInfo;

public class JLConfigureBuildPathCommandHandler extends
    AbstractConfigureBuildpathCommandHandler {

  /**
   * A hook for Eclipse to instantiate this class.
   */
  public JLConfigureBuildPathCommandHandler() {
    this(JLPluginInfo.INSTANCE);
  }

  /**
   * A hook for extensions to instantiate this class.
   */
  public JLConfigureBuildPathCommandHandler(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

  @Override
  protected Wizard getWizard(IProject project) {
    return new JLConfigureBuildPathWizard(pluginInfo, project);
  }
}
