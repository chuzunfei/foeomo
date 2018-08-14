import java.applet.Applet;  
import java.awt.BorderLayout;  
//import com.sun.j3d.utils.applet.MainFrame;  
import com.sun.j3d.utils.geometry.Cone;  
import com.sun.j3d.utils.universe.*;  
import javax.media.j3d.*;  
import javax.vecmath.*;  
  
public class SimpleCone extends Applet{  
  
  public BranchGroup createSceneGraph() {   
    BranchGroup objRoot = new BranchGroup();  
  
    TransformGroup objTrans = new TransformGroup();  
    objRoot.addChild(objTrans);  
  
    Appearance app = new Appearance();  
    Material material = new Material();  
    material.setEmissiveColor(new Color3f(1.0f,0.0f,0.0f));  
    app.setMaterial(material);  
    Cone cone=new Cone(.5f,1.0f,1,app);  
    objTrans.addChild(cone);  
  
    objRoot.compile();  
        return objRoot;  
    }  
  
    public SimpleCone() {  
        setLayout(new BorderLayout());  
        Canvas3D c = new Canvas3D(null);  
        add("Center", c);  
        BranchGroup scene = createSceneGraph();  
        SimpleUniverse u = new SimpleUniverse(c);  
        u.getViewingPlatform().setNominalViewingTransform();  
        u.addBranchGraph(scene);  
    }  
  
//    public static void main(String[] args) {  
//        new MainFrame(new SimpleCone(), 256, 256);  
//    }  
}