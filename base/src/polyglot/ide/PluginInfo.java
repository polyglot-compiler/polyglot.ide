package polyglot.ide;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;

import polyglot.frontend.ExtensionInfo;

/**
 * Encapsulates all information pertaining to a plug-in.
 */
public interface PluginInfo {

  /**
   * @return the fully qualified name by which Eclipse should identify this
   *         plug-in.
   */
  String pluginID();

  /**
   * @return the name of the language. May be presented to users in window
   *         titles and messages.
   */
  String langName();

  /**
   * @return a short version of the name of the language, in CamelCase.
   */
  String langShortName();

  /**
   * @return a new Polyglot ExtensionInfo instance for this plug-in's language.
   */
  ExtensionInfo makeExtInfo();

  /**
   * @return the identifier for the plug-in's nature extension instance. This
   *         should be the same as the identifier given to the
   *         {@code org.eclipse.core.resources.natures} extension point
   *         instantiated in the plug-in's {@code plugin.xml} file.
   */
  String natureID();

  /**
   * @return the identifier for the plug-in's builder. This should be the same
   *         as the builder identifier specified in the
   *         {@code org.eclipse.core.resources.natures} extension point in the
   *         plug-in's {@code plugin.xml} file.
   */
  String builderId();

  /**
   * Builds a list of command-line arguments for the compiler.
   *
   * @param validateOnly
   *          if {@code true}, then arguments for validation mode will be
   *          generated.
   * @param project
   *          the project for which command-line arguments should be built.
   * @param sourceFiles
   *          specifies the names of the files to compile.
   * @return a list of command-line arguments to the compiler for compiling the
   *         given list of source files, using the configuration specified in
   *         the given .buildpath file.
   */
  List<String> compilerArgs(boolean validateOnly, IProject project,
      Collection<String> sourceFiles);

  /**
   * @return the default compiler output directory for the given project.
   */
  String defaultOutputLocation(IProject project);
}
