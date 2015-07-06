package polyglot.jl5.ide.editors;

import polyglot.ide.PluginInfo;
import polyglot.ide.editors.JLEditor;
import polyglot.jl5.ide.JL5PluginInfo;

public class JL5Editor extends JLEditor {

  public JL5Editor() {
    this(JL5PluginInfo.INSTANCE);
  }

  public JL5Editor(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
