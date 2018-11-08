import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Group;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Material;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.fem.object.Line;
import org.fem.object.Point;

import com.sun.j3d.exp.swing.JCanvas3D;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4788865354490698806L;
	private JPanel contentPane;

	private static BufferedImage offscreenImage, onscreenImage;

	JLabel labelX = new JLabel("0");
	JLabel labelY = new JLabel("0");
	JLabel labelZ = new JLabel("0");
	Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	static GraphicsConfiguration gc = SimpleUniverse.getPreferredConfiguration();
	DrawPanel jp = new DrawPanel();
	SimpleUniverse u = null;
	SimpleUniverse universe = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		// onscreenImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 900);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuFile = new JMenu("文件");
		menuBar.add(menuFile);

		JMenuItem menuFileOpen = new JMenuItem("\u6253\u5F00");
		menuFile.add(menuFileOpen);
		menuFileOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		JMenuItem menuFileImport = new JMenuItem("\u5BFC\u5165");
		menuFile.add(menuFileImport);

		JMenuItem menuFileSave = new JMenuItem("\u4FDD\u5B58");
		menuFile.add(menuFileSave);

		JMenu menuParam = new JMenu("\u53C2\u6570");
		menuBar.add(menuParam);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);

		JRadioButton radioButton = new JRadioButton("选择");
		toolBar.add(radioButton);

		JRadioButton radioButton_1 = new JRadioButton("画图");
		radioButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String inputValue = JOptionPane.showInputDialog(contentPane, "Please input a value");
			}
		});
		toolBar.add(radioButton_1);

		JLabel lblNewLabel_2 = new JLabel("  x:");
		toolBar.add(lblNewLabel_2);

		toolBar.add(labelX);

		JLabel lblNewLabel_3 = new JLabel("  y:");
		toolBar.add(lblNewLabel_3);

		toolBar.add(labelY);

		JLabel lblLablez = new JLabel("  z:");
		toolBar.add(lblLablez);

		toolBar.add(labelZ);

		JButton btnNewButton = new JButton("线");
		Border border = BorderFactory.createLineBorder(Color.RED);
		btnNewButton.setBorder(border);
		Line line = new Line();
		line.setEndpoints(new ArrayList<Point>());
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				Object[] columnNames = { "X", "Y", "Z" };
				TableModel model = new DefaultTableModel(columnNames, 3) {
					public Class<?> getColumnClass(int columnIndex) {
						return Float.class;
					};
				};
				final JTable table = new JTable(model) {
					@Override
					public Dimension getPreferredScrollableViewportSize() {
						Dimension d = getPreferredSize();
						int n = getRowHeight();
						return new Dimension(d.width, (n * 3));
					}
				};

				// table.getModel().addTableModelListener(new MyTableModelListener(table,line));
				JPanel jPanel = new JPanel();
				jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				jPanel.setLayout(new BorderLayout(0, 0));

				jPanel.setLayout(new GridLayout());
				JScrollPane sp = new JScrollPane(table);
				jPanel.add(sp);
				contentPane.add(jPanel);
				JDialog jdialog = new JDialog();
				jdialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jdialog.setContentPane(jPanel);

				jdialog.pack();
				jdialog.setVisible(true);
				jdialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						System.out.println("触发windowClosing事件");
						TableModel model = table.getModel();

						for (int i = 0; i < model.getRowCount(); i++) {
							Point point = new Point();
							for (int j = 0; j < model.getColumnCount(); j++) {
								switch (j) {
								case 0:
									point.setX((Float) model.getValueAt(i, j));
									break;
								case 1:
									point.setY((Float) model.getValueAt(i, j));
									break;
								case 2:
									point.setZ((Float) model.getValueAt(i, j));
									break;
								default:
									break;
								}
							}

							line.getEndpoints().add(point);
						}
					}

					public void windowClosed(WindowEvent e) {
						objRoot.detach();
						objTrans.addChild(generateCylinder(line.getEndpoints()));
//						objRoot.addChild(objTrans);
				      
						universe.addBranchGraph(objRoot);
						JPanel panel = new JPanel();

						// setClosable(true);

//						draw3d(panel, line);
//						contentPane.add(panel, BorderLayout.CENTER);
					}
				});

			}
		});
		btnNewButton.setHorizontalAlignment(SwingConstants.RIGHT);
		toolBar.add(btnNewButton);
