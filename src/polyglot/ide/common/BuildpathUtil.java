package polyglot.ide.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import polyglot.ide.PluginInfo;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

/**
 * Handles the creation and parsing of a project's {@code .buildpath} file.
 */
public class BuildpathUtil {
  public static final String BUILDPATH_FILE_NAME = ".buildpath";

  public static final String OUTPUT_DIR_NAME = "bin";
  public static final String SRC_DIR_NAME = "src";

  /**
   * @return the .buildpath file for the given project
   */
  public static File buildpathFile(IProject project) {
    return project.getFile(BUILDPATH_FILE_NAME).getRawLocation().toFile();
  }

  /**
   * Creates a {@code .buildpath} file for the given project with the given
   * entries.
   */
  public static void createBuildpathFile(IProject project,
      List<BuildpathEntry> buildpathEntries)
          throws XMLStreamException, IOException {

    XMLOutputFactory xof = XMLOutputFactory.newInstance();
    XMLStreamWriter xtw = new IndentingXMLStreamWriter(
        xof.createXMLStreamWriter(new FileWriter(buildpathFile(project))));

    xtw.writeStartDocument("UTF-8", "1.0");

    xtw.writeStartElement("buildpath");

    for (BuildpathEntry buildpathEntry : buildpathEntries) {
      xtw.writeEmptyElement("entry");

      xtw.writeAttribute("kind", buildpathEntry.getKind().name());
      xtw.writeAttribute("type", buildpathEntry.getType().name());
      xtw.writeAttribute("path", buildpathEntry.getPath());
    }

    xtw.writeEndElement();

    xtw.flush();
    xtw.close();
  }

