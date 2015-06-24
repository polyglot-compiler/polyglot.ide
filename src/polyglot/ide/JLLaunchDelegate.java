package polyglot.ide;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import polyglot.ide.common.BuildpathUtil;

public class JLLaunchDelegate implements ILaunchConfigurationDelegate {

  @Override
  public void launch(ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException {

    String projectName =
        configuration
        .getAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", "");
    IProject project =
        ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    File classpathFile =
        project.getFile(BuildpathUtil.BUILDPATH_FILE_NAME).getRawLocation()
        .toFile();
    String classpath = BuildpathUtil.parse(classpathFile, "");

    String classToLaunch =
        configuration.getAttribute("org.eclipse.jdt.launching.MAIN_TYPE", "");
    String programArgs =
        configuration.getAttribute(
            "org.eclipse.jdt.launching.PROGRAM_ARGUMENTS", "");

    VMRunnerConfiguration vmConfig =
        new VMRunnerConfiguration(classToLaunch, classpath.split(":"));
    vmConfig.setProgramArguments(programArgs.split(" "));

    IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
    IVMRunner vmRunner = vmInstall.getVMRunner(ILaunchManager.RUN_MODE);
    vmRunner.run(vmConfig, launch, null);
  }
}
