import java.applet.Applet;  
import java.awt.BorderLayout;  
import com.sun.j3d.utils.applet.MainFrame;  
import com.sun.j3d.utils.universe.*;  
import javax.media.j3d.*;  
import javax.vecmath.*;  
  
public class Point4 extends Applet {  
  
    private BranchGroup createSceneGraph() {  
        BranchGroup objRoot = new BranchGroup();  
        Shape3D shape = new pointShape();  
        objRoot.addChild(shape);  
        objRoot.compile();  
        return objRoot;  
    }  
  
    public Point4() {  
        setLayout(new BorderLayout());  
        Canvas3D c = new Canvas3D(null);  
        add("Center", c);  
        BranchGroup scene = createSceneGraph();  
        SimpleUniverse u = new SimpleUniverse(c);  
        u.getViewingPlatform().setNominalViewingTransform();  
        u.addBranchGraph(scene);  
    }  
  
    public static void main(String[] args) {  
        new MainFrame(new Point4(), 400,400);  
    }  
}