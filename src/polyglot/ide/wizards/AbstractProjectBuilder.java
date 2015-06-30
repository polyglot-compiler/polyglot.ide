package polyglot.ide.wizards;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import polyglot.frontend.ExtensionInfo;
import polyglot.ide.PluginInfo;
import polyglot.ide.common.BuildpathUtil;
import polyglot.main.Main;
import polyglot.main.Main.TerminationException;
import polyglot.util.SilentErrorQueue;

public abstract class AbstractProjectBuilder extends IncrementalProjectBuilder {

  protected final PluginInfo pluginInfo;

  protected AbstractProjectBuilder(PluginInfo pluginInfo) {
    this.pluginInfo = pluginInfo;
  }

  protected final ExtensionInfo makeExtensionInfo() {
    return pluginInfo.makeExtInfo();
  }

  @Override
  protected IProject[] build(int kind, Map<String, String> args,
      IProgressMonitor monitor) throws CoreException {

    ExtensionInfo extInfo = makeExtensionInfo();
    Set<String> filesToCompile = filesToCompile(extInfo);
    if (filesToCompile.isEmpty()) return null;

    String[] compilerArgs =
        pluginInfo.compilerArgs(false, getProject(), filesToCompile)
            .toArray(new String[0]);
    Main main = new Main();
    SilentErrorQueue eq = new SilentErrorQueue(100, "compiler");

    try {
      main.start(compilerArgs, extInfo, eq);
    } catch (TerminationException e) {
      // ignore this one
    }

    return null;
  }

  /**
   * @return the names of all files to compile.
   */
  protected Set<String> filesToCompile(ExtensionInfo extInfo) {
    Set<String> result = new HashSet<>();
    for (String srcDir : BuildpathUtil.getSourcePath(pluginInfo,
        buildpathFile())) {
      collectAllFiles(new File(srcDir), result, extInfo);
    }

    return result;
  }

  /**
   * @return the location to which the compiler should emit its output
   */
  protected String outputLocation() {
    return BuildpathUtil.getOutputDir(pluginInfo, getProject());
  }

  /**
   * @return the project's .buildpath file.
   */
  protected File buildpathFile() {
    return BuildpathUtil.buildpathFile(getProject());
  }

  /**
   * Recursively collects all source files in a given directory.
   *
   * @param baseDir
   *          the directory to recursively traverse.
   * @param files
   *          the set to which the names of all encountered source files will be
   *          added.
   * @param extInfo
   *          the language's extension info object.
   */
  protected final void collectAllFiles(File baseDir, Set<String> files,
      ExtensionInfo extInfo) {
    Set<String> fileExts =
        new HashSet<>(Arrays.asList(extInfo.fileExtensions()));

    for (File file : baseDir.listFiles()) {
      if (file.isDirectory())
        collectAllFiles(file, files, extInfo);
      else
        if (fileExts.contains(extension(file.getName())) && file.length() != 0)
          files.add(file.toString());
    }
  }

  /**
   * Gets the extension part of the given filename.
   *
   * @param filename
   *          the name of a file, without any path components.
   */
  protected String extension(String filename) {
    String extension = "";

    int i = filename.lastIndexOf('.');
    if (i > 0) {
      extension = filename.substring(i + 1);
    }

    return extension;
  }

}
