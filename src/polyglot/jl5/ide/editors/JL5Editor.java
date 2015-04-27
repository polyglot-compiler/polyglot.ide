package polyglot.jl5.ide.editors;

import polyglot.ext.jl5.JL5ExtensionInfo;
import polyglot.frontend.ExtensionInfo;
import polyglot.ide.editors.AbstractEditor;
import polyglot.ide.editors.SourceViewerConfiguration;

public class JL5Editor extends AbstractEditor {
	@Override
	public ExtensionInfo extInfo() {
		return new JL5ExtensionInfo();
	}
	
	@Override
	protected SourceViewerConfiguration createSourceViewerConfiguration() {
	    return new JL5SourceViewerConfiguration(this, colorManager);
	}
}
