package polyglot.ide.jl7.editors;

import polyglot.ide.PluginInfo;
import polyglot.ide.jl7.JL7PluginInfo;
import polyglot.ide.jl5.editors.JL5Editor;

public class JL7Editor extends JL5Editor {

  public JL7Editor() {
    this(JL7PluginInfo.INSTANCE);
  }

  public JL7Editor(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
