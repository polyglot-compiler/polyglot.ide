package polyglot.jl7.ide.wizards;

import polyglot.ide.PluginInfo;
import polyglot.jl5.ide.wizards.JL5ConfigureBuildPathCommandHandler;
import polyglot.jl7.ide.JL7PluginInfo;

public class JL7ConfigureBuildPathCommandHandler extends JL5ConfigureBuildPathCommandHandler {

  public JL7ConfigureBuildPathCommandHandler() {
    this(JL7PluginInfo.INSTANCE);
  }

  public JL7ConfigureBuildPathCommandHandler(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
