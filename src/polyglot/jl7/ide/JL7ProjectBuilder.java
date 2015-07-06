package polyglot.jl7.ide;

import polyglot.ide.PluginInfo;
import polyglot.jl5.ide.JL5ProjectBuilder;

public class JL7ProjectBuilder extends JL5ProjectBuilder {
	public JL7ProjectBuilder() {
	  this(JL7PluginInfo.INSTANCE);
	}
	
	public JL7ProjectBuilder(PluginInfo pluginInfo) {
	  super(pluginInfo);
	}
}
