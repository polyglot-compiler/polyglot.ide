package polyglot.ide;

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
   * @return a short name for this plugin, in camelCase.
   */
  String shortName();
}
