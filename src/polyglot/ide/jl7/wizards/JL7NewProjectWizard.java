package polyglot.ide.jl7.wizards;

import polyglot.ide.PluginInfo;
import polyglot.ide.jl7.JL7PluginInfo;
import polyglot.ide.jl5.wizards.JL5NewProjectWizard;

public class JL7NewProjectWizard extends JL5NewProjectWizard {

  public JL7NewProjectWizard() {
    this(JL7PluginInfo.INSTANCE);
  }

  public JL7NewProjectWizard(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
