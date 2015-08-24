package polyglot.ide.jl7;

import polyglot.ide.PluginInfo;
import polyglot.ide.jl5.JL5Nature;

public class JL7Nature extends JL5Nature {

  public JL7Nature() {
    this(JL7PluginInfo.INSTANCE);
  }

  public JL7Nature(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
