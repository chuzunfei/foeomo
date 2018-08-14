
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.*;
 
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.image.TextureLoader;
 
public class KnotHuanDemo extends Applet  {
 
   public BranchGroup createSceneGraph() {
 
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f blue   = new Color3f(0.00f, 0.20f, 0.80f);
 
        Color3f ambientBlue = new Color3f(0.0f, 0.02f, 0.5f);
        Color3f ambient = new Color3f(0.2f, 0.2f, 0.2f);
        Color3f diffuse = new Color3f(0.7f, 0.7f, 0.7f);
        Color3f specular = new Color3f(0.7f, 0.7f, 0.7f);
 
 
        Color3f ambientRed = new Color3f(1.0f, 0.0f, 0.0f);
        Color3f red = new Color3f(0.9f, 0.05f, 0.0f);
 
        BranchGroup branchGroup = new BranchGroup();
 
        BoundingSphere bounds =
           new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
        BoundingLeaf boundingLeaf = new BoundingLeaf(bounds);
        branchGroup.addChild(boundingLeaf);
 
        Background bg = new Background(black);
        bg.setApplicationBounds(bounds);
        branchGroup.addChild(bg);
       
        AmbientLight ambLight = new AmbientLight(white);
        ambLight.setInfluencingBounds(bounds);
        branchGroup.addChild(ambLight);
 
        Vector3f dir = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight dirLight = new DirectionalLight(white, dir);
        dirLight.setInfluencingBounds(bounds);
        branchGroup.addChild(dirLight);
    
        Material blueMaterial =
           new Material(ambientBlue, black, blue, specular, 75.0f);
        blueMaterial.setLightingEnable(true);
 
        Material redMaterial =
           new Material(ambientRed, black, red, specular, 75.0f);
        redMaterial.setLightingEnable(true);
 
        Appearance blueAppearance1 = new Appearance();
        blueAppearance1.setMaterial(blueMaterial);
               
        blueAppearance1.setTransparencyAttributes(
              new TransparencyAttributes(TransparencyAttributes.NICEST, 0.5f));
        PolygonAttributes pa1 = new PolygonAttributes();
        pa1.setPolygonMode(pa1.POLYGON_FILL);
        pa1.setCullFace(pa1.CULL_NONE);
        blueAppearance1.setPolygonAttributes(pa1);
 
        Appearance blueAppearance2 = new Appearance();
        blueAppearance2.setMaterial(blueMaterial);
 
        PolygonAttributes pa2 = new PolygonAttributes();
        pa2.setPolygonMode(pa2.POLYGON_LINE);
        pa2.setCullFace(pa2.CULL_NONE);
        blueAppearance2.setPolygonAttributes(pa2);
 
        Appearance redAppearance = new Appearance();
        redAppearance.setMaterial(redMaterial);
        PolygonAttributes pa21 = new PolygonAttributes();
        pa21.setPolygonMode(pa2.POLYGON_LINE);
        pa21.setCullFace(pa2.CULL_NONE);
        redAppearance.setPolygonAttributes(pa21);
 
        Appearance blueAppearance3 = new Appearance();
        blueAppearance3.setMaterial(blueMaterial);
 
        PolygonAttributes pa3 = new PolygonAttributes();
        pa3.setPolygonMode(pa3.POLYGON_FILL);
        pa3.setCullFace(pa3.CULL_NONE);
        blueAppearance3.setPolygonAttributes(pa3);
 
        Transform3D t = new Transform3D();
        t.set(1, new Vector3d(0.0, 0.0, 0.0));
 
        TransformGroup transformGroup = new TransformGroup(t);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
 
//        Shape3D S = new KnotHuan( 0.0f,0.0f,0.0f,blueAppearance1,true).getChild();
//        transformGroup.addChild( S );
//        Shape3D SW = new KnotHuan( 0.0f,0.0f,0.0f,redAppearance,true).getChild();
//        transformGroup.addChild( SW );
 
        branchGroup.addChild(transformGroup);
       
        MouseRotate behavior = new MouseRotate();
        behavior.setTransformGroup(transformGroup);
        transformGroup.addChild(behavior);
        behavior.setSchedulingBounds(bounds);
       
        MouseZoom behavior2 = new MouseZoom();
        behavior2.setTransformGroup(transformGroup);
        transformGroup.addChild(behavior2);
        behavior2.setSchedulingBounds(bounds);
 
        MouseTranslate behavior3 = new MouseTranslate();
        behavior3.setTransformGroup(transformGroup);
        transformGroup.addChild(behavior3);
        behavior3.setSchedulingBounds(bounds);
 
        return branchGroup;
    }
   
    public KnotHuanDemo() {
                setLayout(new BorderLayout());
                GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
                Canvas3D canvas = new Canvas3D(gc);
                add("Center", canvas);
                BranchGroup scene = createSceneGraph();
 
                SimpleUniverse u = new SimpleUniverse( canvas );
                //UniverseBuilder u = new UniverseBuilder(canvas);
                u.addBranchGraph(scene);
    }
 
    static class killAdapter extends WindowAdapter {
       public void windowClosing(WindowEvent event) {
          System.exit(0);
       }
    }
 
    public static void main(String[] args) {
 
       KnotHuanDemo SampleShape = new KnotHuanDemo();
 
       Frame frame = new Frame("SampleShape1 quad Test");
       frame.setSize(400, 400);
       frame.add("Center", SampleShape);
       Label text2 = new Label(" Left mouse button to rotate ");
       text2.setBackground(Color.black);
       text2.setForeground(Color.yellow);
       frame.add("South", text2);
       frame.addWindowListener(new killAdapter());
       frame.show();
    }
}