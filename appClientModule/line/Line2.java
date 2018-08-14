package line;

import java.applet.Applet;  
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import com.sun.j3d.utils.applet.MainFrame;  
import com.sun.j3d.utils.universe.*;  
import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.*;

import org.fem.object.Line;
import org.fem.object.Point;  
  
public class Line2 extends Applet {  
  
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
  
        Shape3D shape = new lineShape2();  
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
  
    public Line2() {  
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
//        new MainFrame(new Line2(), 400,400);  
    	JFrame frame = new JFrame();
    	frame.setSize(600, 400);
    	JPanel panel = new JPanel();
    	panel.add(new Line2());
    	
    	 GraphicsConfiguration config =
    	           SimpleUniverse.getPreferredConfiguration();
    	Canvas3D jp = new Canvas3D(config);
		SimpleUniverse u = new SimpleUniverse(jp);  
        u.getViewingPlatform().setNominalViewingTransform();  
        Line line = new Line();
		line.setEndpoints(new ArrayList<Point>());
		for (int i=0;i<3;i++) {
			Point point = new Point();
			point.setX(1+i);
			point.setY(15+i*2);
			point.setZ(21+i*2);
			line.getEndpoints().add(point);
		}
		BranchGroup scene = createSceneGraph(line);  
        u.addBranchGraph(scene); 
        
    	frame.add(jp);
    	frame.setVisible(true);
    }  
    
    
    
    
    
    private static BranchGroup createSceneGraph(Line line) {  
        BranchGroup objRoot = new BranchGroup();  
        objRoot.addChild(createObject(line));  
        objRoot.compile();  
        return objRoot;  
    }  
  
    private static Group createObject(Line line) {  
        Transform3D t = new Transform3D();  
        TransformGroup objTrans = new TransformGroup(t);  
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);  
  

      LineAttributes la = new LineAttributes();  
        la.setLineWidth(30.0f);  
        la.setLineAntialiasingEnable(true);  
        la.setLinePattern(LineAttributes.PATTERN_DASH);  
      Appearance appearance = new Appearance();  
      appearance.setLineAttributes(la);  
		Shape3D shape = new Shape3D(line.generateLineShape(), appearance);  
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
}
