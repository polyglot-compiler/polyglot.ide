package polyglot.ide.editors;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class PolyglotContentAssistProcessor implements IContentAssistProcessor {

  @Override
  public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
      int offset) {

    ICompletionProposal proposal =
        new CompletionProposal("TODO: Populate suggestions here ....", 0, 5, 0);
    return new ICompletionProposal[] { proposal };
  }

  @Override
  public IContextInformation[] computeContextInformation(ITextViewer viewer,
      int offset) {

    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public char[] getCompletionProposalAutoActivationCharacters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public char[] getContextInformationAutoActivationCharacters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getErrorMessage() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IContextInformationValidator getContextInformationValidator() {
    // TODO Auto-generated method stub
    return null;
  }

}
