package polyglot.ide;

/**
 * Plug-in class for JL language support in Eclipse.
 */
public class JLPlugin extends AbstractPlugin {
  // The plug-in ID
  public static final String PLUGIN_ID = "polyglot.ide"; //$NON-NLS-1$

  // The shared instance.
  private static JLPlugin instance;

  @Override
  protected void setInstance() {
    JLPlugin.instance = this;
  }

  @Override
  protected void clearInstance() {
    instance = null;
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static JLPlugin getDefault() {
    return instance;
  }
}
