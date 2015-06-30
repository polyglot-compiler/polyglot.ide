package polyglot.ide.wizards;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;

import polyglot.ide.PluginInfo;
import polyglot.ide.common.BuildpathEntry;
import polyglot.ide.common.BuildpathUtil;
import polyglot.ide.common.ErrorUtil;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

/**
 * A wizard that writes to a project's .buildpath file.
 */
public abstract class AbstractBuildPathWizard extends Wizard {
  protected final PluginInfo pluginInfo;

  /**
   * The project whose .buildpath file is written by this wizard, or
   * {@code null} if it is not yet created.
   */
  protected IProject project;

  protected AbstractBuildPathWizard(PluginInfo pluginInfo) {
    this(pluginInfo, null);
  }

  protected AbstractBuildPathWizard(PluginInfo pluginInfo, IProject project) {
    this.pluginInfo = pluginInfo;
    this.project = project;
  }

  /**
   * Obtains the project's buildpath entries. The default implementation calls
   * {@link #extraBuildpathEntries()} and adds entries for the source and output
   * directories to the result.
   */
  protected List<BuildpathEntry> buildpathEntries() {
    List<BuildpathEntry> result = extraBuildpathEntries();
    int XXX; // hard-coded names
    result.add(new BuildpathEntry(BuildpathEntry.SRC, BuildpathUtil.SRC_DIR_NAME));
    result.add(new BuildpathEntry(BuildpathEntry.OUTPUT, BuildpathUtil.OUTPUT_DIR_NAME));
    return result;
  }

  protected abstract List<BuildpathEntry> extraBuildpathEntries();

  /**
   * Writes the project's .buildpath file.
   *
   * @return {@code true} on success
   */
  protected boolean writeBuildpathFile() {
    try {
      BuildpathUtil.createBuildpathFile(project, buildpathEntries());
      return true;
    } catch (XMLStreamException | IOException e) {
      ErrorUtil.handleError(pluginInfo, Level.WARNING,
          "Error writing .buildpath file. Please check file permisssions",
          e.getCause(), Style.BLOCK);
      return false;
    }
  }
}
