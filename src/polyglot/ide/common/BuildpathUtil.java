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

import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

/**
 * Handles the creation and parsing of a project's {@code .buildpath} file.
 */
public class BuildpathUtil {
  public static String BUILDPATH_FILE_NAME = ".buildpath";

  /**
   * Creates a {@code .buildpath} file for the given project with the given
   * entries.
   */
  public static void createBuildpathFile(IProject project,
      List<BuildpathEntry> buildpathEntries) throws XMLStreamException,
      IOException {

    String absoluteBuildpathFileName =
        project.getFile(BUILDPATH_FILE_NAME).getRawLocation().toString();

    XMLOutputFactory xof = XMLOutputFactory.newInstance();
    XMLStreamWriter xtw =
        new IndentingXMLStreamWriter(xof.createXMLStreamWriter(new FileWriter(
            absoluteBuildpathFileName)));

    xtw.writeStartDocument("UTF-8", "1.0");

    xtw.writeStartElement("buildpath");

    for (BuildpathEntry buildpathEntry : buildpathEntries) {
      xtw.writeEmptyElement("entry");

      xtw.writeAttribute("kind", buildpathEntry.getKind().name());
      xtw.writeAttribute("type", buildpathEntry.getType().name());
      xtw.writeAttribute("path", buildpathEntry.getPath());
      if (buildpathEntry.getSourcePath() != null)
        xtw.writeAttribute("sourcepath", buildpathEntry.getSourcePath());
    }

    xtw.writeEndElement();

    xtw.flush();
    xtw.close();
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
  public static String parse(File dotBuildpath,
      BuildpathEntry.Kind buildpathKind, String defaultVal) {
    BuildpathContentHandler contentHandler =
        getContentHandler(dotBuildpath, buildpathKind);
    if (contentHandler != null)
      return contentHandler.getBuildpathString();
    else return defaultVal;
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
  public static String parse(File dotBuildpath, String defaultVal) {
    return parse(dotBuildpath, BuildpathEntry.CLASSPATH, defaultVal);
  }

  /**
   * Reads a list of {@code BuildpathEntry}s for the main classpath from a
   * {@code .buildpath} file.
   *
   * @param dotBuildpath
   *          the file to parse
   */
  public static List<BuildpathEntry> getClasspathEntries(File dotBuildpath) {
    return getBuildpathEntries(dotBuildpath, BuildpathEntry.CLASSPATH);
  }

  /**
   * Reads a list of {@code BuildpathEntry}s from a {@code .buildpath} file.
   *
   * @param dotBuildpath
   *          the file to parse
   * @param buildpathKind
   *          the kind of build path to read
   */
  public static List<BuildpathEntry> getBuildpathEntries(File dotBuildpath,
      BuildpathEntry.Kind buildpathKind) {
    BuildpathContentHandler contentHandler =
        getContentHandler(dotBuildpath, buildpathKind);
    if (contentHandler != null)
      return contentHandler.getBuildpathEntries();
    else return new ArrayList<>();
  }

  private static BuildpathContentHandler getContentHandler(File dotBuildpath,
      BuildpathEntry.Kind buildpathKind) {
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      XMLReader parser = spf.newSAXParser().getXMLReader();
      BuildpathContentHandler buildpathContentHandler =
          new BuildpathContentHandler(dotBuildpath, buildpathKind);
      parser.setContentHandler(buildpathContentHandler);
      parser.parse(dotBuildpath.toString());
      return buildpathContentHandler;
    } catch (Exception e) {
      ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
          "Error parsing dot-buildpath file", e.getCause(), Style.BLOCK);
      return null;
    }
  }

  private static class BuildpathContentHandler extends DefaultHandler {
    StringBuilder buf;
    File dotBuildpath;
    List<BuildpathEntry> buildpathEntries;
    BuildpathEntry.Kind buildpathKind;

    BuildpathContentHandler(File dotBuildpath, BuildpathEntry.Kind buildpathKind) {
      buf = new StringBuilder();
      this.dotBuildpath = dotBuildpath;
      this.buildpathEntries = new ArrayList<>();
      this.buildpathKind = buildpathKind;
    }

    public String getBuildpathString() {
      return buf.toString();
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
      if (!buildpathKind.name().equals(kind)) return;

      String type = atts.getValue("type");
      if (type == null) return;

      String path = atts.getValue("path");
      if (path == null || path.isEmpty()) return;

      BuildpathEntry.Type buildpathEntryType = BuildpathEntry.Type.get(type);

      if (buildpathEntryType == BuildpathEntry.CON) {
        if (buf.length() != 0) buf.append(File.pathSeparator);

        buf.append(path);
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

      if (buf.length() != 0) buf.append(File.pathSeparator);

      buf.append(absolutize(baseDir, path).toString());

      if (buildpathEntryType == BuildpathEntry.LIB)
        buildpathEntries.add(new BuildpathEntry(buildpathEntryType, path));
    }
  }
}
