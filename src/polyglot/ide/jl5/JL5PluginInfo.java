package polyglot.ide.jl5;

import polyglot.ext.jl5.JL5ExtensionInfo;
import polyglot.frontend.ExtensionInfo;
import polyglot.ide.JLPluginInfo;

public class JL5PluginInfo extends JLPluginInfo {

  @SuppressWarnings("hiding")
  public static final JL5PluginInfo INSTANCE = new JL5PluginInfo();

  @Override
  public String pluginID() {
    return "polyglot.ide.jl5";
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
    return "polyglot.ide.jl5.jl5nature";
  }

  @Override
  public String builderId() {
    return "polyglot.ide.jl5.jl5Builder";
  }

}
