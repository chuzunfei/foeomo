import java.applet.Applet;  
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import com.sun.j3d.utils.applet.MainFrame;  
import com.sun.j3d.utils.universe.*;  
import javax.media.j3d.*;  
import javax.vecmath.*;  
  
public class Point1 extends Applet {  
  
  public BranchGroup createSceneGraph() {  
    BranchGroup objRoot = new BranchGroup();  
  
    float vert[] = {   
        .8f, 0.8f,0.0f,  
        -0.8f, 0.8f,0.0f,  
        0.5f, 0.0f,0.0f,  
        -0.5f, 0.0f,0.0f,  
        -0.8f,-0.8f,0.0f,  
        0.8f,-0.8f,0.0f,  
       };  
  
    float color[] = {  
        0.0f,0.5f,1.0f,  
        0.5f,0.0f,1.0f,  
        0.0f,0.8f,0.2f,  
        1.0f,0.0f,0.3f,  
        0.0f,1.0f,0.3f,  
        0.3f,0.8f,0.0f,  
      };  
        Shape3D shape = new Shape3D();  
        PointArray point = new PointArray(6, PointArray.COORDINATES  
                |PointArray.COLOR_3);  
          point.setCoordinates(0,vert);  
          point.setColors(0,color);  
        shape.setGeometry(point);  
  
        objRoot.addChild(shape);  
        objRoot.compile();  
        return objRoot;  
    }  
  
    public Point1() {  
        setLayout(new BorderLayout());  
        GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
        Canvas3D c = new Canvas3D(gc);  
        add("Center", c);  
        BranchGroup scene = createSceneGraph();  
        SimpleUniverse u = new SimpleUniverse(c);  
        u.getViewingPlatform().setNominalViewingTransform();  
        u.addBranchGraph(scene);  
    }  
  
    public static void main(String[] args) {  
        new MainFrame(new Point1(), 400,400);  
    }  
}