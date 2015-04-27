package polyglot.jl7.ide;

import polyglot.ext.jl7.JL7ExtensionInfo;
import polyglot.frontend.ExtensionInfo;
import polyglot.ide.JLProjectBuilder;

public class JL7ProjectBuilder extends JLProjectBuilder {
	
	@Override
	protected ExtensionInfo getExtensionInfo() {
		return new JL7ExtensionInfo();
	}
}