  /**
   * Converts a path into a string representation.
   */
  public static String flattenPath(List<String> path) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String elt : path) {
      if (!first) sb.append(File.pathSeparator);
      sb.append(elt);
      first = false;
    }
    return sb.toString();
  }

  /**
   * Reads a build path from a {@code .buildpath} file.
   *
   * @param dotBuildpath
   *          the file to parse
   * @param buildpathKind
   *          the kind of build path to read
   */
  public static List<String> parse(PluginInfo pluginInfo, File dotBuildpath,
      BuildpathEntry.Kind buildpathKind) {
    return parse(pluginInfo, dotBuildpath, buildpathKind, null);
  }

  /**
   * Reads a build path from a {@code .buildpath} file and turns it into a
   * string representation.
   *
   * @param dotBuildpath
   *          the file to parse
   * @param buildpathKind
   *          the kind of build path to read
   * @param defaultVal
   *          the value to return if the given build path is not found in the
   *          given file
   */
  public static String parseString(PluginInfo pluginInfo, File dotBuildpath,
      BuildpathEntry.Kind buildpathKind, String defaultVal) {
    return parseString(pluginInfo, dotBuildpath, buildpathKind, null,
        defaultVal);
  }

  /**
   * Reads a path from a {@code .buildpath} file.
   *
   * @param dotBuildpath
   *          the file to parse
   * @param buildpathKind
   *          the kind of build path to read
   * @param buildpathType
   *          the type of build path to read
   */
  public static List<String> parse(PluginInfo pluginInfo, File dotBuildpath,
      BuildpathEntry.Kind buildpathKind, BuildpathEntry.Type buildpathType) {
    BuildpathContentHandler contentHandler = getContentHandler(pluginInfo,
        dotBuildpath, buildpathKind, buildpathType);
    return contentHandler == null ? new ArrayList<String>()
        : contentHandler.getPath();
  }

  /**
   * Reads a path from a {@code .buildpath} file and turns it into a string
   * representation.
   *
   * @param dotBuildpath
   *          the file to parse
   * @param buildpathKind
   *          the kind of build path to read
   * @param buildpathType
   *          the type of build path to read
   * @param defaultVal
   *          the value to return if the given build path is not found in the
   *          given file
   */
  public static String parseString(PluginInfo pluginInfo, File dotBuildpath,
      BuildpathEntry.Kind buildpathKind, BuildpathEntry.Type buildpathType,
      String defaultVal) {
    return flattenPath(
        parse(pluginInfo, dotBuildpath, buildpathKind, buildpathType));
  }

  /**
   * Reads the main classpath from a {@code .buildpath} file.
   *
   * @param dotBuildpath
   *          the file to parse
   */
  public static List<String> parse(PluginInfo pluginInfo, File dotBuildpath) {
    return parse(pluginInfo, dotBuildpath, BuildpathEntry.CLASSPATH);
  }

  /**
   * Reads the main classpath from a {@code .buildpath} file and turns it into a
   * string representation.
   *
   * @param dotBuildpath
   *          the file to parse
   * @param defaultVal
   *          the value to return if the given entry is not found in the given
   *          file
   */
  public static String parseString(PluginInfo pluginInfo, File dotBuildpath,
      String defaultVal) {
    return parseString(pluginInfo, dotBuildpath, BuildpathEntry.CLASSPATH,
        defaultVal);
  }

  /**
   * Reads the source path from a {@code .buildpath} file.
   */
  public static List<String> getSourcePath(PluginInfo pluginInfo,
      File dotBuildpath) {
    return parse(pluginInfo, dotBuildpath, BuildpathEntry.CLASSPATH,
        BuildpathEntry.SRC);
  }

  /**
   * Reads the source path from a {@code .buildpath} file and turns it into a
   * string representation.
   */
  public static String getSourcePathString(PluginInfo pluginInfo,
      File dotBuildpath, String defaultVal) {
    return parseString(pluginInfo, dotBuildpath, BuildpathEntry.CLASSPATH,
        BuildpathEntry.SRC, defaultVal);
  }

  /**
   * Reads the output directory from a {@code .buildpath} file.
   */
  public static String getOutputDir(PluginInfo pluginInfo, IProject project) {
    File dotBuildpath = buildpathFile(project);
    List<String> parsed = parse(pluginInfo, dotBuildpath,
        BuildpathEntry.CLASSPATH, BuildpathEntry.OUTPUT);

    if (parsed.isEmpty()) return pluginInfo.defaultOutputLocation(project);

    // In the case of more than one entry, the last one takes precedence.
    return parsed.get(parsed.size() - 1);
  }

  /**
   * Reads a list of {@code BuildpathEntry}s for the main classpath from a
   * {@code .buildpath} file.
   *
   * @param dotBuildpath
   *          the file to parse
   */
  public static List<BuildpathEntry> getClasspathEntries(PluginInfo pluginInfo,
      File dotBuildpath) {
    return getBuildpathEntries(pluginInfo, dotBuildpath,
        BuildpathEntry.CLASSPATH);
  }

  /**
   * Reads a list of {@code BuildpathEntry}s from a {@code .buildpath} file.
   *
   * @param dotBuildpath
   *          the file to parse
   * @param buildpathKind
   *          the kind of build path to read
   */
  public static List<BuildpathEntry> getBuildpathEntries(PluginInfo pluginInfo,
      File dotBuildpath, BuildpathEntry.Kind buildpathKind) {
    BuildpathContentHandler contentHandler =
        getContentHandler(pluginInfo, dotBuildpath, buildpathKind);
    if (contentHandler != null)
      return contentHandler.getBuildpathEntries();
    else return new ArrayList<>();
  }

  private static BuildpathContentHandler getContentHandler(
      PluginInfo pluginInfo, File dotBuildpath,
      BuildpathEntry.Kind buildpathKind) {
    return getContentHandler(pluginInfo, dotBuildpath, buildpathKind, null);
  }

  private static BuildpathContentHandler getContentHandler(
      PluginInfo pluginInfo, File dotBuildpath,
      BuildpathEntry.Kind buildpathKind, BuildpathEntry.Type buildpathType) {
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      XMLReader parser = spf.newSAXParser().getXMLReader();
      BuildpathContentHandler buildpathContentHandler =
          new BuildpathContentHandler(dotBuildpath, buildpathKind,
              buildpathType);
      parser.setContentHandler(buildpathContentHandler);
      parser.parse(dotBuildpath.toString());
      return buildpathContentHandler;
    } catch (Exception e) {
      ErrorUtil.handleError(pluginInfo, Level.WARNING,
          "Error parsing .buildpath file", e.getCause(), Style.BLOCK);
      return null;
    }
  }

  private static class BuildpathContentHandler extends DefaultHandler {
    List<String> path;
    File dotBuildpath;
    List<BuildpathEntry> buildpathEntries;
    BuildpathEntry.Kind buildpathKind;
    BuildpathEntry.Type buildpathType;

    BuildpathContentHandler(File dotBuildpath,
        BuildpathEntry.Kind buildpathKind, BuildpathEntry.Type buildpathType) {
      this.path = new ArrayList<>();
      this.dotBuildpath = dotBuildpath;
      this.buildpathEntries = new ArrayList<>();
      this.buildpathKind = buildpathKind;
      this.buildpathType = buildpathType;
    }

    public List<String> getPath() {
      return path;
    }

    public List<BuildpathEntry> getBuildpathEntries() {
      return buildpathEntries;
    }

    private static File absolutize(File base, String path) {
      path = path.replace('/', File.separatorChar);
      File child = new File(path);
      if (child.isAbsolute())
        return child;
      else return new File(base, path);
    }

    @Override
    public void startElement(String uri, String localName, String qname,
        Attributes atts) {
      if (!localName.equals("entry")) return; // unknown element type

      String kind = atts.getValue("kind");
      if (buildpathKind != null && !buildpathKind.name().equals(kind)) return;

      String type = atts.getValue("type");
      if (buildpathType != null && !buildpathType.name().equals(type)) return;

      String path = atts.getValue("path");
      if (path == null || path.isEmpty()) return;

      BuildpathEntry.Type buildpathEntryType = BuildpathEntry.Type.get(type);

      if (buildpathEntryType == BuildpathEntry.CON) {
        this.path.add(path);
        buildpathEntries.add(new BuildpathEntry(BuildpathEntry.CON, path));
        return;
      }

      File baseDir = null;
      if (buildpathEntryType == BuildpathEntry.SRC
          || buildpathEntryType == BuildpathEntry.OUTPUT) {
        baseDir = dotBuildpath.getParentFile().getAbsoluteFile();
      } else if (type.equals(BuildpathEntry.LIB.name())) {
        baseDir =
            ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
      }

      this.path.add(absolutize(baseDir, path).toString());

      if (buildpathEntryType == BuildpathEntry.LIB)
        buildpathEntries.add(new BuildpathEntry(buildpathEntryType, path));
    }
  }
}
