package pide.editors;

import static pide.editors.ColorManager.COMMENT_COLOR;
import static pide.editors.ColorManager.DEFAULT_COLOR;
import static pide.editors.ColorManager.KEYWORD_COLOR;
import static pide.editors.ColorManager.STRING_COLOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;

/**
 * Scanner for syntax highlighting. This is hooked in by
 * {@link SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)}.
 */
public class CodeScanner extends RuleBasedScanner {
  public CodeScanner(Set<String> keywords, ColorManager cm) {
    IToken keyword =
        new Token(new TextAttribute(cm.getColor(KEYWORD_COLOR), null, SWT.BOLD));
    IToken string = new Token(new TextAttribute(cm.getColor(STRING_COLOR)));
    IToken comment = new Token(new TextAttribute(cm.getColor(COMMENT_COLOR)));
    IToken other = new Token(new TextAttribute(cm.getColor(DEFAULT_COLOR)));

    List<IRule> rules = new ArrayList<>();

    // Rule for line comments.
    rules.add(new EndOfLineRule("//", comment));

    // Rule for strings.
    rules.add(new SingleLineRule("\"", "\"", string, '\\'));
    rules.add(new SingleLineRule("'", "'", string, '\\'));

    // Generic whitespace rule.
    rules.add(new WhitespaceRule(new IWhitespaceDetector() {
      @Override
      public boolean isWhitespace(char c) {
        return Character.isWhitespace(c);
      }
    }));

    // Rule for keywords.
    {
      WordRule wordRule = new WordRule(new IWordDetector() {
        @Override
        public boolean isWordStart(char c) {
          return Character.isJavaIdentifierStart(c);
        }

        @Override
        public boolean isWordPart(char c) {
          return Character.isJavaIdentifierPart(c);
        }
      }, other);

      for (String kw : keywords) {
        wordRule.addWord(kw, keyword);
      }

      rules.add(wordRule);
    }

    // Configure with the sequence for rules we've built up.
    setRules(rules.toArray(new IRule[rules.size()]));
  }
}
