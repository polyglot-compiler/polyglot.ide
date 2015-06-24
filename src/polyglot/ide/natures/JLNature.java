package polyglot.ide.natures;

import org.eclipse.core.runtime.CoreException;

import polyglot.ide.JLPluginInfo;
import polyglot.ide.PluginInfo;

public class JLNature extends AbstractNature {

  /**
   * A hook for Eclipse to instantiate this class.
   */
  public JLNature() {
    this(JLPluginInfo.INSTANCE);
  }

  /**
   * A hook for extensions to instantiate this class.
   */
  public JLNature(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

  @Override
  public void configure() throws CoreException {
    // TODO Auto-generated method stub

  }

  @Override
  public void deconfigure() throws CoreException {
    // TODO Auto-generated method stub

  }
}
