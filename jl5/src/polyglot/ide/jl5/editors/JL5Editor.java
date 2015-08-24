package polyglot.ide.jl5.editors;

import polyglot.ide.PluginInfo;
import polyglot.ide.editors.JLEditor;
import polyglot.ide.jl5.JL5PluginInfo;

public class JL5Editor extends JLEditor {

  public JL5Editor() {
    this(JL5PluginInfo.INSTANCE);
  }

  public JL5Editor(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
