package polyglot.ide;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import polyglot.frontend.ExtensionInfo;
import polyglot.frontend.JLExtensionInfo;
import polyglot.ide.common.BuildpathUtil;
import polyglot.ide.common.ErrorUtil;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

public class JLPluginInfo implements PluginInfo {

  public static final JLPluginInfo INSTANCE = new JLPluginInfo();

  protected JLPluginInfo() {
  }

  @Override
  public String pluginID() {
    return "polyglot.ide";
  }

  @Override
  public String langName() {
    return "JL";
  }

  @Override
  public String langShortName() {
    return "JL";
  }

  @Override
  public ExtensionInfo makeExtInfo() {
    return new JLExtensionInfo();
  }

  @Override
  public String natureID() {
    return "polyglot.ide.jlnature";
  }

  @Override
  public String builderId() {
    return "polyglot.ide.jlBuilder";
  }

  /**
   * @return a classpath that to be appended to all classpaths passed to the
   *         compiler.
   */
  protected List<String> baseClasspath() {
    return Collections.emptyList();
  }

  @Override
  public List<String> compilerArgs(boolean validateOnly, IProject project,
      Collection<String> sourceFiles) {
    return addCompilerArgs(validateOnly, project, sourceFiles,
        new ArrayList<String>());
  }

  /**
   * Adds compilerArgs to the given result list. @see #compilerArgs(IProject,
   * Collection).
   *
   * @param validateOnly
   *          if {@code true}, then arguments for validation mode will be
   *          generated.
   * @param project
   *          the project for which command-line arguments should be added.
   * @param sourceFiles
   *          specifies the names of the files to compile.
   * @param result
   *          the list to which command-line arguments should be added.
   * @return {@code result} (for fluency).
   */
  protected List<String> addCompilerArgs(boolean validateOnly, IProject project,
      Collection<String> sourceFiles, List<String> result) {

    File buildpathFile = BuildpathUtil.buildpathFile(project);
    List<String> classpath = BuildpathUtil.parse(this, buildpathFile);
    String outputPath = validateOnly ? System.getProperty("java.io.tmpdir")
        : BuildpathUtil.getOutputDir(this, project);
    String srcPath = BuildpathUtil.getSourcePathString(this, buildpathFile, "");

    // Append base classpath.
    classpath.addAll(baseClasspath());

    result.addAll(Arrays.asList("-d", outputPath));

    if (!classpath.isEmpty()) {
      result.addAll(
          Arrays.asList("-classpath", BuildpathUtil.flattenPath(classpath)));
    }

    if (!"".equals(srcPath)) {
      result.addAll(Arrays.asList("-sourcepath", srcPath));
    }

    result.addAll(sourceFiles);
    return result;
  }

  @Override
  public String defaultOutputLocation(IProject project) {
    return project.getFile(BuildpathUtil.OUTPUT_DIR_NAME).getRawLocation()
        .toOSString();
  }

  /**
   * @return a path to a file in the plug-in directory.
   */
  protected String getPluginPath(String filename) {
    return getPluginPath(Platform.getBundle(pluginID()), filename);
  }

  /**
   * @return a path to a file in the directory of the given plug-in/bundle.
   */
  protected String getPluginPath(Bundle bundle, String filename) {
    URL url = FileLocator.find(bundle, new Path(filename), null);

    if (url != null) {
      try {
        URI uri = FileLocator.toFileURL(url).toURI();
        return uri.getPath();
      } catch (Exception e) {
        ErrorUtil.handleError(this, Level.WARNING,
            "Unable to include default classpath entries.", e.getCause(),
            Style.BLOCK);
      }
    }

    return "";
  }

}
