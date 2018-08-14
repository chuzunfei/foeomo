import java.applet.Applet;  
import java.awt.BorderLayout;  
import com.sun.j3d.utils.applet.MainFrame;  
import com.sun.j3d.utils.universe.*;  
import javax.media.j3d.*;  
import javax.vecmath.*;  
  
public class Point2 extends Applet {  
  
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
        0.0f,0.8f,2.0f,  
        1.0f,0.0f,0.3f,  
        0.0f,1.0f,0.3f,  
        0.3f,0.8f,0.0f,  
      };  
        Shape3D shape = new Shape3D();  
        PointArray point = new PointArray(6, PointArray.COORDINATES  
                |PointArray.COLOR_3);  
          point.setCoordinates(0,vert);  
          point.setColors(0,color);  
        PointAttributes pa = new PointAttributes();  
          pa.setPointSize(20.0f);  
  
          pa.setPointAntialiasingEnable(true);   
                //不加这一行，点的显示效果为正方形  
                //加了这一行，点的显示效果为圆形  
  
        Appearance ap = new Appearance();  
         ap.setPointAttributes(pa);  
   
        shape.setGeometry(point);  
        shape.setAppearance(ap);  
        objRoot.addChild(shape);  
        objRoot.compile();  
        return objRoot;  
    }  
  
    public Point2() {  
        setLayout(new BorderLayout());  
        Canvas3D c = new Canvas3D(null);  
        add("Center", c);  
        BranchGroup scene = createSceneGraph();  
        SimpleUniverse u = new SimpleUniverse(c);  
        u.getViewingPlatform().setNominalViewingTransform();  
        u.addBranchGraph(scene);  
    }  
  
    public static void main(String[] args) {  
        new MainFrame(new Point2(), 400,400);  
    }  
}