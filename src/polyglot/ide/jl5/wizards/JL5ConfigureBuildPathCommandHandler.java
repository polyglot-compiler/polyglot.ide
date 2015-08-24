package polyglot.ide.jl5.wizards;

import polyglot.ide.PluginInfo;
import polyglot.ide.jl5.JL5PluginInfo;
import polyglot.ide.wizards.JLConfigureBuildPathCommandHandler;

public class JL5ConfigureBuildPathCommandHandler extends JLConfigureBuildPathCommandHandler {

  public JL5ConfigureBuildPathCommandHandler() {
    this(JL5PluginInfo.INSTANCE);
  }

  public JL5ConfigureBuildPathCommandHandler(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
