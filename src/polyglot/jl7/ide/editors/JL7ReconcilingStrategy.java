package polyglot.jl7.ide.editors;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import polyglot.ide.editors.Editor;
import polyglot.ide.editors.ReconcilingStrategy;
import polyglot.jl7.ide.JL7Nature;

public class JL7ReconcilingStrategy extends ReconcilingStrategy {

	public JL7ReconcilingStrategy(Editor editor) {
		super(editor);
	}
	
	@Override
	protected boolean checkNature(IProject project) {
		try {
			if (Arrays.asList(project.getDescription().getNatureIds())
					.contains(JL7Nature.NATURE_ID))
				return true;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}
}
