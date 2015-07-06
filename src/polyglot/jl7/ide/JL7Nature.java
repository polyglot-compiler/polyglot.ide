package polyglot.jl7.ide;

import polyglot.ide.PluginInfo;
import polyglot.jl5.ide.JL5Nature;

public class JL7Nature extends JL5Nature {

  public JL7Nature() {
    this(JL7PluginInfo.INSTANCE);
  }

  public JL7Nature(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
