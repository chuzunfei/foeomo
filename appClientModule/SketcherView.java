import javax.swing.JComponent;
import java.util.*;
import java.awt.*;

@SuppressWarnings("serial")
public class SketcherView extends JComponent implements Observer {
  public SketcherView(Sketcher theApp) {
    this.theApp = theApp;
  }

  // Method called by Observable object when it changes
  public void update(Observable o, Object rectangle) {
    // Code to respond to changes in the model...
  }

  // Method to draw on the view
  @Override
  public void paint(Graphics g) {
    // Temporary code...
    Graphics2D g2D = (Graphics2D)g;                                    // Get a Java 2D device context

    g2D.setPaint(Color.RED);                                           // Draw in red
    g2D.draw3DRect(50, 50, 150, 100, true);                            // Draw a raised 3D rectangle
    g2D.drawString("A nice 3D rectangle", 60, 100);                    // Draw some text
  }

  private Sketcher theApp;                                             // The application object
}