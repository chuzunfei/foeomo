// Defines application wide constants
package Constants;
import java.awt.Color;
import javax.swing.*;

public class SketcherConstants {
  // Path for images
  public final static String imagePath = "E:/JavaProject/BeginningJava/Images/";

  // Toolbar icons
  public final static Icon NEW24 = new ImageIcon(imagePath + "New24.gif");
  public final static Icon OPEN24 = new ImageIcon(imagePath + "Open24.gif");
  public final static Icon SAVE24 = new ImageIcon(imagePath + "Save24.gif");
  public final static Icon SAVEAS24 = new ImageIcon(imagePath + "SaveAs24.gif");
  public final static Icon PRINT24 = new ImageIcon(imagePath + "Print24.gif");

  public final static Icon LINE24 = new ImageIcon(imagePath + "Line24.gif");
  public final static Icon RECTANGLE24 = new ImageIcon(imagePath + "Rectangle24.gif");
  public final static Icon CIRCLE24 = new ImageIcon(imagePath + "Circle24.gif");
  public final static Icon CURVE24 = new ImageIcon(imagePath + "Curve24.gif");

  public final static Icon RED24 = new ImageIcon(imagePath + "Red24.gif");
  public final static Icon GREEN24 = new ImageIcon(imagePath + "Green24.gif");
  public final static Icon BLUE24 = new ImageIcon(imagePath + "Blue24.gif");
  public final static Icon YELLOW24 = new ImageIcon(imagePath + "Yellow24.gif");

  // Menu item icons
  public final static Icon NEW16 = new ImageIcon(imagePath + "new16.gif");
  public final static Icon OPEN16 = new ImageIcon(imagePath + "Open16.gif");
  public final static Icon SAVE16 = new ImageIcon(imagePath + "Save16.gif");
  public final static Icon SAVEAS16 = new ImageIcon(imagePath + "SaveAs16.gif");
  public final static Icon PRINT16 = new ImageIcon(imagePath + "print16.gif");

  public final static Icon LINE16 = new ImageIcon(imagePath + "Line16.gif");
  public final static Icon RECTANGLE16 = new ImageIcon(imagePath + "Rectangle16.gif");
  public final static Icon CIRCLE16 = new ImageIcon(imagePath + "Circle16.gif");
  public final static Icon CURVE16 = new ImageIcon(imagePath + "Curve16.gif");

  public final static Icon RED16 = new ImageIcon(imagePath + "Red16.gif");
  public final static Icon GREEN16 = new ImageIcon(imagePath + "Green16.gif");
  public final static Icon BLUE16 = new ImageIcon(imagePath + "Blue16.gif");
  public final static Icon YELLOW16 = new ImageIcon(imagePath + "Yellow16.gif");

  // Element type definitions
  public final static int LINE      = 101;
  public final static int RECTANGLE = 102;
  public final static int CIRCLE    = 103;
  public final static int CURVE     = 104;

  // Initial conditions
  public final static int DEFAULT_ELEMENT_TYPE = LINE;
  public final static Color DEFAULT_ELEMENT_COLOR = Color.BLUE;
}