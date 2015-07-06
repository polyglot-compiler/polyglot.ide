package polyglot.jl5.ide.wizards;

import polyglot.ide.PluginInfo;
import polyglot.ide.wizards.JLNewProjectWizard;
import polyglot.jl5.ide.JL5PluginInfo;

public class JL5NewProjectWizard extends JLNewProjectWizard {

  public JL5NewProjectWizard() {
    this(JL5PluginInfo.INSTANCE);
  }

  public JL5NewProjectWizard(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
