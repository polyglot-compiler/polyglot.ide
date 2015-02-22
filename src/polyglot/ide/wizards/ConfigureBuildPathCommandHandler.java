package polyglot.ide.wizards;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

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

    Wizard wizard = getWizard(project);
    WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
    dialog.setPageSize(250, 400);
    dialog.open();

    return null;
  }

  protected Wizard getWizard(IProject project) {
    return new JLConfigureBuildPathWizard(project);
  }
}
