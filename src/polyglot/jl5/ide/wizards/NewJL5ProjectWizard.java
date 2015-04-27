package polyglot.jl5.ide.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import polyglot.ide.wizards.NewJLProjectWizard;
import polyglot.ide.wizards.NewJLProjectWizardPageTwo;
import polyglot.jl5.ide.JL5Nature;

public class NewJL5ProjectWizard extends NewJLProjectWizard {
	
	@Override
	protected String getTitle() {
		return "New JL5 Project";
	}
	
	@Override
	protected String getNature() {
	    return JL5Nature.NATURE_ID;
	}
	
	@Override
	public void addPages() {
		pageOne = new WizardNewProjectCreationPage("newJL5ProjectPageOne") {
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
		pageOne.setTitle("Create a JL5 Project");
		pageOne.setDescription("Enter a project name.");
		addPage(pageOne);

		pageTwo = new NewJLProjectWizardPageTwo("newJL5ProjectPageTwo");
		pageTwo.setTitle("JL5 Settings");
		pageTwo.setDescription("Define the JL5 build settings.");

		addPage(pageTwo);
	}
	
	@Override
	protected String getBuilderId() {
		return "polyglot.jl5.ide.jl5Builder";
	}
}
