package polyglot.ide.jl7;

import polyglot.ext.jl7.JL7ExtensionInfo;
import polyglot.frontend.ExtensionInfo;
import polyglot.ide.jl5.JL5PluginInfo;

public class JL7PluginInfo extends JL5PluginInfo {

  @SuppressWarnings("hiding")
  public static final JL7PluginInfo INSTANCE = new JL7PluginInfo();

  @Override
  public String pluginID() {
    return "polyglot.ide.jl7";
  }

  @Override
  public String langName() {
    return "JL7";
  }

  @Override
  public String langShortName() {
    return "JL7";
  }

  @Override
  public ExtensionInfo makeExtInfo() {
    return new JL7ExtensionInfo();
  }

  @Override
  public String natureID() {
    return "polyglot.ide.jl7.jl7nature";
  }

  @Override
  public String builderId() {
    return "polyglot.ide.jl7.jl7Builder";
  }

}
