import java.applet.Applet;  
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import com.sun.j3d.utils.applet.MainFrame;  
import com.sun.j3d.utils.universe.*;  
import javax.media.j3d.*;  
import javax.vecmath.*;  
  
public class Point5 extends Applet {  
  
    private BranchGroup createSceneGraph() {  
        BranchGroup objRoot = new BranchGroup();  
        objRoot.addChild(createObject());  
        objRoot.compile();  
        return objRoot;  
    }  
  
    private Group createObject() {  
        Transform3D t = new Transform3D();  
        TransformGroup objTrans = new TransformGroup(t);  
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);  
  
        Shape3D shape = new pointShape();  
        objTrans.addChild(shape);  
  
        Transform3D yAxis = new Transform3D();  
        Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE,  
                                        0, 0,  
                                        4000, 0, 0,  
                                        0, 0, 0);  
        RotationInterpolator rotator =  
            new RotationInterpolator(rotationAlpha, objTrans, yAxis,  
                                     0.0f, (float) Math.PI*2.0f);  
        BoundingSphere bounds =  
            new BoundingSphere(new Point3d(0.0,0.0,0.0), 50.0);  
        rotator.setSchedulingBounds(bounds);  
        objTrans.addChild(rotator);  
          
        return objTrans;  
    }  
  
    public Point5() {  
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
        new MainFrame(new Point5(), 400,400);  
    }  
}