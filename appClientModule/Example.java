import java.applet.Applet;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.j3d.utils.applet.MainFrame;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import com.sun.j3d.utils.universe.*;
import java.text.*;
import java.util.ArrayList;
import java.awt.*;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;

import org.fem.object.Line;
import org.fem.object.Point;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.*;

public class Example extends Applet implements ActionListener {
	public static void main(String[] args) {
		new MainFrame(new Example(true, 0), 950, 600);
	}


	public void actionPerformed(ActionEvent e) {
		System.out.println("action performed has been called...");
	}

	boolean isApplication;
	float offScreenScale = 1.5f;

	public Example(boolean isApplication, float initOffScreenScale) {
		this.isApplication = isApplication;
	}

	public Example() {
		this(false, 1.0f);
	}

	SimpleUniverse u;
	NumberFormat nf;
	Canvas3D canvas;
	OffScreenCanvas3D offScreenCanvas;

	public void init() {
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		add("Center", canvas);
		u = new SimpleUniverse(canvas);
		if (isApplication) {
			offScreenCanvas = new OffScreenCanvas3D(config, true);
			Screen3D sOn = canvas.getScreen3D();
			Screen3D sOff = offScreenCanvas.getScreen3D();
			Dimension dim = sOn.getSize();
			dim.width *= offScreenScale;
			dim.height *= offScreenScale;
			sOff.setSize(dim);
			sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth() * offScreenScale);
			sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight() * offScreenScale);
			u.getViewer().getView().addCanvas3D(offScreenCanvas);
		}
		BranchGroup scene = createSceneGraph();
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
		view = u.getViewer().getView();
		
		OrbitBehavior orbit = new OrbitBehavior(offScreenCanvas,
				OrbitBehavior.REVERSE_ALL | OrbitBehavior.STOP_ZOOM);
		BoundingSphere bounds2 = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		orbit.setSchedulingBounds(bounds2);
		u.getViewingPlatform().setViewPlatformBehavior(orbit);
	}

	View view;


	String snapImageString = "Snap Image";

	BranchGroup createSceneGraph() {
		BranchGroup objRoot = new BranchGroup();
		TransformGroup objScale = new TransformGroup();
		Transform3D scaleTrans = new Transform3D();
		scaleTrans.set(1 / 3.5f);
		objScale.setTransform(scaleTrans);
		objRoot.addChild(objScale);
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objScale.addChild(objTrans);
		objTrans.addChild(draw3d());
		BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
		Background bg = new Background(new Color3f(1.0f, 1.0f, 1.0f));
		bg.setApplicationBounds(bounds);
		objTrans.addChild(bg);
		MouseRotate mr = new MouseRotate();
		mr.setTransformGroup(objTrans);
		mr.setSchedulingBounds(bounds);
		mr.setFactor(0.007);
		objTrans.addChild(mr);
		Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		objRoot.addChild(ambientLightNode);
		Color3f light1Color = new Color3f(1.0f, 1.0f, 1.0f);
		Vector3f light1Direction = new Vector3f(0.0f, -0.2f, -1.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		objRoot.addChild(light1);
		return objRoot;
	}
	
	private TransformGroup addAixs() {
		TransformGroup tg = new TransformGroup();
		int StripCount[] = new int[1];  
		  
	    float vert[] = {   
	    		 0.0f,0.0f,0.0f,  
	 	        5f,0.0f,0.0f,  
	 	        0.0f,0.0f,0.0f,  
	 	        0.0f,5.0f,0.0f,  
	 	        0.0f,0.0f,0.0f,  
	 	        0.0f,0.0f,5.0f,  
	       };  
	  
	    float color[] = {  
	    		 0.0f,0.5f,1.0f,  
	    	        0.5f,0.0f,1.0f,  
	    	        0.0f,0.8f,2.0f,  
	    	        1.0f,0.0f,0.3f,  
	    	        0.0f,1.0f,0.3f,  
	    	        0.3f,0.8f,0.0f,  
	      };  
		StripCount[0] = 6;  
		  
        LineStripArray line = new LineStripArray(6,   
                  LineStripArray.COORDINATES|  
                  LineStripArray.COLOR_3,StripCount);  
          line.setCoordinates(0,vert);  
          line.setColors(0,color);  
        LineAttributes la = new LineAttributes();  
          la.setLineWidth(30.0f);  
          la.setLineAntialiasingEnable(true);  
          la.setLinePattern(LineAttributes.PATTERN_SOLID);  
        Appearance ap = new Appearance();  
         ap.setLineAttributes(la);
         
         Shape3D shape = new Shape3D();
  		shape.setGeometry(line);
  		shape.setAppearance(ap);
  		
  		tg.addChild(shape);
  		return tg;
	}
	private TransformGroup draw3d() {
		Line line = new Line();
		line.setEndpoints(new ArrayList<>());
		Point point = new Point();
		point.setX(0);
		point.setY(0);
		point.setZ(0);
		line.getEndpoints().add(point);
		Point point1 = new Point();
		point1.setX(0);
		point1.setY(0);
		point1.setZ(1);
		line.getEndpoints().add(point1);
		Point point2 = new Point();
		point2.setX(2);
		point2.setY(3);
		point2.setZ(1);
		line.getEndpoints().add(point2);

		TransformGroup objTrans = new TransformGroup();
//		Transform3D t3dTrans = new Transform3D();
//		t3dTrans.setTranslation(new Vector3d(0, 0, -1));
//		objTrans.setTransform(t3dTrans);
		
//		objTrans.addChild(generateCylinder());
//		objRot.addChild(line.generateLineShape());
		
		return generateCylinder();
	}
	public TransformGroup generateCylinder() {
		TransformGroup tg = new TransformGroup();
		tg.addChild(addAixs());
		
		 TransformGroup cg1 = new TransformGroup();
//		 cg1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//		 cg1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
//		 Cylinder cy1 = new Cylinder(0.1f, (float)Math.sqrt(3) );
//		//    	 verts[i] = new Point3f( endpoints.get(i).getX(),  endpoints.get(i).getY(),  endpoints.get(i).getZ());
//		 Transform3D t1 = new Transform3D();
//		//         t1.rotZ(Math.atan());
//		 t1.rotX(Math.atan(1/2));
//		 Transform3D offset1 = new Transform3D();
//		 Vector3f vec1 = new Vector3f(0,(float)Math.sqrt(3)/2,0);
//		 offset1.setTranslation(vec1);
//		 cg1.setTransform(offset1);
//		 AxisAngle4f a1 = new AxisAngle4f(0.0f, 0.0f, 1.0f, 0.0f);
//		 a1.setAngle((float) Math.toRadians(45));
//		 cg1.getTransform(offset1);
//		 offset1.setRotation(a1);
//		 cg1.setTransform(offset1);
//		 cg1.addChild(cy1);
		 
		 cg1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		 cg1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tmpVector.set(0f, 0f, 0f);
		tmpTrans.set(tmpVector);
		cg1.setTransform(tmpTrans);
		
		Material material = new Material(red, black, red, white, 64);
		Appearance appearance = new Appearance();
		appearance.setMaterial(material);
		
		// offset and place the cylinder for the l_shoulder
		tmpTG = new TransformGroup();
		// offset the shape
		tmpVector.set(0.0f, (float)Math.sqrt(1+1+1)/2, 0.0f);
		tmpTrans.set(tmpVector);
		tmpTG.setTransform(tmpTrans);
		tmpCyl = new Cylinder(0.1f, (float)Math.sqrt(1+1+1), appearance);
		tmpTG.addChild(tmpCyl);
		
		
		// add the shape to the l_shoulder
		cg1.addChild(tmpTG);
		AxisAngle4f z1 = new AxisAngle4f(0.0f, 0.0f, 1.0f, 0.0f);
		z1.setAngle((float) Math.toRadians(315));
		cg1.getTransform(tmpTrans);
		tmpTrans.setRotation(z1);
		
		AxisAngle4f x1 = new AxisAngle4f(1.0f, 0.0f, .0f, 0.0f);
		x1.setAngle((float) Math.toRadians(36));
		Transform3D t3d = new Transform3D();
		t3d.setRotation(x1);
		tmpTrans.mul(t3d);
		cg1.setTransform(tmpTrans);
		
		
//		cg1.getTransform(tmpTrans);
//		tmpTrans.setRotation(x1);
//		cg1.setTransform(tmpTrans);
		 
    	 tg.addChild(cg1);
    	 
    	 TransformGroup cg2 = new TransformGroup();
		 Cylinder cy2 = new Cylinder(0.1f, (float)Math.sqrt(1+1+1) );
//    	 verts[i] = new Point3f( endpoints.get(i).getX(),  endpoints.get(i).getY(),  endpoints.get(i).getZ());
         Vector3f vec2 = new Vector3f(1,(float) (1+Math.sqrt(3)/2),1);
         Transform3D offset2 = new Transform3D();
         offset2.setTranslation(vec2);
         cg2.setTransform(offset2 );
		 cg2.addChild(cy2);
		 
    	 tg.addChild(cg2);
		
		return tg;
	}
	
	Vector3f tmpVector = new Vector3f();
	Transform3D tmpTrans = new Transform3D();
	Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	TransformGroup tmpTG;
	Cylinder tmpCyl;

	public void destroy() {
		u.removeAllLocales();
	}
	
}

