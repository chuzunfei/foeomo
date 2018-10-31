import java.applet.Applet;
import com.sun.j3d.utils.applet.MainFrame;
import javax.swing.event.*;
import java.awt.event.*;
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

public class Example extends Applet implements ChangeListener, ActionListener {
	public static void main(String[] args) {
		float initOffScreenScale = 2.5f;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-s")) {
				if (args.length >= (i + 1)) {
					initOffScreenScale = Float.parseFloat(args[i + 1]);
					i++;
				}
			}
		}
		new MainFrame(new Example(true, initOffScreenScale), 950, 600);
	}

	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		int value = source.getValue();
		if (source == rShoulderSlider) {
			setRShoulderRot(value);
			rShoulderSliderLabel.setText(Integer.toString(value));
		} else if (source == rElbowSlider) {
			setRElbowRot(value);
			rElbowSliderLabel.setText(Integer.toString(value));
		} else if (source == lShoulderSlider) {
			setLShoulderRot(value);
			lShoulderSliderLabel.setText(Integer.toString(value));
		} else if (source == lElbowSlider) {
			setLElbowRot(value);
			lElbowSliderLabel.setText(Integer.toString(value));
		}
	}

	TransformGroup Human_l_shoulder;
	TransformGroup Human_l_elbow;
	TransformGroup Human_skullbase;

	AxisAngle4f rElbowAA = new AxisAngle4f(0.0f, 0.0f, -1.0f, 0.0f);
	AxisAngle4f lShoulderAA = new AxisAngle4f(0.0f, 0.0f, 1.0f, 0.0f);
	AxisAngle4f lElbowAA = new AxisAngle4f(0.0f, 0.0f, 1.0f, 0.0f);

	public void setRElbowRot(int rotation) {
		float angle = (float) Math.toRadians(rotation);
		rElbowRot = rotation;
		rElbowAA.angle = (float) Math.toRadians(rElbowRot);
		Human_r_elbow.getTransform(tmpTrans);
		tmpTrans.setRotation(rElbowAA);
		Human_r_elbow.setTransform(tmpTrans);
	}

	public void setLShoulderRot(int rotation) {
		lShoulderRot = rotation;
//		lShoulderAA.angle = (float) Math.toRadians(lShoulderRot);
		lShoulderAA.setAngle((float) Math.toRadians(lShoulderRot));
		Human_l_shoulder.getTransform(tmpTrans);
		tmpTrans.setRotation(lShoulderAA);
		Human_l_shoulder.setTransform(tmpTrans);
	}

	public void setLElbowRot(int rotation) {
		float angle = (float) Math.toRadians(rotation);
		lElbowRot = rotation;
		lElbowAA.angle = (float) Math.toRadians(lElbowRot);
		Human_l_elbow.getTransform(tmpTrans);
		tmpTrans.setRotation(lElbowAA);
		Human_l_elbow.setTransform(tmpTrans);
	}

	public void setRShoulderRot(int rotation) {
		rShoulderRot = rotation;
		rShoulderAA.angle = (float) Math.toRadians(rShoulderRot);
		Human_r_shoulder.getTransform(tmpTrans);
		tmpTrans.setRotation(rShoulderAA);
		Human_r_shoulder.setTransform(tmpTrans);
	}

	AxisAngle4f rShoulderAA = new AxisAngle4f(0.0f, 0.0f, -1.0f, 0.0f);

	public void actionPerformed(ActionEvent e) {
		System.out.println("action performed has been called...");
	}

	boolean isApplication;
	float offScreenScale = 1.5f;

	public Example(boolean isApplication, float initOffScreenScale) {
		this.isApplication = isApplication;
		this.offScreenScale = offScreenScale;
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
		add("East", guiPanel());
	}

	View view;

	JPanel guiPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.add(new JLabel("Right Shoulder rotation"));
		rShoulderSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, rShoulderRot);
		rShoulderSlider.addChangeListener(this); // we somehow skipped this the first time around...?!
		rShoulderSliderLabel = new JLabel(Integer.toString(rShoulderRot));
		panel.add(rShoulderSlider); // this was for the right shoulder rotation
		panel.add(rShoulderSliderLabel); // this was for the right shoulder rotation
		// Human_r_elbow rotation
		panel.add(new JLabel("Right Elbow rotation"));
		rElbowSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, rElbowRot);
		rElbowSlider.addChangeListener(this);
		rElbowSliderLabel = new JLabel(Integer.toString(rElbowRot));
		panel.add(rElbowSlider);
		panel.add(rElbowSliderLabel);
		// Human_l_shoulder rotation
		panel.add(new JLabel("Left Shoulder rotation"));
		lShoulderSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, lShoulderRot);
		lShoulderSlider.addChangeListener(this);
		lShoulderSliderLabel = new JLabel(Integer.toString(lShoulderRot));
		panel.add(lShoulderSlider);
		panel.add(lShoulderSliderLabel);
		// Human_l_elbow rotation
		panel.add(new JLabel("Left Elbow rotation"));
		lElbowSlider = new JSlider(JSlider.HORIZONTAL, 0, 180, lElbowRot);
		lElbowSlider.addChangeListener(this);
		lElbowSliderLabel = new JLabel(Integer.toString(lElbowRot));
		panel.add(lElbowSlider);
		panel.add(lElbowSliderLabel);
		if (isApplication) {
			JButton snapButton = new JButton(snapImageString);
			snapButton.setActionCommand(snapImageString);
			snapButton.addActionListener(this);
			panel.add(snapButton);
		}
		return panel;
	}

	String snapImageString = "Snap Image";
	int rShoulderRot = 0;
	JSlider rShoulderSlider;
	JLabel rShoulderSliderLabel;
	int lShoulderRot = 0;
	JSlider lShoulderSlider;
	JLabel lShoulderSliderLabel;
	int rElbowRot = 0;
	JSlider rElbowSlider;
	JLabel rElbowSliderLabel;
	int lElbowRot = 0;
	JSlider lElbowSlider;
	JLabel lElbowSliderLabel;

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
//		createHuman();
//		objTrans.addChild(Human_body);
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
		tmpVector.set(0.0f, -0.5f, 0.0f);
		tmpTrans.set(tmpVector);
		tmpTG.setTransform(tmpTrans);
		tmpCyl = new Cylinder(0.2f, 1.0f, appearance);
		tmpTG.addChild(tmpCyl);
		
		
		// add the shape to the l_shoulder
		cg1.addChild(tmpTG);
		AxisAngle4f a1 = new AxisAngle4f(0.0f, 0.0f, 1.0f, 0.0f);
		a1.setAngle((float) Math.toRadians(45));
		cg1.getTransform(tmpTrans);
