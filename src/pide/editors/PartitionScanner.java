package pide.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;

/**
 * Partitions a document into various regions. A region can denote code, a
 * comment, a string literal, or a character literal. This is hooked in by
 * {@link DocumentProvider#createDocument(Object)} and
 * {@link SourceViewerConfiguration#getConfiguredContentTypes(ISourceViewer)}.
 */
public class PartitionScanner extends RuleBasedPartitionScanner {
  // The different partition types.
  public static final String BLOCK_COMMENT = "block comment";
  public static final String LINE_COMMENT = "line comment";
  public static final String STRING_LITERAL = "string";
  public static final String CHAR_LITERAL = "char";
  public static final String[] PARTITION_TYPES =
      new String[] { BLOCK_COMMENT, LINE_COMMENT, STRING_LITERAL, CHAR_LITERAL };

  public static final PartitionScanner INSTANCE = new PartitionScanner();

  /**
   * Creates the partitioner and sets up the partitioning rules.
   */
  private PartitionScanner() {
    final IToken blockComment = new Token(BLOCK_COMMENT);
    final IToken lineComment = new Token(LINE_COMMENT);
    final IToken stringLiteral = new Token(STRING_LITERAL);
    final IToken charLiteral = new Token(CHAR_LITERAL);

    final List<IPredicateRule> rules = new ArrayList<>();

    // Rule for line comments.
    rules.add(new EndOfLineRule("//", lineComment));

    // Rule for string and character literals.
    rules.add(new SingleLineRule("\"", "\"", stringLiteral, '\\'));
    rules.add(new SingleLineRule("'", "'", charLiteral, '\\'));

    // Rule for block comments.
    rules.add(new MultiLineRule("/*", "*/", blockComment, '\0', true));

    // Configure with the set of partitioning rules we've built up.
    setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
  }
}
