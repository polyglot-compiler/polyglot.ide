package polyglot.ide.jl7;

import polyglot.ide.PluginInfo;
import polyglot.ide.jl5.JL5ProjectBuilder;

public class JL7ProjectBuilder extends JL5ProjectBuilder {
	public JL7ProjectBuilder() {
	  this(JL7PluginInfo.INSTANCE);
	}
	
	public JL7ProjectBuilder(PluginInfo pluginInfo) {
	  super(pluginInfo);
	}
}
