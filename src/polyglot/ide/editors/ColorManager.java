package polyglot.ide.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Maintains a map for interning Color resources for given RGB values.
 */
public class ColorManager {
  public static final RGB COMMENT_COLOR = new RGB(63, 127, 95);
  public static final RGB KEYWORD_COLOR = new RGB(127, 0, 85);
  public static final RGB STRING_COLOR = new RGB(42, 0, 255);
  public static final RGB DEFAULT_COLOR = new RGB(0, 0, 0);
  
  protected Map<RGB, Color> colorTable = new HashMap<>(10);
  
  public void dispose() {
    for (Color c : colorTable.values()) c.dispose();
  }
  
  public Color getColor(RGB rgb) {
    Color color = colorTable.get(rgb);
    if (color == null) {
      color = new Color(Display.getCurrent(), rgb);
      colorTable.put(rgb, color);
    }
    
    return color;
  }
}
