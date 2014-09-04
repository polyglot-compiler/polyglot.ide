package polyglot.ide.editors;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.JLExtensionInfo;

public class JLEditor extends AbstractEditor {

  @Override
  public ExtensionInfo extInfo() {
    return new JLExtensionInfo();
  }

}
