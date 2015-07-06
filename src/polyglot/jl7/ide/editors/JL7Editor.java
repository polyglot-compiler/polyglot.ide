package polyglot.jl7.ide.editors;

import polyglot.ide.PluginInfo;
import polyglot.jl5.ide.editors.JL5Editor;
import polyglot.jl7.ide.JL7PluginInfo;

public class JL7Editor extends JL5Editor {

  public JL7Editor() {
    this(JL7PluginInfo.INSTANCE);
  }

  public JL7Editor(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
