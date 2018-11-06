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
import java.util.List;
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
//		new Example().makeQuat4f(0, Math.PI/2, 0);
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
		scaleTrans.set(1 / 16.5f);
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
	 	        15f,0.0f,0.0f,  
	 	        0.0f,0.0f,0.0f,  
	 	        0.0f,15.0f,0.0f,  
	 	        0.0f,0.0f,0.0f,  
	 	        0.0f,0.0f,15.0f,  
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
		point.setZ((float) 0.5);
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

		return generateCylinder(line.getEndpoints());
	}

	public TransformGroup generateCylinder(List<Point> pList) {
		TransformGroup tg = new TransformGroup();
		tg.addChild(addAixs());
		Material material = new Material(red, black, red, white, 64);
		Appearance appearance = new Appearance();
		appearance.setMaterial(material);
		for (int i=1;i<pList.size();i++) {
			Point p1 = pList.get(i-1);
			Point p2 = pList.get(i);
			double len = Point.distance(p1, p2);
			
			TransformGroup cg = new TransformGroup();
			cg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			cg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			
			tmpVector.set((float) p1.getX(), (float) p1.getY(), (float) p1.getZ());
			tmpTrans.set(tmpVector);
			cg.setTransform(tmpTrans);

			tmpTG = new TransformGroup();
			tmpVector.set(0, (float) len/2, 0);
			tmpTrans.set(tmpVector);
//			tmpTrans.setScale(0.1);
			tmpTG.setTransform(tmpTrans);
			tmpCyl = new Cylinder(0.1f, (float) len, appearance);
			tmpTG.addChild(tmpCyl);
			cg.addChild(tmpTG);

			cg.getTransform(tmpTrans);
			tmpTrans.setRotation(makeAA(p1, p2, len));
			cg.setTransform(tmpTrans);

			tg.addChild(cg);
		}

		return tg;
	}
	
	/*
	 *  x = sin(Y/2)sin(Z/2)cos(X/2)+cos(Y/2)cos(Z/2)sin(X/2)
		y = sin(Y/2)cos(Z/2)cos(X/2)+cos(Y/2)sin(Z/2)sin(X/2)
		z = cos(Y/2)sin(Z/2)cos(X/2)-sin(Y/2)cos(Z/2)sin(X/2)
		w = cos(Y/2)cos(Z/2)cos(X/2)-sin(Y/2)sin(Z/2)sin(X/2)
		q = ((x, y, z), w)
	 */
	private Quat4f makeQuat4f(double anglex,double angley,double anglez) {
		float x = (float) (Math.sin(angley/2)*Math.sin(anglez/2)*Math.cos(anglex/2) + Math.cos(angley/2)*Math.cos(anglez/2)*Math.sin(anglex/2));
		float y = (float) (Math.sin(angley/2)*Math.cos(anglez/2)*Math.cos(anglex/2) + Math.cos(angley/2)*Math.sin(anglez/2)*Math.sin(anglex/2));
		float z = (float) (Math.cos(angley/2)*Math.sin(anglez/2)*Math.cos(anglex/2) - Math.sin(angley/2)*Math.cos(anglez/2)*Math.sin(anglex/2));
		float w = (float) (Math.cos(angley/2)*Math.cos(anglez/2)*Math.cos(anglex/2) - Math.sin(angley/2)*Math.sin(anglez/2)*Math.sin(anglex/2));
		System.out.println(x);
		System.out.println(y);
		System.out.println(z);
		System.out.println(w);
		return new Quat4f(x,y,z,w);
	}
	
	private AxisAngle4d makeAA(Point s, Point d, double len) {
		AxisAngle4d aa = new AxisAngle4d();
		if (len == 0) {
			len = Math.sqrt(Math.pow(d.getX()-s.getX(), 2) + Math.pow(d.getY()-s.getY(), 2) + Math.pow(d.getZ()-s.getZ(), 2));
		}
		Vector3d vs = new Vector3d(0, len, 0);
		Vector3d vd = new Vector3d(d.getX()-s.getX(), d.getY()-s.getY(), d.getZ()-s.getZ());
		
		Vector3d vr = new Vector3d();
		vr.cross(vs, vd);
		double angle = Math.acos(vs.dot(vd)/(vs.length()*vd.length()));
		
		aa.set(vr, angle);
		
		return aa;
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