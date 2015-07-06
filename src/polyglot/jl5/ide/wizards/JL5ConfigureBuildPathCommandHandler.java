package polyglot.jl5.ide.wizards;

import polyglot.ide.PluginInfo;
import polyglot.ide.wizards.JLConfigureBuildPathCommandHandler;
import polyglot.jl5.ide.JL5PluginInfo;

public class JL5ConfigureBuildPathCommandHandler extends JLConfigureBuildPathCommandHandler {

  public JL5ConfigureBuildPathCommandHandler() {
    this(JL5PluginInfo.INSTANCE);
  }

  public JL5ConfigureBuildPathCommandHandler(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
