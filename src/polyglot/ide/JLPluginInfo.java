package polyglot.ide;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.JLExtensionInfo;

public class JLPluginInfo implements PluginInfo {

  public static final JLPluginInfo INSTANCE = new JLPluginInfo();

  protected JLPluginInfo() {
  }

  @Override
  public String pluginID() {
    return "polyglot.ide";
  }

  @Override
  public String langName() {
    return "JL";
  }

  @Override
  public String langShortName() {
    return "JL";
  }

  @Override
  public ExtensionInfo makeExtInfo() {
    return new JLExtensionInfo();
  }

  @Override
  public String natureID() {
    return "polyglot.ide.jlnature";
  }

  @Override
  public String builderId() {
    return "polyglot.ide.jlBuilder";
  }

}
