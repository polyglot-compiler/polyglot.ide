package polyglot.ide.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import polyglot.ide.PluginInfo;
import polyglot.ide.common.BuildpathUtil;
import polyglot.ide.common.ErrorUtil;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

public abstract class AbstractNewProjectWizard extends AbstractBuildPathWizard
    implements INewWizard {

  /**
   * The object selection in the UI with which this wizard was initialized.
   */
  protected IStructuredSelection selection;

  /**
   * The first page to be displayed.
   */
  protected WizardNewProjectCreationPage pageOne;

  protected AbstractNewProjectWizard(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.selection = selection;
    setNeedsProgressMonitor(true);
    setWindowTitle(getTitle());
  }

  @Override
  public boolean performFinish() {
    return createProject() != null;
  }

  protected String getTitle() {
    return "New " + pluginInfo.langName() + " Project";
  }

  /**
   * {@inheritDoc} The default implementation adds a page for configuring the
   * project name and calls {@link #addExtraPages()} to add any remaining pages
   * to the wizard.
   */
  @Override
  public void addPages() {
    pageOne =
        new WizardNewProjectCreationPage("new" + pluginInfo.langShortName()
            + "ProjectPageOne") {
          @Override
          public void createControl(Composite parent) {
            super.createControl(parent);
            createWorkingSetGroup((Composite) getControl(), selection,
                new String[] { "org.eclipse.ui.resourceWorkingSetPage" });
            Dialog.applyDialogFont(getControl());
          }
        };
    pageOne.setTitle("Create a " + pluginInfo.langName() + " Project");
    pageOne.setDescription("Enter a project name.");
    addPage(pageOne);

    addExtraPages();
  }

  /**
   * Adds the remaining pages to this wizard. This is invoked by the default
   * implementation of {@link #addPages()} to add the remaining pages after the
   * initial project-creation page. These extra pages can be used, for example,
   * to configure the classpath.
   */
  protected abstract void addExtraPages();

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
    project = pageOne.getProjectHandle();

    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IProjectDescription description =
        workspace.newProjectDescription(project.getName());

    // Set the project's location.
    final URI location =
        pageOne.useDefaults() ? null : pageOne.getLocationURI();
    description.setLocationURI(location);

    // Create the project in a separate thread.
    try {
      getContainer().run(true, true, new IRunnableWithProgress() {
        @Override
        public void run(IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException {
          try {
            CreateProjectOperation op =
                new CreateProjectOperation(description, "Create a "
                    + pluginInfo.langName() + " Project");
            op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
          } catch (Throwable t) {
            throw new InvocationTargetException(t);
          }
        }
      });

      addNature();

      createSrcBinFolders();

      writeBuildpathFile();

      associateBuilder();

      return project;
    } catch (InterruptedException e) {
      return null;
    } catch (InvocationTargetException e) {
      Throwable t = e.getTargetException();
      if (t instanceof ExecutionException
          && t.getCause() instanceof CoreException) {
        CoreException cause = (CoreException) t.getCause();

        // Handle errors caused by case-insensitive file systems.
        if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
          ErrorUtil.handleError(pluginInfo, Level.WARNING,
              "Error creating project",
              "The underlying file system is case insensitive. There is an "
                  + "existing project or directory that conflicts with '"
                  + project.getName() + "'.", cause, Style.BLOCK);
          return null;
        }

        ErrorUtil.handleError(pluginInfo,
            ErrorUtil.toLevel(cause.getStatus().getSeverity(), Level.WARNING),
            "Error creating project", cause, Style.BLOCK);
        return null;
      }

      ErrorUtil.handleError(pluginInfo, Level.WARNING,
          "Error creating project", "Internal error: " + t.getMessage(), t,
          Style.LOG, Style.BLOCK);
      return null;
    }
  }

  /**
   * Associates the plug-in's nature extension with the project.
   */
  private void addNature() {
    try {
      IProjectDescription description = project.getDescription();
      description.setNatureIds(new String[] { pluginInfo.natureID() });
      project.setDescription(description, null);
    } catch (CoreException e) {
      ErrorUtil.handleError(pluginInfo, Level.WARNING,
          "Unable to associate nature with project: " + project.getName(),
          "Internal error: " + e.getMessage(), e, Style.LOG, Style.BLOCK);
    }
  }

  /**
   * Creates directories for the project's source files and compiler output.
   */
  private void createSrcBinFolders() {
    int XXX; // hard-coded names.
    IPath srcFolderPath =
        new Path(project.getName()).makeAbsolute().append(BuildpathUtil.SRC_DIR_NAME);
    IPath binFolderPath =
        new Path(project.getName()).makeAbsolute().append(BuildpathUtil.OUTPUT_DIR_NAME);

    IWorkspaceRoot root = project.getWorkspace().getRoot();

    try {
      root.getFolder(srcFolderPath).create(true, true,
          new SubProgressMonitor(new NullProgressMonitor(), 1));

      root.getFolder(binFolderPath).create(
          (IResource.FORCE | IResource.DERIVED), true,
          new SubProgressMonitor(new NullProgressMonitor(), 1));
    } catch (CoreException e) {
      ErrorUtil
          .handleError(
              pluginInfo,
              ErrorUtil.toLevel(e.getStatus().getSeverity(), Level.WARNING),
              "Error initializing project structure. Please check file permissions",
              e.getCause(), Style.BLOCK);
    }
  }

  /**
   * Associates a builder with the project.
   */
  private void associateBuilder() {
    try {
      String builderId = pluginInfo.builderId();
      IProjectDescription desc = project.getDescription();
      ICommand[] commands = desc.getBuildSpec();

      // Ensure the builder is associated with the project.
      boolean found = false;
      for (ICommand command : commands) {
        if (command.getBuilderName().equals(builderId)) {
          found = true;
          break;
        }
      }

      if (!found) {
        // Add the builder to project before all other builders.
        ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 1, commands.length);

        newCommands[0] = desc.newCommand();
        newCommands[0].setBuilderName(builderId);

        desc.setBuildSpec(newCommands);
        project.setDescription(desc, null);
      }

      // Enable auto-build.
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      IWorkspaceDescription description = workspace.getDescription();
      if (!description.isAutoBuilding()) {
        description.setAutoBuilding(true);
        workspace.setDescription(description);
      }
    } catch (CoreException e) {
      ErrorUtil.handleError(pluginInfo,
          ErrorUtil.toLevel(e.getStatus().getSeverity(), Level.WARNING),
          "Error initializing project builder.", e.getCause(), Style.BLOCK);
    }
  }
}
