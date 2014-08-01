package pide.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;

public abstract class AbstractNature implements IProjectNature {

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
