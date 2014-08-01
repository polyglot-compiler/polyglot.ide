/*
 * Some of this code was cribbed from BasicNewProjectResourceWizard. Good
 * practice says we should subclass, but BasicNewProjectResourceWizard is not
 * intended to be subclassed.
 */
package pide.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import pide.common.ErrorUtil;
import pide.common.ErrorUtil.Level;
import pide.common.ErrorUtil.Style;

public class NewJLProjectWizard extends Wizard implements INewWizard {

  /**
   * The object selection with which this wizard was initialized.
   */
  private IStructuredSelection selection;

  /**
   * The first page to be displayed.
   */
  private WizardNewProjectCreationPage pageOne;

  /**
   * The project created by this wizard, or {@code null} if it has yet to be
   * created.
   */
  private IProject project;

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.selection = selection;
    setNeedsProgressMonitor(true);
    setWindowTitle("New JL Project");
  }

  @Override
  public boolean performFinish() {
    if (createProject() == null) return false;
    
    // TODO: update perspective and open the project
    return true;
  }

  @Override
  public void addPages() {
    super.addPages();

    pageOne = new WizardNewProjectCreationPage("newJLProjectPage") {
      @Override
      public void createControl(Composite parent) {
        super.createControl(parent);
        createWorkingSetGroup((Composite) getControl(), selection,
            new String[] { "org.eclipse.ui.resourceWorkingSetPage" });
        Dialog.applyDialogFont(getControl());
        
        // TODO add project-layout controls
      }
    };
    pageOne.setTitle("Create a JL Project");
    pageOne.setDescription("Enter a project name.");
    addPage(pageOne);
    
    // TODO: add additional pages
  }

  /**
   * Creates a project resource with the selected name.
   * <p>
   * In normal usage, this is invoked after the user has pressed Finish on the
   * wizard. The Finish button is only enabled when all wizard inputs are
   * successfully validated.
   * </p>
   * <p>
   * This wizard caches the new project once it has been successfully created.
   * Subsequent invocations of this method will return the same project resource
   * without attempting to create it again.
   * </p>
   * 
   * @return the created project resource, or {@code null} if the project was
   *         not created.
   */
  private IProject createProject() {
    if (project != null) return project;

    // Obtain the project handle and descriptor.
    final IProject newProjectHandle = pageOne.getProjectHandle();
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IProjectDescription description =
        workspace.newProjectDescription(newProjectHandle.getName());

    // Set the project's location.
    final URI location =
        pageOne.useDefaults() ? null : pageOne.getLocationURI();
    description.setLocationURI(location);
    
    // TODO add JL nature

    // Create the project in a separate thread.
    try {
      getContainer().run(true, true, new IRunnableWithProgress() {
        @Override
        public void run(IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException {
          try {
            CreateProjectOperation op =
                new CreateProjectOperation(description, "Create a JL Project");
            op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
            
            // TODO set up project layout
          } catch (Throwable t) {
            throw new InvocationTargetException(t);
          }
        }
      });
      return project = newProjectHandle;
    } catch (InterruptedException e) {
      return null;
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      if (t instanceof ExecutionException
          && t.getCause() instanceof CoreException) {
        CoreException cause = (CoreException) t.getCause();

        // Handle errors caused by case-insensitive file systems.
        if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
          ErrorUtil.handleError(Level.WARNING, "pide",
              "Error creating project",
              "The underlying file system is case insensitive. There is an "
                  + "existing project or directory that conflicts with '"
                  + newProjectHandle.getName() + "'.", cause, Style.BLOCK);
          return null;
        }

        ErrorUtil.handleError(
            ErrorUtil.toLevel(cause.getStatus().getSeverity(), Level.WARNING),
            "pide", "Error creating project", cause, Style.BLOCK);
        return null;
      }

      ErrorUtil.handleError(Level.WARNING, "pide", "Error creating project",
          "Internal error: " + t.getMessage(), t, Style.LOG, Style.BLOCK);
      return null;
    }
  }
}
