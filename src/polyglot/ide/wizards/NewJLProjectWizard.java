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
  protected IStructuredSelection selection;

  /**
   * The first page to be displayed.
   */
  protected WizardNewProjectCreationPage pageOne;

  protected NewJLProjectWizardPageTwo pageTwo;

  /**
   * The project created by this wizard, or {@code null} if it has yet to be
   * created.
   */
  protected IProject project;

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
    return "New JL Project";
  }

  @Override
  public void addPages() {
    pageOne = new WizardNewProjectCreationPage("newJLProjectPageOne") {
      @Override
      public void createControl(Composite parent) {
        super.createControl(parent);
        createWorkingSetGroup((Composite) getControl(), selection,
            new String[] { "org.eclipse.ui.resourceWorkingSetPage" });
        Dialog.applyDialogFont(getControl());
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
                new CreateProjectOperation(description, "Create a JL Project");
            op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
          } catch (Throwable t) {
            throw new InvocationTargetException(t);
          }
        }
      });

      addNature();

      createSrcBinFolders();

      createClasspathFile();

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
          ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
              "Error creating project",
              "The underlying file system is case insensitive. There is an "
                  + "existing project or directory that conflicts with '"
                  + project.getName() + "'.", cause, Style.BLOCK);
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

  protected void addNature() {
    try {
      final IProjectDescription description =
          ResourcesPlugin.getWorkspace().newProjectDescription(
              project.getName());
      String[] natures = description.getNatureIds();
      String[] newNatures = new String[natures.length + 1];
      System.arraycopy(natures, 0, newNatures, 0, natures.length);
      newNatures[natures.length] = "polyglot.ide.natures.jlnature";
      description.setNatureIds(newNatures);
      project.setDescription(description, null);
    } catch (CoreException e) {
      // Something went wrong
    }
  }

  private void createSrcBinFolders() {
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

  protected void createClasspathFile() {
    List<ClasspathEntry> classpathEntries = new ArrayList<>();
    classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.SRC, "src"));
    List<LibraryResource> libraryResourceList = pageTwo.getClasspathEntries();

    if (libraryResourceList != null)
      for (LibraryResource libraryResource : libraryResourceList)
        classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.LIB,
            libraryResource.getName()));

    classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.OUTPUT, "bin"));

    try {
      ClasspathUtil.createClasspathFile(project, classpathEntries);
    } catch (Exception e) {
      ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
          "Error creating dot-classpath file. Please check file permissions",
          e.getCause(), Style.BLOCK);
    }
  }

  private void associateBuilder() {
    try {
      String builderId = getBuilderId();
      IProjectDescription desc = project.getDescription();
      ICommand[] commands = desc.getBuildSpec();
      boolean found = false;

      for (ICommand command : commands) {
        if (command.getBuilderName().equals(builderId)) {
          found = true;
          break;
        }
      }
      if (!found) {
        // add builder to project
        ICommand command = desc.newCommand();
        command.setBuilderName(builderId);
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

  protected String getBuilderId() {
    return "polyglot.ide.builder.jlBuilder";
  }
}
