package pide.editors;

import polyglot.frontend.ExtensionInfo;

public class JLEditor extends AbstractEditor {

  @Override
  public ExtensionInfo extInfo() {
    boolean useJif = true;
    if (useJif) {
      return new jif.ExtensionInfo();
    } else {
      return new polyglot.frontend.JLExtensionInfo();
    }
  }

}
