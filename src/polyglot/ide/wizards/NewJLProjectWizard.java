/*
 * Some of this code was cribbed from BasicNewProjectResourceWizard. Good
 * practice says we should subclass, but BasicNewProjectResourceWizard is not
 * intended to be subclassed.
 */
package polyglot.ide.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;

import polyglot.ide.common.ClasspathEntry;
import polyglot.ide.common.ClasspathEntry.ClasspathEntryKind;
import polyglot.ide.common.ClasspathUtil;
import polyglot.ide.common.ErrorUtil;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

public class NewJLProjectWizard extends Wizard implements INewWizard {

  /**
   * The object selection with which this wizard was initialized.
   */
  private IStructuredSelection selection;

  /**
   * The first page to be displayed.
   */
  private WizardNewProjectCreationPage pageOne;

  private NewJLProjectWizardPageTwo pageTwo;

  /**
   * The project created by this wizard, or {@code null} if it has yet to be
   * created.
   */
  private IProject project;

  private List<LibraryResource> libraryResourceList;

  @Override
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.selection = selection;
    setNeedsProgressMonitor(true);
    setWindowTitle("New JL Project");
  }

  @Override
  public boolean performFinish() {
    libraryResourceList = pageTwo.getLibraries();

    if (createProject() == null) return false;

    // TODO: update perspective and open the project
    return true;
  }

  @Override
  public void addPages() {
    super.addPages();

    pageOne = new WizardNewProjectCreationPage("newJLProjectPageOne") {
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

    pageTwo = new NewJLProjectWizardPageTwo("newJLProjectPageTwo");
    pageTwo.setTitle("JL Settings");
    pageTwo.setDescription("Define the JL build settings.");

    addPage(pageTwo);
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
          } catch (Throwable t) {
            throw new InvocationTargetException(t);
          }
        }
      });

      createSrcBinFolders(newProjectHandle);

      createClasspathFile(newProjectHandle);

      associateBuilder(newProjectHandle);

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
          ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
              "Error creating project",
              "The underlying file system is case insensitive. There is an "
                  + "existing project or directory that conflicts with '"
                  + newProjectHandle.getName() + "'.", cause, Style.BLOCK);
          return null;
        }

        ErrorUtil.handleError(
            ErrorUtil.toLevel(cause.getStatus().getSeverity(), Level.WARNING),
            "polyglot.ide", "Error creating project", cause, Style.BLOCK);
        return null;
      }

      ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
          "Error creating project", "Internal error: " + t.getMessage(), t,
          Style.LOG, Style.BLOCK);
      return null;
    }
  }

  private void createSrcBinFolders(IProject project) {
    IPath srcFolderPath =
        new Path(project.getName()).makeAbsolute().append("src");
    IPath binFolderPath =
        new Path(project.getName()).makeAbsolute().append("bin");

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
          ErrorUtil.toLevel(e.getStatus().getSeverity(), Level.WARNING),
          "polyglot.ide",
          "Error initializing project structure. Please check file permissions",
          e.getCause(), Style.BLOCK);
    }
  }

  private void createClasspathFile(IProject project) {
    List<ClasspathEntry> classpathEntries = new ArrayList<>();
    classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.SRC, "src"));

    if (libraryResourceList != null)
      for (LibraryResource libraryResource : libraryResourceList)
        classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.LIB,
            libraryResource.getName()));

    classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.OUTPUT, "bin"));

    try {
      ClasspathUtil.createClassPathFile(project, classpathEntries);
    } catch (Exception e) {
      ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
          "Error creating dot-classpath file. Please check file permissions",
          e.getCause(), Style.BLOCK);
    }
  }

  private void associateBuilder(IProject project) {
    try {
      final String BUILDER_ID = "polyglot.ide.builder.jlBuilder";
      IProjectDescription desc = project.getDescription();
      ICommand[] commands = desc.getBuildSpec();
      boolean found = false;

      for (ICommand command : commands) {
        if (command.getBuilderName().equals(BUILDER_ID)) {
          found = true;
          break;
        }
      }
      if (!found) {
        // add builder to project
        ICommand command = desc.newCommand();
        command.setBuilderName(BUILDER_ID);
        ICommand[] newCommands = new ICommand[commands.length + 1];

        // Add it before other builders.
        System.arraycopy(commands, 0, newCommands, 1, commands.length);
        newCommands[0] = command;
        desc.setBuildSpec(newCommands);
        project.setDescription(desc, null);
      }

      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      IWorkspaceDescription description = workspace.getDescription();
      if (!description.isAutoBuilding()) {
        description.setAutoBuilding(true);
        workspace.setDescription(description);
      }
    } catch (CoreException e) {
      ErrorUtil.handleError(
          ErrorUtil.toLevel(e.getStatus().getSeverity(), Level.WARNING),
          "polyglot.ide", "Error initializing project builder.", e.getCause(),
          Style.BLOCK);
    }
  }
}
