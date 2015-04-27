package polyglot.jl7.ide.editors;

import polyglot.ext.jl7.JL7ExtensionInfo;
import polyglot.frontend.ExtensionInfo;
import polyglot.ide.editors.AbstractEditor;

public class JL7Editor extends AbstractEditor {
	@Override
	public ExtensionInfo extInfo() {
		return new JL7ExtensionInfo();
	}
}
