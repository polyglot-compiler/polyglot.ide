package polyglot.ide.editors;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.PlatformUI;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Documentable;
import polyglot.ast.Field;
import polyglot.ast.Javadoc;
import polyglot.ast.Lang;
import polyglot.ast.Local;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.SourceFile;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.LocalInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class PolyglotASTUtil {
  public static JLHyperlink getHyperlink(ITextViewer textViewer, int offset) {
    JLEditor editor = null;
    try {
      editor =
          (JLEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage().getActiveEditor();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    SourceFile outputAST =
        ReconcilingStrategy.getAST(editor.getFile().getLocation().toString());

    if (outputAST == null) return null;

    FirstPassNodeVisitor firstPassNodeVisitor =
        new FirstPassNodeVisitor(outputAST.lang(), offset);
    outputAST.visitChildren(firstPassNodeVisitor);

    Position pos = firstPassNodeVisitor.pos;
    String name = firstPassNodeVisitor.name;

    if (pos == null || pos == Position.COMPILER_GENERATED || name == null)
      return null;

    IRegion targetRegion =
        getTargetRegion(textViewer.getDocument(), offset, name);
    if (targetRegion == null) return null;

    return new JLHyperlink(targetRegion, name, pos);
  }

  private static IRegion getTargetRegion(IDocument document, int offset,
      String name) {
    IRegion targetRegion = null;
    IRegion lineRegion;
    String candidate;

    try {
      lineRegion = document.getLineInformationOfOffset(offset);
      candidate = document.get(lineRegion.getOffset(), lineRegion.getLength());
    } catch (BadLocationException ex) {
      ex.printStackTrace();
      return null;
    }

    int index = 0;

    while (true) {
      index = candidate.indexOf(name, index);

      if (index == -1) break;

      targetRegion = new Region(lineRegion.getOffset() + index, name.length());
      if ((targetRegion.getOffset() <= offset)
          && ((targetRegion.getOffset() + targetRegion.getLength()) >= offset))
        break;

      index += name.length();
    }

    return targetRegion;
  }

  public static String getJavadoc(Position pos) {
    SourceFile outputAST = ReconcilingStrategy.getAST(pos.path());

    if (outputAST == null) return null;

    SecPassNodeVisitor secPassNodeVisitor =
        new SecPassNodeVisitor(outputAST.lang(), pos);
    outputAST.visitChildren(secPassNodeVisitor);

    return secPassNodeVisitor.getJavadoc();
  }

  private static class FirstPassNodeVisitor extends NodeVisitor {
    int offset;
    Position pos;
    String name;

    public FirstPassNodeVisitor(Lang lang, int offset) {
      super(lang);
      this.offset = offset;
    }

    @Override
    public NodeVisitor enter(Node n) {
      if (offset >= n.position().offset() && offset <= n.position().endOffset()) {
        if (n instanceof New) {
          New newNode = ((New) n);
          ConstructorInstance constructorInstance =
              newNode.constructorInstance();
          name = newNode.type().toString();
          pos = constructorInstance.position();
        } else if (n instanceof Call) {
          Call call = (Call) n;
          MethodInstance methodInstance = call.methodInstance();
          name = methodInstance.name();
          pos = methodInstance.position();
        } else if (n instanceof Field) {
          Field field = ((Field) n);
          FieldInstance fieldInstance = field.fieldInstance();
          name = fieldInstance.name();
          pos = fieldInstance.position();
        } else if (n instanceof Local) {
          LocalInstance localInstance = ((Local) n).localInstance();
          pos = localInstance.position();
          name = localInstance.name();
        } else if (n instanceof Block) {
          List<Stmt> statements = ((Block) n).statements();
          if (statements != null && !statements.isEmpty()) {
            Stmt stmt = statements.get(0);

            if (stmt instanceof ConstructorCall) {
              ConstructorInstance constructorInstance =
                  ((ConstructorCall) stmt).constructorInstance();
              pos = constructorInstance.position();
              name = ((ConstructorCall) stmt).kind().toString();
            }
          }
        } else if (n instanceof TypeNode) {
          TypeNode typeNode = (TypeNode) n;
          name = typeNode.name();
          Type type = typeNode.type();
          pos = type.position();
        } else if (n instanceof Special) {
          Special special = (Special) n;
          name = special.kind().toString();
          pos = special.type().position();
        } else if (n instanceof Documentable) {
          pos = n.position();
          name = ((Documentable) n).id().id();
        }
      }

      return super.enter(n);
    }
  }

  private static class SecPassNodeVisitor extends NodeVisitor {
    Position pos;
    String javadocText;

    public SecPassNodeVisitor(Lang lang, Position pos) {
      super(lang);
      this.pos = pos;
    }

    @Override
    public NodeVisitor enter(Node n) {
      if (n.position() == pos) {
        if (n instanceof Documentable) {
          Documentable documentable = ((Documentable) n);

          Javadoc javadoc = documentable.javadoc();
          if (javadoc != null) javadocText = javadoc.getText();
        }
      }

      return super.enter(n);
    }

    public String getJavadoc() {
      if (javadocText == null) return null;

      StringBuilder res = new StringBuilder();
      String[] parts = javadocText.split("/\\*\\*");
      if (parts != null && parts.length > 1) {
        parts = parts[1].split("\\*/");
      }

      if (parts != null) {
        String[] lines = parts[0].split("\n(\\s)*\\*");
        if (lines != null) {
          for (String line : lines) {
            res.append(line).append("\n");
          }
        }
      }

      return res.toString().trim();
    }
  }
}
