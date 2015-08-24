package polyglot.ide.jl7.wizards;

import polyglot.ide.PluginInfo;
import polyglot.ide.jl7.JL7PluginInfo;
import polyglot.ide.jl5.wizards.JL5ConfigureBuildPathCommandHandler;

public class JL7ConfigureBuildPathCommandHandler extends JL5ConfigureBuildPathCommandHandler {

  public JL7ConfigureBuildPathCommandHandler() {
    this(JL7PluginInfo.INSTANCE);
  }

  public JL7ConfigureBuildPathCommandHandler(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
