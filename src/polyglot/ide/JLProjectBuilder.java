package polyglot.ide;

import polyglot.ide.wizards.AbstractProjectBuilder;

public class JLProjectBuilder extends AbstractProjectBuilder {

  /**
   * A hook for Eclipse to instantiate this class.
   */
  public JLProjectBuilder() {
    this(JLPluginInfo.INSTANCE);
  }

  /**
   * A hook for extensions to instantiate this class.
   */
  protected JLProjectBuilder(PluginInfo pluginInfo) {
    super(pluginInfo);
  }
}
