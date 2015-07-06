package polyglot.jl7.ide.editors;

import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

import polyglot.ide.editors.ColorManager;
import polyglot.ide.editors.Editor;
import polyglot.ide.editors.SourceViewerConfiguration;

public class JL7SourceViewerConfiguration extends SourceViewerConfiguration {

	public JL7SourceViewerConfiguration(Editor editor, ColorManager colorManager) {
		super(editor, colorManager);
	}

	@Override
	  public IReconciler getReconciler(ISourceViewer sourceViewer) {
		MonoReconciler reconciler =
	        new MonoReconciler(new JL7ReconcilingStrategy(editor), false);
	    reconciler.install(sourceViewer);
	    return reconciler;
	  }
}
