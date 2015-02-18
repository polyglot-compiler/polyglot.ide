package polyglot.ide.editors;

import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;
import static polyglot.ide.editors.ColorManager.COMMENT_COLOR;
import static polyglot.ide.editors.ColorManager.DEFAULT_COLOR;
import static polyglot.ide.editors.ColorManager.STRING_COLOR;
import static polyglot.ide.editors.PartitionScanner.BLOCK_COMMENT;
import static polyglot.ide.editors.PartitionScanner.CHAR_LITERAL;
import static polyglot.ide.editors.PartitionScanner.LINE_COMMENT;
import static polyglot.ide.editors.PartitionScanner.STRING_LITERAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * Provides hooks for plugging in custom editor-related UI behaviour (e.g.,
 * syntax highlighting, text hovering, code completion). This is hooked in by
 * {@link AbstractEditor#AbstractEditor()}.
 */
public class SourceViewerConfiguration extends
org.eclipse.jface.text.source.SourceViewerConfiguration {

  private final Editor editor;
  private final ColorManager colorManager;
  private final CodeScanner scanner;

  public SourceViewerConfiguration(Editor editor, ColorManager colorManager) {
    this.editor = editor;
    this.colorManager = colorManager;

    scanner = new CodeScanner(editor.extInfo().keywords(), colorManager);
    scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager
        .getColor(DEFAULT_COLOR))));
  }

  @Override
  public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
    // Obtain a list of the super class's configured partition types.
    String[] superResult = super.getConfiguredContentTypes(sourceViewer);
    List<String> types = new ArrayList<>(Arrays.asList(superResult));

    // Add our configured partition types.
    types.addAll(Arrays.asList(PartitionScanner.PARTITION_TYPES));

    // Convert to array and return.
    return types.toArray(new String[types.size()]);
  }

  @Override
  public IPresentationReconciler getPresentationReconciler(
      ISourceViewer sourceViewer) {
    PresentationReconciler reconciler = new PresentationReconciler();

    // Use a CodeScanner-based repairer for DEFAULT_CONTENT_TYPE partitions.
    DefaultDamagerRepairer defaultRepairer =
        new DefaultDamagerRepairer(scanner);
    reconciler.setDamager(defaultRepairer, DEFAULT_CONTENT_TYPE);
    reconciler.setRepairer(defaultRepairer, DEFAULT_CONTENT_TYPE);

    // Use the non-rule-based repairer for BLOCK_COMMENT partitions.
    NonRuleBasedDamagerRepairer blockCommentRepairer =
        new NonRuleBasedDamagerRepairer(new TextAttribute(
            colorManager.getColor(COMMENT_COLOR)));
    reconciler.setDamager(blockCommentRepairer, BLOCK_COMMENT);
    reconciler.setRepairer(blockCommentRepairer, BLOCK_COMMENT);

    // Use the non-rule-based repairer for LINE_COMMENT partitions.
    NonRuleBasedDamagerRepairer lineCommentRepairer =
        new NonRuleBasedDamagerRepairer(new TextAttribute(
            colorManager.getColor(COMMENT_COLOR)));
    reconciler.setDamager(lineCommentRepairer, LINE_COMMENT);
    reconciler.setRepairer(lineCommentRepairer, LINE_COMMENT);

    // Use the non-rule-based repairer for STRING_LITERAL partitions.
    NonRuleBasedDamagerRepairer stringRepairer =
        new NonRuleBasedDamagerRepairer(new TextAttribute(
            colorManager.getColor(STRING_COLOR)));
    reconciler.setDamager(stringRepairer, STRING_LITERAL);
    reconciler.setRepairer(stringRepairer, STRING_LITERAL);

    // Use the non-rule-based repairer for CHAR_LITERAL partitions.
    NonRuleBasedDamagerRepairer charRepairer =
        new NonRuleBasedDamagerRepairer(new TextAttribute(
            colorManager.getColor(STRING_COLOR)));
    reconciler.setDamager(charRepairer, CHAR_LITERAL);
    reconciler.setRepairer(charRepairer, CHAR_LITERAL);

    return reconciler;
  }

  @Override
  public IReconciler getReconciler(ISourceViewer sourceViewer) {
    MonoReconciler reconciler =
        new MonoReconciler(new ReconcilingStrategy(editor), false);
    reconciler.install(sourceViewer);
    return reconciler;
  }

  @Override
  public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer,
      String contentType) {
    // Obtain a list of the super class's configured auto-edit strategies.
    List<IAutoEditStrategy> strategies =
        new ArrayList<>(Arrays.asList(super.getAutoEditStrategies(sourceViewer,
            contentType)));

    // Add our own strategies.
    if (DEFAULT_CONTENT_TYPE.equals(contentType)) {
      strategies.add(new AutoEditStrategy());
    }

    // Convert to array and return.
    return strategies.toArray(new IAutoEditStrategy[strategies.size()]);
  }

  @Override
  public IContentAssistant getContentAssistant(ISourceViewer sv) {
    ContentAssistant assistant = new ContentAssistant();

    assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sv));
    assistant.setContentAssistProcessor(new PolyglotContentAssistProcessor(),
        IDocument.DEFAULT_CONTENT_TYPE);

    assistant.setAutoActivationDelay(0);
    assistant.enableAutoActivation(true);

    assistant.setProposalSelectorBackground(Display.getDefault()
        .getSystemColor(SWT.COLOR_WHITE));

    return assistant;
  }

  @Override
  public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
    return new DefaultAnnotationHover();
  }

  @Override
  public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
    return new TextHover();
  }

  @Override
  public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
    return new IHyperlinkDetector[] { new JLHyperlinkDetector(),
        new URLHyperlinkDetector() };
  }
}
