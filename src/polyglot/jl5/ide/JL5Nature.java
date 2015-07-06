package polyglot.jl5.ide;

import polyglot.ide.PluginInfo;
import polyglot.ide.natures.JLNature;

public class JL5Nature extends JLNature {

  public JL5Nature() {
    this(JL5PluginInfo.INSTANCE);
  }

  public JL5Nature(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
