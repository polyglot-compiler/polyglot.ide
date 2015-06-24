package polyglot.ide;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Controls the plug-in life cycle. Referenced from plug-in manifest.
 */
public abstract class AbstractPlugin extends AbstractUIPlugin {

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);

    try {
      ResourcesPlugin.getWorkspace().build(
          IncrementalProjectBuilder.FULL_BUILD, null);
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }

}
