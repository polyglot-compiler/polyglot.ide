package polyglot.ide.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;

import polyglot.ide.PluginInfo;

public abstract class AbstractNature implements IProjectNature {

  protected final PluginInfo pluginInfo;

  protected AbstractNature(PluginInfo pluginInfo) {
    this.pluginInfo = pluginInfo;
  }

  /**
   * The project to which this project nature applies.
   */
  protected IProject project;

  @Override
  public IProject getProject() {
    return project;
  }

  @Override
  public void setProject(IProject project) {
    this.project = project;
  }

}
