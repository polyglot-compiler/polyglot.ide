package polyglot.jl7.ide.editors;

import polyglot.ext.jl7.JL7ExtensionInfo;
import polyglot.frontend.ExtensionInfo;
import polyglot.ide.editors.AbstractEditor;
import polyglot.ide.editors.SourceViewerConfiguration;

public class JL7Editor extends AbstractEditor {
	@Override
	public ExtensionInfo extInfo() {
		return new JL7ExtensionInfo();
	}
	
	@Override
	protected SourceViewerConfiguration createSourceViewerConfiguration() {
	    return new JL7SourceViewerConfiguration(this, colorManager);
	}
}
