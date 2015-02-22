package polyglot.ide.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.Wizard;

import polyglot.ide.common.ClasspathEntry;
import polyglot.ide.common.ClasspathEntry.ClasspathEntryKind;
import polyglot.ide.common.ClasspathUtil;
import polyglot.ide.common.ErrorUtil;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

public class JLConfigureBuildPathWizard extends Wizard {
  IProject project;
  NewJLProjectWizardPageTwo buildConfigurationPage;

  JLConfigureBuildPathWizard(IProject project) {
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
    updateClasspathFile();
    try {
      project.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
      project.build(IncrementalProjectBuilder.FULL_BUILD,
          new NullProgressMonitor());

      return true;
    } catch (CoreException e) {
      return false;
    }
  }

  private boolean updateClasspathFile() {
    List<ClasspathEntry> classpathEntries = new ArrayList<>();
    classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.SRC, "src"));

    List<LibraryResource> libraryResourceList =
        buildConfigurationPage.getClasspathEntries();
    if (libraryResourceList != null)
      for (LibraryResource libraryResource : libraryResourceList)
        classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.LIB,
            libraryResource.getName()));

    classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.OUTPUT, "bin"));

    try {
      ClasspathUtil.createClasspathFile(project, classpathEntries);
      return true;
    } catch (Exception e) {
      ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
          "Error updating dot-classpath file. Please check file permissions",
          e.getCause(), Style.BLOCK);
      return false;
    }
  }
}
