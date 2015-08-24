package polyglot.ide.editors;

import polyglot.ide.JLPluginInfo;
import polyglot.ide.PluginInfo;

public class JLEditor extends AbstractEditor {

  /**
   * A hook for Eclipse to instantiate this class.
   */
  public JLEditor() {
    this(JLPluginInfo.INSTANCE);
  }

  /**
   * A hook for extensions to instantiate this class.
   */
  protected JLEditor(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

}