//
		JPanel panel = new JPanel();
//
//		// setClosable(true);
//
		
		JCanvas3D canvas = new JCanvas3D(new GraphicsConfigTemplate3D());
		canvas.setResizeMode(JCanvas3D.RESIZE_IMMEDIATELY);
//		setLayout(new BorderLayout());
//		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
//		Canvas3D canvas = new Canvas3D(config);
//		add("Center", canvas);
		Dimension dim = new Dimension(256, 256);
		canvas.setPreferredSize(dim);
		canvas.setSize(dim);
		panel.setLayout(new BorderLayout());
		panel.add(canvas, BorderLayout.CENTER);
		
		BranchGroup objRoot = createSceneGraph(canvas);

		universe = new SimpleUniverse(canvas.getOffscreenCanvas3D()); // TODO: this is awful and must not
																						// be done like that in final
																						// version
		universe.addBranchGraph(objRoot);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		ViewingPlatform viewingPlatform = universe.getViewingPlatform();
		viewingPlatform.setNominalViewingTransform();
		OrbitBehavior orbit = new OrbitBehavior(canvas.getOffscreenCanvas3D(),
				OrbitBehavior.REVERSE_ALL | OrbitBehavior.STOP_ZOOM);
		BoundingSphere bounds2 = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		orbit.setSchedulingBounds(bounds2);
		viewingPlatform.setViewPlatformBehavior(orbit);
		
		contentPane.add(panel, BorderLayout.CENTER);

	}
	
	TransformGroup objTrans = null;
	BranchGroup objRoot = null;
	BranchGroup createSceneGraph(Component comp) {
		
		objRoot = new BranchGroup();
		objRoot.setCapability(BranchGroup.ALLOW_DETACH);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		TransformGroup objScale = new TransformGroup();
		Transform3D scaleTrans = new Transform3D();
		scaleTrans.set(1 / 16.5f);
		objScale.setTransform(scaleTrans);
		objRoot.addChild(objScale);
		objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objScale.addChild(objTrans);
		objTrans.addChild(draw3d());
		BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
		Background bg = new Background(new Color3f(1.0f, 1.0f, 1.0f));
		bg.setApplicationBounds(bounds);
		objTrans.addChild(bg);
		MouseRotate mr = new MouseRotate(comp);
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
          la.setLineWidth(3.0f);  
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
		Vector3f tmpVector = new Vector3f();
		Transform3D tmpTrans = new Transform3D();
		TransformGroup tmpTG = null;
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
			Cylinder tmpCyl = new Cylinder(0.1f, (float) len, appearance);
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
	
	BranchGroup createSceneGraphOld(JCanvas3D canvas) {
		BranchGroup objRoot = new BranchGroup();
		
		
		TransformGroup objTrans = new TransformGroup();
//		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
//		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D t3dTrans = new Transform3D();
		t3dTrans.setTranslation(new Vector3d(0, 0, -1));
		objTrans.setTransform(t3dTrans);

		TransformGroup objRot = new TransformGroup();
		objRot.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.addChild(objRot);
		objRoot.addChild(objTrans);
		
		Shape3D sh = new Shape3D();
		Appearance app = new Appearance();
		Material mm = new Material();
		mm.setLightingEnable(true);
		app.setMaterial(mm);
//		sh.setGeometry(text);
		sh.setAppearance(app);

		LineAttributes la = new LineAttributes();
		la.setLineWidth(30.0f);
		la.setLineAntialiasingEnable(true);
		la.setLinePattern(LineAttributes.PATTERN_DASH);
		Appearance appearance = new Appearance();
		// appearance.setLineAttributes(la);
		appearance.setMaterial(mm);
		
		
		TransformGroup objRot2 = new TransformGroup();
		objRot.addChild(draw3d(new Line()));

		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		// Set up the ambient light
		Color3f ambientColor = new Color3f(0.3f, 0.3f, 0.3f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		objRoot.addChild(ambientLightNode);

		// Set up the directional lights
		Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
		Vector3f light1Direction = new Vector3f(1.0f, 1.0f, 1.0f);
		Color3f light2Color = new Color3f(1.0f, 1.0f, 0.9f);
		Vector3f light2Direction = new Vector3f(-1.0f, -1.0f, -1.0f);

		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		objRoot.addChild(light1);

		DirectionalLight light2 = new DirectionalLight(light2Color, light2Direction);
		light2.setInfluencingBounds(bounds);
		objRoot.addChild(light2);

		boolean isInteractive = true;
		if (true == isInteractive) {
			MouseRotate mr = new MouseRotate(canvas.getOffscreenCanvas3D(),objRot);
//			mr.setTransformGroup(objRot);
			mr.setSchedulingBounds(bounds);
			mr.setSchedulingInterval(1);
			objRoot.addChild(mr);
		}
		objRoot.compile();
		
		return objRoot;
	}
	
	private TransformGroup draw3d(Line line) {
		line.setEndpoints(new ArrayList<>());
		Point point = new Point();
		point.setX(0);
		point.setY((float) 0.5);
		point.setZ(0);
		line.getEndpoints().add(point);
		Point point1 = new Point();
		point1.setX(1);
		point1.setY(1);
		point1.setZ(1);
		line.getEndpoints().add(point1);
		Point point2 = new Point();
		point2.setX(6);
		point2.setY(6);
		point2.setZ(0);
		line.getEndpoints().add(point2);
//		Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		
		
		
		return line.generateCylinder();

		

	}


	class DrawPanel extends Canvas3D implements MouseListener, MouseMotionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5634646508547740038L;
		Vector<Rectangle> lines = new Vector<Rectangle>();
		Vector<Rectangle> ellipses = new Vector<Rectangle>();
		Vector<Rectangle> rectangles = new Vector<Rectangle>();
		int x1, y1;
		int x2, y2;
		int end = -1;
		// 当鼠标pressed的时候的变量
		// 直线
		int l1 = 4;// 鼠标到直线两端点的距离 (大于三的任何值一下同是)
		int l2 = 4;
		int mx, my, nx, ny;// 直线端点坐标
		// 圆
		int rx0, ry0, rr0;// 圆心和半径
		int rl1 = 4;// rl1鼠标到圆心的距离，rl2鼠标到圆的边框的距离
		int rl2 = 4;
		// 矩形
		int recx1, recy1, recx2, recy2;// 矩形对角顶点坐标
		int recj1, recj2, recj3, recj4;// 分别对应到四条边的距离
		int recw, rech;
		// 矩形
		int rx1, ry1, rx2, ry2;// 矩形对角顶点坐标
		int j1, j2, j3, j4;// 分别对应到四条边的距离

		public DrawPanel() {
			super(gc);
			setBackground(Color.white);
			addMouseMotionListener(this);
			addMouseListener(this);
		}

		public void removeListener() {
			removeMouseListener(this);
		}

		public void mouseDragged(MouseEvent e) {
			labelX.setText(String.valueOf(e.getX()));
			labelY.setText(String.valueOf(e.getY()));
			labelZ.setText("N/A");
			e.consume();
			end = -1;
			x2 = e.getX();
			y2 = e.getY();
			repaint();
		}

		public void mouseMoved(MouseEvent e) {
			labelX.setText(String.valueOf(e.getX()));
			labelY.setText(String.valueOf(e.getY()));

		}

		public void mousePressed(MouseEvent e) {
			e.consume();
			x1 = e.getX();
			y1 = e.getY();
			x2 = e.getX();
			y2 = e.getY();

		}

		// public void postRender () {
		// J3DGraphics2D graphics = this.getGraphics2D();
		//
		// graphics.drawRenderedImage(onscreenImage, new AffineTransform());
		//
		// graphics.flush(false);
		//
		//
		// Thread.yield();
		// }

		@SuppressWarnings("unchecked")
		public void mouseReleased(MouseEvent e) {
			e.consume();
			end = 0;
			int mode = 0;
			switch (mode) {
			case 0:
				lines.addElement(new Rectangle(x1, y1, e.getX(), e.getY()));
				break;
			case 1:
				double r = Math.sqrt((e.getX() - x1) * (e.getX() - x1) + (e.getY() - y1) * (e.getY() - y1));
				int intr = (int) r;
				ellipses.addElement(new Rectangle(x1 - intr, y1 - intr, 2 * intr, 2 * intr));
				break;
			case 2:
				if ((e.getX() - x1) > 0 && (e.getY() - y1) > 0) {
					rectangles.addElement(new Rectangle(x1, y1, e.getX() - x1, e.getY() - y1));
				} else if ((e.getX() - x1) < 0 && (e.getY() - y1) < 0) {
					rectangles.addElement(
							new Rectangle(e.getX(), e.getY(), Math.abs(e.getX() - x1), Math.abs(e.getY() - y1)));
				} else if ((e.getX() - x1) > 0 && (e.getY() - y1) < 0) {
					rectangles.addElement(new Rectangle(x1, e.getY(), e.getX() - x1, Math.abs(e.getY() - y1)));
				} else if ((e.getX() - x1) < 0 && (e.getY() - y1) > 0) {
					rectangles.addElement(new Rectangle(e.getX(), y1, Math.abs(e.getX() - x1), e.getY() - y1));
				}
				break;
			case 3:
				if (l1 <= l2 && l1 <= 3) {
					lines.addElement(new Rectangle(mx, my, e.getX(), e.getY()));
				} else if (l2 < l1 && l2 <= 3) {
					lines.addElement(new Rectangle(mx, my, e.getX(), e.getY()));
				} else if (rl2 <= rl1 && rl2 <= 3) {
					int rr = (int) Math.sqrt((e.getX() - rx0) * (e.getX() - rx0) + (e.getY() - ry0) * (e.getY() - ry0));
					ellipses.addElement(new Rectangle(rx0 - rr, ry0 - rr, rr * 2, rr * 2));
				} else if (rl1 < rl2 && rl1 <= 3) {
					ellipses.addElement(new Rectangle(e.getX() - rr0, e.getY() - rr0, 2 * rr0, 2 * rr0));
				} else if (recj1 <= 3) {
					if (y1 <= recy2 && y1 >= recy1) {
						if (e.getX() <= recx2) {
							rectangles.addElement(new Rectangle(e.getX(), recy1, recx2 - e.getX(), rech));
						} else {
							rectangles.addElement(new Rectangle(recx2, recy1, e.getX() - recx2, rech));
						}
					}
				} else if (recj2 <= 3) {
					if (y1 <= recy2 && y1 >= recy1) {
						if (e.getX() >= recx1) {
							rectangles.addElement(new Rectangle(recx1, recy1, e.getX() - recx1, rech));
						} else {
							rectangles.addElement(new Rectangle(e.getX(), recy1, recx1 - e.getX(), rech));
						}
					}
				} else if (recj3 <= 3) {
					if (x1 <= recx2 && x1 >= recx1) {
						if (e.getY() <= recy2) {
							rectangles.addElement(new Rectangle(recx1, e.getY(), recw, recy2 - e.getY()));
						} else {
							rectangles.addElement(new Rectangle(recx1, recy2, recw, e.getY() - recy2));
						}
					}
				} else if (recj4 <= 3) {
					if (x1 <= recx2 && x1 >= recx1) {
						if (e.getY() >= recy1) {
							rectangles.addElement(new Rectangle(recx1, recy1, recw, e.getY() - recy1));
						} else {
							rectangles.addElement(new Rectangle(recx1, e.getY(), recw, recy1 - e.getY()));
						}
					}
				}
				break;
			default:
				throw new RuntimeException();
			}
			repaint();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			int mode = 0;
			switch (mode) {
			case 0:
				if (end == -1) {
					g.setColor(Color.red);
					g.drawLine(x1, y1, x2, y2);
				}
				g.setColor(Color.black);
				drawAll(g);
				break;
			case 1:
				if (end == -1) {
					g.setColor(Color.red);
					double r = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
					int intr = (int) r;
					g.drawLine(x1, y1, x2, y2);
					g.drawOval(x1 - intr, y1 - intr, 2 * intr, 2 * intr);
				}
				g.setColor(Color.black);
				drawAll(g);
				break;
			case 2:
				if (end == -1) {
					g.setColor(Color.red);
					if ((x2 - x1) > 0 && (y2 - y1) > 0) {
						g.drawRect(x1, y1, x2 - x1, y2 - y1);
					} else if ((x2 - x1) < 0 && (y2 - y1) < 0) {
						g.drawRect(x2, y2, Math.abs(x2 - x1), Math.abs(y2 - y1));
					} else if ((x2 - x1) > 0 && (y2 - y1) < 0) {
						g.drawRect(x1, y2, x2 - x1, Math.abs(y2 - y1));
					} else if ((x2 - x1) < 0 && (y2 - y1) > 0) {
						g.drawRect(x2, y1, Math.abs(x2 - x1), y2 - y1);
					}
				}
				g.setColor(Color.black);
				drawAll(g);
				break;

			case 3:
				if (l1 <= l2 && l1 <= 3) {
					g.setColor(Color.red);
					g.drawLine(x2, y2, mx, my);
				} else if (l2 < l1 && l2 <= 3) {
					g.setColor(Color.red);
					g.drawLine(x2, y2, mx, my);
				} else if (rl2 <= rl1 && rl2 <= 3) {
					g.setColor(Color.red);
					int rr = (int) Math.sqrt((x2 - rx0) * (x2 - rx0) + (y2 - ry0) * (y2 - ry0));
					g.drawOval(rx0 - rr, ry0 - rr, rr * 2, rr * 2);
				} else if (rl1 < rl2 && rl1 <= 3) {
					g.setColor(Color.red);
					g.drawOval(x2 - rr0, y2 - rr0, 2 * rr0, 2 * rr0);
				} else if (recj1 <= 3) {
					if (y1 <= recy2 && y1 >= recy1) {
						if (x2 <= recx2) {
							g.setColor(Color.red);
							g.drawRect(x2, recy1, recx2 - x2, rech);
						} else {
							g.setColor(Color.red);
							g.drawRect(recx2, recy1, x2 - recx2, rech);
						}
					}

				} else if (recj2 <= 3) {
					if (y1 <= recy2 && y1 >= recy1) {
						if (x2 >= recx1) {
							g.setColor(Color.red);
							g.drawRect(recx1, recy1, x2 - recx1, rech);
						} else {
							g.setColor(Color.red);
							g.drawRect(x2, recy1, recx1 - x2, rech);
						}
					}

				} else if (recj3 <= 3) {
					if (x1 <= recx2 && x1 >= recx1) {
						if (y2 <= recy2) {
							g.setColor(Color.red);
							g.drawRect(recx1, y2, recw, recy2 - y2);
						} else {
							g.setColor(Color.red);
							g.drawRect(recx1, recy2, recw, y2 - recy2);
						}
					}

				} else if (recj4 <= 3) {
					if (x1 <= recx2 && x1 >= recx1) {
						if (y2 >= recy1) {
							g.setColor(Color.red);
							g.drawRect(recx1, recy1, recw, y2 - recy1);
						} else {
							g.setColor(Color.red);
							g.drawRect(recx1, y2, recw, recy1 - y2);
						}
					}

				}
				g.setColor(Color.BLACK);
				drawAll(g);
				break;
			}
		}

		private void drawAll(Graphics g) {
			for (Object r : lines) {
				Rectangle rec = (Rectangle) r;
				g.drawLine(rec.x, rec.y, rec.width, rec.height);
			}
			for (Object r : ellipses) {
				Rectangle rec = (Rectangle) r;
				g.drawOval(rec.x, rec.y, rec.width, rec.height);
			}
			for (Object r : rectangles) {
				Rectangle rec = (Rectangle) r;
				g.drawRect(rec.x, rec.y, rec.width, rec.height);
			}
		}
	}

	class MyTableModelListener implements TableModelListener {
		JTable table;
		Line line;

		MyTableModelListener(JTable table, Line line) {
			this.table = table;
			this.line = line;
		}

		public void tableChanged(TableModelEvent e) {
			int firstRow = e.getFirstRow();
			int lastRow = e.getLastRow();
			int index = e.getColumn();

			switch (e.getType()) {
			case TableModelEvent.INSERT:
				for (int i = firstRow; i <= lastRow; i++) {
					System.out.println(i);
				}
				break;
			case TableModelEvent.UPDATE:

				TableModel model = table.getModel();
				if (firstRow == TableModelEvent.HEADER_ROW) {
					if (index == TableModelEvent.ALL_COLUMNS) {
						System.out.println("A column was added");
					} else {
						System.out.println(index + "in header changed");
					}
				} else {

					for (int i = firstRow; i <= lastRow; i++) {
						Point point = new Point();
						switch (index) {
						case 0:
							point.setX((Float) model.getValueAt(i, index));
							break;
						case 1:
							point.setY((Float) model.getValueAt(i, index));
							break;
						case 2:
							point.setZ((Float) model.getValueAt(i, index));
							break;
						default:
							break;
						}

						line.getEndpoints().add(point);
					}
				}

				break;
			case TableModelEvent.DELETE:
				for (int i = firstRow; i <= lastRow; i++) {
					System.out.println(i);
				}
				break;
			}
		}
	}
}
