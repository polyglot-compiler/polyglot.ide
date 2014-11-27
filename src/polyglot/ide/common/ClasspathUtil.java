package polyglot.ide.common;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.core.resources.IProject;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import polyglot.ide.common.ErrorUtil.Level;
import polyglot.ide.common.ErrorUtil.Style;

import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

public class ClasspathUtil {

	public static String CLASSPATH_FILE_NAME = ".classpath";
	private static final String KIND_VAR = "var";
	private static final Set<String> kinds = new HashSet<>(Arrays.asList("src",
			"con", "lib", "output", KIND_VAR));

	public static void createClassPathFile(IProject project,
			List<ClasspathEntry> classpathEntries) throws Exception {

		String fullClasspath = project.getFile(CLASSPATH_FILE_NAME)
				.getRawLocation().toString();

		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter xtw = new IndentingXMLStreamWriter(
				xof.createXMLStreamWriter(new FileWriter(fullClasspath)));

		xtw.writeStartDocument("UTF-8", "1.0");

		xtw.writeStartElement("classpath");

		for (ClasspathEntry classpathEntry : classpathEntries) {
			xtw.writeEmptyElement("classpathentry");

			xtw.writeAttribute("kind", classpathEntry.getKind().name()
					.toLowerCase());
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
		final StringBuilder buf = new StringBuilder();

		try {
			// all entries in .classpath are relative to this directory.
			final File baseDir = dotClasspath.getParentFile().getAbsoluteFile();

			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			XMLReader parser = spf.newSAXParser().getXMLReader();
			parser.setContentHandler(new DefaultHandler() {
				@Override
				public void startElement(String uri, String localName,
						String qname, Attributes atts) {
					if (!localName.equals("classpathentry"))
						return; // unknown

					String kind = atts.getValue("kind");
					if (kind != null && kinds.contains(kind)) {
						String path = atts.getValue("path");
						if (kind.equals(KIND_VAR)) {
							int i = path.indexOf('/');
							String dir = System.getProperty(path
									.substring(0, i));
							path = dir + '/' + path.substring(i + 1);
						}

						if (buf.length() != 0)
							buf.append(File.pathSeparator);
						buf.append(absolutize(baseDir, path).toString());
					}

					String output = atts.getValue("output");
					if (output != null) {
						if (buf.length() != 0)
							buf.append(File.pathSeparator);
						buf.append(absolutize(baseDir, output).toString());
					}
				}
			});

			parser.parse(dotClasspath.toString());
		} catch (Exception e) {
			ErrorUtil.handleError(Level.WARNING, "polyglot.ide",
					"Error parsing dot-classpath file", e.getCause(),
					Style.BLOCK);
		}

		return buf.toString();
	}

	private static File absolutize(File base, String path) {
		path = path.replace('/', File.separatorChar);
		File child = new File(path);
		if (child.isAbsolute())
			return child;
		else
			return new File(base, path);
	}
}
