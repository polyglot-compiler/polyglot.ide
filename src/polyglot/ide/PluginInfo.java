package polyglot.ide;

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
}
