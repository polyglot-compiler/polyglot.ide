package polyglot.jl7.ide.wizards;

import polyglot.ide.PluginInfo;
import polyglot.jl5.ide.wizards.JL5NewProjectWizard;
import polyglot.jl7.ide.JL7PluginInfo;

public class JL7NewProjectWizard extends JL5NewProjectWizard {

  public JL7NewProjectWizard() {
    this(JL7PluginInfo.INSTANCE);
  }

  public JL7NewProjectWizard(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