//		tmpTrans.setRotation(a1);
		cg1.setTransform(tmpTrans);
		 
    	 tg.addChild(cg1);
    	 
    	 TransformGroup cg2 = new TransformGroup();
		 Cylinder cy2 = new Cylinder(0.1f, (float)Math.sqrt(1+1+1) );
//    	 verts[i] = new Point3f( endpoints.get(i).getX(),  endpoints.get(i).getY(),  endpoints.get(i).getZ());
		 Transform3D t2 = new Transform3D();
         t2.rotZ(Math.atan(-3/2 ));
//         t2.rotX(Math.atan( (endpoints.get(i).getZ()-endpoints.get(i-1).getZ())/(endpoints.get(i).getY()-endpoints.get(i-1).getY()) ));
         Vector3f vec2 = new Vector3f(1,(float) (1+Math.sqrt(3)/2),1);
         Transform3D offset2 = new Transform3D();
         offset2.setTranslation(vec2);
         t2.mul(offset2);
         cg2.setTransform(offset2 );
		 cg2.addChild(cy2);
		 
    	 tg.addChild(cg2);
		
		return tg;
	}
	
	TransformGroup Human_body;
	Vector3f tmpVector = new Vector3f();
	Transform3D tmpTrans = new Transform3D();
	Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	TransformGroup tmpTG;
	TransformGroup Human_r_shoulder;
	Cylinder tmpCyl;
	Sphere tmpSphere;

	void createHuman() {
		Human_body = new TransformGroup();
		tmpVector.set(0.0f, -1.5f, 0.0f);
		tmpTrans.set(tmpVector);
//		Human_body.setTransform(tmpTrans);
		Material material = new Material(red, black, red, white, 64);
		Appearance appearance = new Appearance();
		appearance.setMaterial(material);
		tmpTG = new TransformGroup();
//		tmpVector.set(0.0f, 1.5f, 0.0f);
//		tmpTrans.set(tmpVector);
//		tmpTG.setTransform(tmpTrans);
//		tmpCyl = new Cylinder(0.75f, 3.0f, appearance);
//		tmpTG.addChild(tmpCyl);
//		Human_body.addChild(tmpTG);
		Human_r_shoulder = new TransformGroup();
		Human_r_shoulder.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		Human_r_shoulder.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tmpVector.set(-0.95f, 2.9f, -0.2f);
		tmpTrans.set(tmpVector);
		Human_r_shoulder.setTransform(tmpTrans);
		tmpSphere = new Sphere(0.22f, appearance);
		Human_r_shoulder.addChild(tmpSphere);
		tmpTG = new TransformGroup();
		tmpVector.set(0.0f, -0.5f, 0.0f);
		tmpTrans.set(tmpVector);
		tmpTG.setTransform(tmpTrans);
		tmpCyl = new Cylinder(0.2f, 1.0f, appearance);
		tmpTG.addChild(tmpCyl);
		Human_r_shoulder.addChild(tmpTG);
//		Human_body.addChild(Human_r_shoulder);

		// create the r_elbow TransformGroup
		Human_r_elbow = new TransformGroup();
		Human_r_elbow.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		Human_r_elbow.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tmpVector.set(0.0f, -1.054f, 0.0f);
		tmpTrans.set(tmpVector);
		Human_r_elbow.setTransform(tmpTrans);

		// place the sphere for the r_elbow
		tmpSphere = new Sphere(0.22f, appearance);
		Human_r_elbow.addChild(tmpSphere);

		// offset and place the cylinder for the r_shoulder
		tmpTG = new TransformGroup();
		// offset the shape
		tmpVector.set(0.0f, -0.5f, 0.0f);
		tmpTrans.set(tmpVector);
		tmpTG.setTransform(tmpTrans);
		tmpCyl = new Cylinder(0.2f, 1.0f, appearance);
		tmpTG.addChild(tmpCyl);

		// add the shape to the r_shoulder
		Human_r_elbow.addChild(tmpTG);

		// add the elbow to the shoulder group
		Human_r_shoulder.addChild(Human_r_elbow);

		// create the l_shoulder TransformGroup
		Human_l_shoulder = new TransformGroup();
		Human_l_shoulder.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		Human_l_shoulder.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tmpVector.set(1.95f, 2.9f, -0.2f);
		tmpTrans.set(tmpVector);
		Human_l_shoulder.setTransform(tmpTrans);

		// place the sphere for the l_shoulder
		tmpSphere = new Sphere(0.22f, appearance);
//		Human_l_shoulder.addChild(tmpSphere);

		// offset and place the cylinder for the l_shoulder
		tmpTG = new TransformGroup();
		// offset the shape
		tmpVector.set(0.0f, -0.5f, 0.0f);
		tmpTrans.set(tmpVector);
		tmpTG.setTransform(tmpTrans);
		tmpCyl = new Cylinder(0.2f, 1.0f, appearance);
		tmpTG.addChild(tmpCyl);

		// add the shape to the l_shoulder
		Human_l_shoulder.addChild(tmpTG);

		// add the shoulder to the body group
		Human_body.addChild(Human_l_shoulder);

		// create the r_elbow TransformGroup
		Human_l_elbow = new TransformGroup();
		Human_l_elbow.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		Human_l_elbow.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tmpVector.set(0.0f, -1.054f, 0.0f);
		tmpTrans.set(tmpVector);
		Human_l_elbow.setTransform(tmpTrans);

		// place the sphere for the l_elbow
		tmpSphere = new Sphere(0.22f, appearance);
		Human_l_elbow.addChild(tmpSphere);

		// offset and place the cylinder for the l_elbow
		tmpTG = new TransformGroup();
		// offset the shape
		tmpVector.set(0.0f, -0.5f, 0.0f);
		tmpTrans.set(tmpVector);
		tmpTG.setTransform(tmpTrans);
		tmpCyl = new Cylinder(0.2f, 1.0f, appearance);
		tmpTG.addChild(tmpCyl);

		// add the shape to the l_elbow
		Human_l_elbow.addChild(tmpTG);

		// add the shoulder to the body group
//		Human_l_shoulder.addChild(Human_l_elbow);

		// create the skullbase TransformGroup
		Human_skullbase = new TransformGroup();
		tmpVector.set(0.0f, 3.632f, 0.0f);
		tmpTrans.set(tmpVector);
		Human_skullbase.setTransform(tmpTrans);

		// offset and place the sphere for the skull
		tmpSphere = new Sphere(0.5f, appearance);

		// add the shape to the l_shoulder
		Human_skullbase.addChild(tmpSphere);

		// add the shoulder to the body group
//		Human_body.addChild(Human_skullbase);

	}

	TransformGroup Human_r_elbow;

	public void destroy() {
		u.removeAllLocales();
	}
}