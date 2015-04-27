package polyglot.jl7.ide.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import polyglot.ide.wizards.NewJLProjectWizard;
import polyglot.ide.wizards.NewJLProjectWizardPageTwo;
import polyglot.jl7.ide.JL7Nature;

public class NewJL7ProjectWizard extends NewJLProjectWizard {
	
	@Override
	protected String getTitle() {
		return "New JL7 Project";
	}
	
	@Override
	protected String getNature() {
	    return JL7Nature.NATURE_ID;
	}
	
	@Override
	public void addPages() {
		pageOne = new WizardNewProjectCreationPage("newJL7ProjectPageOne") {
			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				createWorkingSetGroup(
						(Composite) getControl(),
						selection,
						new String[] { "org.eclipse.ui.resourceWorkingSetPage" });
				Dialog.applyDialogFont(getControl());
			}
		};
		pageOne.setTitle("Create a JL7 Project");
		pageOne.setDescription("Enter a project name.");
		addPage(pageOne);

		pageTwo = new NewJLProjectWizardPageTwo("newJL7ProjectPageTwo");
		pageTwo.setTitle("JL7 Settings");
		pageTwo.setDescription("Define the JL7 build settings.");

		addPage(pageTwo);
	}
	
	@Override
	protected String getBuilderId() {
		return "polyglot.jl7.ide.jl7Builder";
	}
}