class OffScreenCanvas3D extends Canvas3D {

	  OffScreenCanvas3D(GraphicsConfiguration graphicsConfiguration,
	      boolean offScreen) {

	    super(graphicsConfiguration, offScreen);
	  }

	  private BufferedImage doRender(int width, int height) {

	    BufferedImage bImage = new BufferedImage(width, height,
	        BufferedImage.TYPE_INT_RGB);

	    ImageComponent2D buffer = new ImageComponent2D(
	        ImageComponent.FORMAT_RGB, bImage);
	    //buffer.setYUp(true);

	    setOffScreenBuffer(buffer);
	    renderOffScreenBuffer();
	    waitForOffScreenRendering();
	    bImage = getOffScreenBuffer().getImage();
	    return bImage;
	  }

	  void snapImageFile(String filename, int width, int height) {
	    BufferedImage bImage = doRender(width, height);

	    /*
	     * JAI: RenderedImage fImage = JAI.create("format", bImage,
	     * DataBuffer.TYPE_BYTE); JAI.create("filestore", fImage, filename +
	     * ".tif", "tiff", null);
	     */

	    /* No JAI: */
	    try {
	      FileOutputStream fos = new FileOutputStream(filename + ".jpg");
	      BufferedOutputStream bos = new BufferedOutputStream(fos);

	      JPEGImageEncoder jie = JPEGCodec.createJPEGEncoder(bos);
	      JPEGEncodeParam param = jie.getDefaultJPEGEncodeParam(bImage);
	      param.setQuality(1.0f, true);
	      jie.setJPEGEncodeParam(param);
	      jie.encode(bImage);

	      bos.flush();
	      fos.close();
	    } catch (Exception e) {
	      System.out.println(e);
	    }
	  }
}