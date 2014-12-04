package polyglot.ide.common;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import polyglot.ide.common.ClasspathEntry.ClasspathEntryKind;
import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

public class ClasspathUtil {
  public static String CLASSPATH_FILE_NAME = ".classpath";

  public static void createClassPathFile(IProject project,
      List<ClasspathEntry> classpathEntries) throws Exception {

    String fullClasspath =
        project.getFile(CLASSPATH_FILE_NAME).getRawLocation().toString();

    XMLOutputFactory xof = XMLOutputFactory.newInstance();
    XMLStreamWriter xtw =
        new IndentingXMLStreamWriter(xof.createXMLStreamWriter(new FileWriter(
            fullClasspath)));

    xtw.writeStartDocument("UTF-8", "1.0");

    xtw.writeStartElement("classpath");

    for (ClasspathEntry classpathEntry : classpathEntries) {
      xtw.writeEmptyElement("classpathentry");

      xtw.writeAttribute("kind", classpathEntry.getKind().name().toLowerCase());
      xtw.writeAttribute("path", classpathEntry.getPath());
      if (classpathEntry.getSourcePath() != null)
        xtw.writeAttribute("sourcepath", classpathEntry.getSourcePath());
    }

    xtw.writeEndElement();

    xtw.flush();
    xtw.close();
  }

  /**
   * Reads a ".classpath" file and turns it into a string formatted to fit the
   * CLASSPATH variable.
   */
  public static String parse(File dotClasspath) {
    ClasspathContentHandler contentHandler = getContentHandler(dotClasspath);
    if (contentHandler != null)
      return contentHandler.getClasspathString();
    else return "";
  }

  public static List<ClasspathEntry> getClasspathEntries(File dotClasspath) {
    ClasspathContentHandler contentHandler = getContentHandler(dotClasspath);
    if (contentHandler != null)
      return contentHandler.getClasspathEntries();
    else return new ArrayList<>();
  }

  private static ClasspathContentHandler getContentHandler(File dotClasspath) {
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      XMLReader parser = spf.newSAXParser().getXMLReader();
      ClasspathContentHandler classpathContentHandler =
          new ClasspathContentHandler(dotClasspath);
      parser.setContentHandler(classpathContentHandler);
      parser.parse(dotClasspath.toString());
      return classpathContentHandler;
    } catch (Exception e) {
      ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
          "Error parsing dot-classpath file", e.getCause(), Style.BLOCK);
      return null;
    }
  }

  private static class ClasspathContentHandler extends DefaultHandler {
    StringBuilder buf;
    File dotClasspath;
    List<ClasspathEntry> classpathEntries;

    ClasspathContentHandler(File dotClasspath) {
      buf = new StringBuilder();
      this.dotClasspath = dotClasspath;
      this.classpathEntries = new ArrayList<>();
    }

    public String getClasspathString() {
      return buf.toString();
    }

    public List<ClasspathEntry> getClasspathEntries() {
      return classpathEntries;
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
      if (!localName.equals("classpathentry")) return; // unknown

      String kind = atts.getValue("kind");
      if (kind == null) return;

      String path = atts.getValue("path");
      if (path == null || path.isEmpty()) return;

      ClasspathEntryKind classpathEntryKind =
          ClasspathEntryKind.valueOf(kind.toUpperCase());

      if (classpathEntryKind.equals(ClasspathEntryKind.CON)) {
        if (buf.length() != 0) buf.append(File.pathSeparator);

        buf.append(path);
        classpathEntries.add(new ClasspathEntry(ClasspathEntryKind.CON, path));
        return;
      }

      File baseDir = null;
      if (classpathEntryKind.equals(ClasspathEntryKind.SRC)
          || classpathEntryKind.equals(ClasspathEntryKind.OUTPUT)) {
        baseDir = dotClasspath.getParentFile().getAbsoluteFile();
      } else if (kind.equalsIgnoreCase(ClasspathEntryKind.LIB.name())) {
        baseDir =
            ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
      }

      if (buf.length() != 0) buf.append(File.pathSeparator);

      buf.append(absolutize(baseDir, path).toString());

      if (classpathEntryKind.equals(ClasspathEntryKind.LIB))
        classpathEntries.add(new ClasspathEntry(classpathEntryKind, path));
    }
  }
}
