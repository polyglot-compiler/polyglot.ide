package polyglot.jl5.ide;

import polyglot.ext.jl5.JL5ExtensionInfo;
import polyglot.frontend.ExtensionInfo;
import polyglot.ide.JLProjectBuilder;

public class JL5ProjectBuilder extends JLProjectBuilder {
	
	@Override
	protected ExtensionInfo getExtensionInfo() {
		return new JL5ExtensionInfo();
	}
}
