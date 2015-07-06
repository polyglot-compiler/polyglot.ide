package polyglot.jl5.ide;

import polyglot.ext.jl5.JL5ExtensionInfo;
import polyglot.frontend.ExtensionInfo;
import polyglot.ide.JLPluginInfo;

public class JL5PluginInfo extends JLPluginInfo {

  @SuppressWarnings("hiding")
  public static final JL5PluginInfo INSTANCE = new JL5PluginInfo();

  @Override
  public String pluginID() {
    return "polyglot.jl5.ide";
  }

  @Override
  public String langName() {
    return "JL5";
  }

  @Override
  public String langShortName() {
    return "JL5";
  }

  @Override
  public ExtensionInfo makeExtInfo() {
    return new JL5ExtensionInfo();
  }

  @Override
  public String natureID() {
    return "polyglot.jl5.ide.jl5nature";
  }

  @Override
  public String builderId() {
    return "polyglot.jl5.ide.jl5Builder";
  }

}
