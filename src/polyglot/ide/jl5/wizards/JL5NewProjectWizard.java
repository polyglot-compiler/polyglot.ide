package polyglot.ide.jl5.wizards;

import polyglot.ide.PluginInfo;
import polyglot.ide.jl5.JL5PluginInfo;
import polyglot.ide.wizards.JLNewProjectWizard;

public class JL5NewProjectWizard extends JLNewProjectWizard {

  public JL5NewProjectWizard() {
    this(JL5PluginInfo.INSTANCE);
  }

  public JL5NewProjectWizard(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
