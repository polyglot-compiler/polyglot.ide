package polyglot.ide;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import polyglot.ide.common.BuildpathUtil;
import polyglot.ide.wizards.AbstractProjectBuilder;

public class JLProjectBuilder extends AbstractProjectBuilder {

  /**
   * A hook for Eclipse to instantiate this class.
   */
  public JLProjectBuilder() {
    this(JLPluginInfo.INSTANCE);
  }

  /**
   * A hook for extensions to instantiate this class.
   */
  protected JLProjectBuilder(PluginInfo pluginInfo) {
    super(pluginInfo);
  }

  @Override
  protected List<String> compilerArgs(Set<String> filesToCompile) {
    File buildpathFile = buildpathFile();
    String classpath = BuildpathUtil.parse(pluginInfo, buildpathFile, "");
    String binPath = outputLocation();

    List<String> result =
        new ArrayList<>(Arrays.asList("-d", binPath, "-classpath", classpath));
    result.addAll(filesToCompile);

    return result;
  }
}
