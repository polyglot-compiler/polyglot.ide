package polyglot.jl5.ide.editors;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import polyglot.ide.editors.Editor;
import polyglot.ide.editors.ReconcilingStrategy;
import polyglot.jl5.ide.JL5Nature;

public class JL5ReconcilingStrategy extends ReconcilingStrategy {

	public JL5ReconcilingStrategy(Editor editor) {
		super(editor);
	}
	
	@Override
	protected boolean checkNature(IProject project) {
		try {
			if (Arrays.asList(project.getDescription().getNatureIds())
					.contains(JL5Nature.NATURE_ID))
				return true;
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return false;
	}
}
