package polyglot.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import polyglot.ide.common.ClasspathEntry;
import polyglot.ide.common.ClasspathEntry.ClasspathEntryKind;
import polyglot.ide.common.ClasspathUtil;
import polyglot.ide.common.ErrorUtil;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

public class ConfigureBuildPathCommandHandler extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchWindow window =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IStructuredSelection selection =
        (IStructuredSelection) window.getSelectionService().getSelection();
    Object firstElement = selection.getFirstElement();
    IProject project =
        (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);

    ConfigureBuildPathWizard wizard = new ConfigureBuildPathWizard(project);
    WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
    dialog.setPageSize(250, 400);
    dialog.open();

    return null;
  }

  private static class ConfigureBuildPathWizard extends Wizard {
    IProject project;
    NewJLProjectWizardPageTwo buildConfigurationPage;

    ConfigureBuildPathWizard(IProject project) {
      this.project = project;
    }

    @Override
    public void addPages() {
      buildConfigurationPage =
          new NewJLProjectWizardPageTwo("buildConfigWizardPage", project);
      buildConfigurationPage.setTitle("JL Settings");
      buildConfigurationPage.setDescription("Define the JL build settings.");
      addPage(buildConfigurationPage);
    }

    @Override
    public boolean performFinish() {
      createClasspathFile();
      try {
        project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
        project.build(IncrementalProjectBuilder.FULL_BUILD,
            new NullProgressMonitor());

        return true;
      } catch (CoreException e) {
        return false;
      }
    }

    private boolean createClasspathFile() {
      List<ClasspathEntry> classpathEntries = new ArrayList<>();
      classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.SRC, "src"));

      List<LibraryResource> libraryResourceList =
          buildConfigurationPage.getLibraries();
      if (libraryResourceList != null)
        for (LibraryResource libraryResource : libraryResourceList)
          classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.LIB,
              libraryResource.getName()));

      classpathEntries
      .add(new ClasspathEntry(ClasspathEntryKind.OUTPUT, "bin"));

      try {
        ClasspathUtil.createClassPathFile(project, classpathEntries);
        return true;
      } catch (Exception e) {
        ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
            "Error creating dot-classpath file. Please check file permissions",
            e.getCause(), Style.BLOCK);
        return false;
      }
    }
  }
}
