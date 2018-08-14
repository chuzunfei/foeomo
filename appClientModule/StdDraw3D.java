import java.io.*;  
import java.net.*;  
import java.awt.*;  
import java.util.*;  
import javax.swing.*;  
import javax.swing.event.*;  
import java.text.*;  
import java.awt.geom.*;  
import java.awt.event.*;  
import java.awt.image.*;  
import javax.imageio.ImageIO;  
   
// Java3D libraries.  
import javax.vecmath.*;  
import javax.media.j3d.*;  
import com.sun.j3d.utils.image.*;  
import com.sun.j3d.utils.geometry.*;  
import com.sun.j3d.utils.universe.*;  
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;  
import com.sun.j3d.utils.scenegraph.io.*;  
import com.sun.j3d.loaders.objectfile.ObjectFile;  
import com.sun.j3d.loaders.lw3d.Lw3dLoader;  
import com.sun.j3d.loaders.Loader;  
   
   
public final class StdDraw3D implements  
MouseListener, MouseMotionListener, MouseWheelListener,  
KeyListener, ActionListener, ChangeListener, ComponentListener, WindowFocusListener  
{  
   
     
   
     
    //-------------------------------------------------------------------------  
   
    // Preset colors.  
    public static final Color BLACK      = Color.BLACK;  
    public static final Color BLUE       = Color.BLUE;  
    public static final Color CYAN       = Color.CYAN;  
    public static final Color DARK_GRAY  = Color.DARK_GRAY;  
    public static final Color GRAY       = Color.GRAY;  
    public static final Color GREEN      = Color.GREEN;  
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;  
    public static final Color MAGENTA    = Color.MAGENTA;  
    public static final Color ORANGE     = Color.ORANGE;  
    public static final Color PINK       = Color.PINK;  
    public static final Color RED        = Color.RED;  
    public static final Color WHITE      = Color.WHITE;  
    public static final Color YELLOW     = Color.YELLOW;  
   
    // Camera modes.  
    public static final int ORBIT_MODE = 0;  
    public static final int FPS_MODE   = 1;  
    public static final int AIRPLANE_MODE = 2;  
    public static final int LOOK_MODE = 3;  
    public static final int FIXED_MODE = 4;  
    public static final int IMMERSIVE_MODE = 5;  
   
     
    //-------------------------------------------------------------------------  
   
    // GUI Components  
    private static JFrame frame;  
    private static Panel canvasPanel;  
    private static JMenuBar menuBar;  
    private static JMenu fileMenu, cameraMenu, graphicsMenu;  
    private static JMenuItem loadButton, saveButton, save3DButton, quitButton;  
    private static JSpinner fovSpinner;  
    private static JRadioButtonMenuItem  
    orbitModeButton, fpsModeButton, airplaneModeButton, lookModeButton, fixedModeButton;  
    private static JRadioButtonMenuItem perspectiveButton, parallelButton;  
    private static JCheckBoxMenuItem antiAliasingButton;  
    private static JSpinner numDivSpinner;  
    private static JCheckBox infoCheckBox;  
   
    // Scene groups.  
    private static SimpleUniverse universe;  
    private static BranchGroup rootGroup, lightGroup, soundGroup, fogGroup, appearanceGroup;  
    private static BranchGroup onscreenGroup, offscreenGroup;  
    private static OrbitBehavior orbit;  
    private static Background background;  
    private static Group bgGroup;  
    private static View view;  
   
    // Drawing canvas.  
    private static Canvas3D canvas;  
   
    // Camera object  
    private static Camera camera;  
   
    // Buffered Images for 2D drawing  
    private static BufferedImage offscreenImage, onscreenImage;  
    private static BufferedImage infoImage;  
   
    // Canvas dimensions.  
    private static int width;  
    private static int height;  
    private static double aspectRatio;  
   
    // Camera mode.  
    private static int cameraMode;  
   
    // Center of orbit  
    private static Point3d orbitCenter;  
   
    // Coordinate bounds.  
    private static double min, max, zoom;  
   
    // Current background color.  
    private static Color bgColor;  
   
    // Pen properties.  
    private static Color penColor;  
    private static float penRadius;  
    private static Font font;  
   
    // Keeps track of screen clearing.  
    private static boolean  clear3D;  
    private static boolean clearOverlay;  
    private static boolean infoDisplay;  
   
    // Number of triangles per shape.  
    private static int numDivisions;  
   
    // Mouse states.  
    private static boolean mouse1;  
    private static boolean mouse2;  
    private static boolean mouse3;  
    private static double mouseX;  
    private static double mouseY;  
   
    // Keyboard states.  
    private static TreeSet<Integer> keysDown = new TreeSet<Integer>();  
    private static LinkedList<Character> keysTyped = new LinkedList<Character>();  
   
    // For event synchronization.  
    private static Object mouseLock = new Object();  
    private static Object keyLock = new Object();  
   
    // Helper to see when initialization complete  
    private static boolean initialized = false;  
    private static boolean fullscreen = false;  
    private static boolean immersive = false;  
   
    // Pauses the renderer  
    private static boolean showedOnce = true;  
    private static boolean renderedOnce = false;  
   
     
    //-------------------------------------------------------------------------  
   
    // Default square canvas dimension in pixels.  
    private static final int DEFAULT_SIZE = 600;  
   
    // Default boundaries of canvas scale.  
    private static final double DEFAULT_MIN =  0.0;  
    private static final double DEFAULT_MAX =  1.0;  
   
    // Default camera mode  
    private static final int    DEFAULT_CAMERA_MODE = ORBIT_MODE;  
   
    // Default field of vision for perspective projection.  
    private static final double DEFAULT_FOV = 0.9;  
    private static final int    DEFAULT_NUM_DIVISIONS = 100;  
   
    // Default clipping distances for rendering.  
    private static final double DEFAULT_FRONT_CLIP = 0.01;  
    private static final double DEFAULT_BACK_CLIP  = 10;  
   
    // Default pen settings.  
    private static final Font  DEFAULT_FONT       = new Font("Arial", Font.PLAIN, 16);  
    private static final double DEFAULT_PEN_RADIUS = 0.002;  
    private static final Color DEFAULT_PEN_COLOR  = StdDraw3D.WHITE;  
   
    // Default background color.  
    private static final Color DEFAULT_BGCOLOR    = StdDraw3D.BLACK;  
   
    // Scales the size of Text3D.  
    private static final double TEXT3D_SHRINK_FACTOR = 0.005;  
    private static final double TEXT3D_DEPTH         = 1.5;  
   
    // Default shape flags.  
    private static final int PRIMFLAGS =  
        Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;  
   
    // Infinite bounding sphere.  
    private static final BoundingSphere INFINITE_BOUNDS =  
        new BoundingSphere(new Point3d(0.0,0.0,0.0), 1e100);  
   
    // Axis vectors.  
    private static final Vector3D xAxis = new Vector3D(1, 0, 0);  
    private static final Vector3D yAxis = new Vector3D(0, 1, 0);  
    private static final Vector3D zAxis = new Vector3D(0, 0, 1);  
   
     
    //-------------------------------------------------------------------------  
   
    // Singleton for callbacks - avoids generation of extra .class files.  
    private static StdDraw3D std = new StdDraw3D();  
   
    // Blank constructor.  
    private StdDraw3D () { }  
   
    // Static initializer.  
    static {  
        //System.setProperty("j3d.rend", "ogl");  
        System.setProperty("j3d.audiodevice", "com.sun.j3d.audioengines.javasound.JavaSoundMixer");  
        setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE);  
    }  
   
     
   
     
    private static void initialize () {  
   
        numDivisions = DEFAULT_NUM_DIVISIONS;  
   
        onscreenImage  = createBufferedImage();  
        offscreenImage = createBufferedImage();  
        infoImage      = createBufferedImage();  
   
        initializeCanvas();  
   
        if (frame != null) frame.setVisible(false);  
        frame = new JFrame();  
        frame.setVisible(false);  
        frame.setResizable(fullscreen);  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setTitle("Standard Draw 3D");  
        frame.add(canvasPanel);  
        frame.setJMenuBar(createMenuBar());  
        frame.addComponentListener(std);  
        frame.addWindowFocusListener(std);  
        //frame.getContentPane().setCursor(new Cursor(Cursor.MOVE_CURSOR));  
        frame.pack();  
   
        rootGroup = createBranchGroup();  
        lightGroup = createBranchGroup();  
        bgGroup = createBranchGroup();  
        soundGroup = createBranchGroup();  
        fogGroup = createBranchGroup();  
        appearanceGroup = createBranchGroup();  
   
        onscreenGroup  = createBranchGroup();  
        offscreenGroup = createBranchGroup();  
   
        rootGroup.addChild(onscreenGroup);  
        rootGroup.addChild(lightGroup);  
        rootGroup.addChild(bgGroup);  
        rootGroup.addChild(soundGroup);  
        rootGroup.addChild(fogGroup);  
        rootGroup.addChild(appearanceGroup);  
   
        universe = new SimpleUniverse(canvas, 2);  
        universe.addBranchGraph(rootGroup);  
   
        setDefaultLight();  
   
        Viewer viewer = universe.getViewer();  
        viewer.createAudioDevice();  
   
        view = viewer.getView();  
        view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);  
        view.setScreenScalePolicy(View.SCALE_EXPLICIT);  
        view.setLocalEyeLightingEnable(true);  
        setAntiAliasing(false);  
   
        //view.setMinimumFrameCycleTime(long minimumTime);  
   
        ViewingPlatform viewingPlatform = universe.getViewingPlatform();  
        viewingPlatform.setNominalViewingTransform();  
   
        orbit = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ALL^OrbitBehavior.STOP_ZOOM);  
        BoundingSphere bounds = INFINITE_BOUNDS;  
        orbit.setMinRadius(0);  
        orbit.setSchedulingBounds(bounds);  
        setOrbitCenter(new Point3d(0, 0, 0));  
   
        viewingPlatform.setViewPlatformBehavior(orbit);  
        TransformGroup cameraTG = viewingPlatform.getViewPlatformTransform();  
   
        Transform3D cameraTrans = new Transform3D();  
        cameraTG.getTransform(cameraTrans);  
   
        viewingPlatform.detach();  
        cameraTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);  
        cameraTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);  
        camera = new Camera(cameraTG);  
        universe.addBranchGraph(viewingPlatform);  
   
        setPerspectiveProjection();  
        setCameraMode();  
        setPenColor();  
        setPenRadius();  
        setFont();  
        setScale();  
        setInfoDisplay(true);  
   
        setBackground(DEFAULT_BGCOLOR);  
   
        frame.setVisible(true);  
        frame.toFront();  
        frame.setState(Frame.NORMAL);  
        initialized = true;  
   
    }  
   
     
    private static void initializeCanvas () {  
   
        Panel p = new Panel();  
   
        GridBagLayout gl = new GridBagLayout();  
        GridBagConstraints gbc = new GridBagConstraints();  
   
        p.setLayout(gl);  
        gbc.gridx = 0;  
        gbc.gridy = 0;  
        gbc.gridwidth = 5;  
        gbc.gridheight = 5;  
   
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();  
   
        canvas = new Canvas3D(config) {  
   
            public void preRender () {  
                if (camera.pair != null) {  
                    camera.setPosition(camera.pair.getPosition());  
                    camera.setOrientation(camera.pair.getOrientation());  
                }  
            }  
   
            public void postRender () {  
                J3DGraphics2D graphics = this.getGraphics2D();  
   
                graphics.drawRenderedImage(onscreenImage, new AffineTransform());  
                if (infoDisplay) {  
                    graphics.drawRenderedImage(infoImage, new AffineTransform());  
                }  
   
                graphics.flush(false);  
   
   
                Thread.yield();  
            }  
   
             
        };  
   
        canvas.addKeyListener(std);  
        canvas.addMouseListener(std);  
        canvas.addMouseMotionListener(std);  
        canvas.addMouseWheelListener(std);  
   
        canvas.setSize(width, height);  
        p.add(canvas, gbc);  
   
        canvasPanel = p;  
   
        //canvas.stopRenderer();  
    }  
   
     
    private static JMenuBar createMenuBar () {  
   
        menuBar = new JMenuBar();  
   
        fileMenu = new JMenu("File");  
        menuBar.add(fileMenu);  
   
        loadButton = new JMenuItem(" Load 3D Model..  ");  
        loadButton.addActionListener(std);  
        loadButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
        fileMenu.add(loadButton);  
   
        saveButton = new JMenuItem(" Save Image...  ");  
        saveButton.addActionListener(std);  
        saveButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
        fileMenu.add(saveButton);  
   
        save3DButton = new JMenuItem(" Export 3D Scene...  ");  
        save3DButton.addActionListener(std);  
        save3DButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
        //fileMenu.add(save3DButton);  
   
        fileMenu.addSeparator();  
   
        quitButton = new JMenuItem(" Quit...   ");  
        quitButton.addActionListener(std);  
        quitButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
        fileMenu.add(quitButton);  
   
        cameraMenu = new JMenu("Camera");  
        menuBar.add(cameraMenu);  
   
        JLabel cameraLabel = new JLabel("Camera Mode");  
        cameraLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  
        cameraLabel.setForeground(GRAY);  
        cameraMenu.add(cameraLabel);  
        cameraMenu.addSeparator();  
   
        ButtonGroup cameraButtonGroup = new ButtonGroup();  
   
        orbitModeButton  
        = new JRadioButtonMenuItem("Orbit Mode");  
        orbitModeButton.setSelected(true);  
        cameraButtonGroup.add(orbitModeButton);  
        cameraMenu.add(orbitModeButton);  
        orbitModeButton.addActionListener(std);  
        orbitModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
   
        fpsModeButton  
        = new JRadioButtonMenuItem("First-Person Mode");  
        cameraButtonGroup.add(fpsModeButton);  
        cameraMenu.add(fpsModeButton);  
        fpsModeButton.addActionListener(std);  
        fpsModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
   
        airplaneModeButton  
        = new JRadioButtonMenuItem("Airplane Mode");  
        cameraButtonGroup.add(airplaneModeButton);  
        cameraMenu.add(airplaneModeButton);  
        airplaneModeButton.addActionListener(std);  
        airplaneModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
   
        lookModeButton  
        = new JRadioButtonMenuItem("Look Mode");  
        cameraButtonGroup.add(lookModeButton);  
        cameraMenu.add(lookModeButton);  
        lookModeButton.addActionListener(std);  
        lookModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
   
        fixedModeButton  
        = new JRadioButtonMenuItem("Fixed Mode");  
        cameraButtonGroup.add(fixedModeButton);  
        cameraMenu.add(fixedModeButton);  
        fixedModeButton.addActionListener(std);  
        fixedModeButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5,  
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  
   
        cameraMenu.addSeparator();  
        JLabel projectionLabel = new JLabel("Projection Mode");  
        projectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  
        projectionLabel.setForeground(GRAY);  
        cameraMenu.add(projectionLabel);  
        cameraMenu.addSeparator();  
   
        SpinnerNumberModel snm = new SpinnerNumberModel(DEFAULT_FOV, 0.5, 3.0, 0.05);  
        fovSpinner = new JSpinner(snm);  
        JPanel fovPanel = new JPanel();  
        fovPanel.setLayout(new BoxLayout(fovPanel, BoxLayout.X_AXIS));  
        JLabel fovLabel = new JLabel("Field of View:");  
        fovPanel.add(javax.swing.Box.createRigidArea(new Dimension(30, 5)));  
        fovPanel.add(fovLabel);  
        fovPanel.add(javax.swing.Box.createRigidArea(new Dimension(10, 5)));  
        fovPanel.add(fovSpinner);  
   
        final ButtonGroup projectionButtons = new ButtonGroup();  
        perspectiveButton = new JRadioButtonMenuItem("Perspective Projection");  
        parallelButton = new JRadioButtonMenuItem("Parallel Projection");  
   
        fovSpinner.addChangeListener(std);  
   
        perspectiveButton.addActionListener(std);  
   
        parallelButton.addActionListener(std);  
   
        cameraMenu.add(parallelButton);  
        cameraMenu.add(perspectiveButton);  
        cameraMenu.add(fovPanel);  
   
        projectionButtons.add(parallelButton);  
        projectionButtons.add(perspectiveButton);  
        perspectiveButton.setSelected(true);  
   
        graphicsMenu = new JMenu("Graphics");  
        // Leaving out graphics menu for now!!  
        //menuBar.add(graphicsMenu);  
   
        JLabel graphicsLabel = new JLabel("Polygon Count");  
        graphicsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  
        graphicsLabel.setForeground(GRAY);  
        graphicsMenu.add(graphicsLabel);  
        graphicsMenu.addSeparator();  
   
        SpinnerNumberModel snm2 =  
            new SpinnerNumberModel(DEFAULT_NUM_DIVISIONS, 4, 4000, 5);  
        numDivSpinner = new JSpinner(snm2);  
   
        JPanel numDivPanel = new JPanel();  
        numDivPanel.setLayout(new BoxLayout(numDivPanel, BoxLayout.X_AXIS));  
        JLabel numDivLabel = new JLabel("Triangles:");  
        numDivPanel.add(javax.swing.Box.createRigidArea(new Dimension(5, 5)));  
        numDivPanel.add(numDivLabel);  
        numDivPanel.add(javax.swing.Box.createRigidArea(new Dimension(15, 5)));  
        numDivPanel.add(numDivSpinner);  
        graphicsMenu.add(numDivPanel);  
   
        numDivSpinner.addChangeListener(std);  
   
        graphicsMenu.addSeparator();  
        JLabel graphicsLabel2 = new JLabel("Advanced Rendering");  
        graphicsLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);  
        graphicsLabel2.setForeground(GRAY);  
        graphicsMenu.add(graphicsLabel2);  
        graphicsMenu.addSeparator();  
   
        antiAliasingButton = new JCheckBoxMenuItem("Enable Anti-Aliasing");  
        antiAliasingButton.setSelected(false);  
        antiAliasingButton.addActionListener(std);  
   
        graphicsMenu.add(antiAliasingButton);  
   
        infoCheckBox = new JCheckBox("Show Info Display");  
        infoCheckBox.setFocusable(false);  
        infoCheckBox.addActionListener(std);  
        menuBar.add(javax.swing.Box.createRigidArea(new Dimension(50, 5)));  
        menuBar.add(infoCheckBox);  
   
        return menuBar;  
    }  
   
    public void stateChanged(ChangeEvent e) {  
        Object source = e.getSource();  
   
        if (source == numDivSpinner)  
            numDivisions = (Integer)numDivSpinner.getValue();  
        if (source == fovSpinner) {  
            setPerspectiveProjection((Double)fovSpinner.getValue());  
            perspectiveButton.setSelected(true);  
        }  
    }  
   
     
    public void actionPerformed (ActionEvent e) {  
        Object source = e.getSource();  
   
        if (source == saveButton)       
            saveAction();  
        else if (source == loadButton)  
            loadAction();  
        else if (source == save3DButton)     
            save3DAction();  
        else if (source == quitButton)       
            quitAction();  
        else if (source == orbitModeButton)  
            setCameraMode(ORBIT_MODE);  
        else if (source == fpsModeButton)    
            setCameraMode(FPS_MODE);  
        else if (source == airplaneModeButton)  
            setCameraMode(AIRPLANE_MODE);  
        else if (source == lookModeButton)   
            setCameraMode(LOOK_MODE);  
        else if (source == fixedModeButton)  
            setCameraMode(FIXED_MODE);  
        else if (source == perspectiveButton)  
            setPerspectiveProjection((Double)fovSpinner.getValue());  
        else if (source == parallelButton)  
            setParallelProjection();  
        else if (source == antiAliasingButton)  
            setAntiAliasing(antiAliasingButton.isSelected());  
        else if (source == infoCheckBox)  
            setInfoDisplay(infoCheckBox.isSelected());  
    }  
   
     
    public void componentHidden(ComponentEvent e) { keysDown = new TreeSet<Integer>(); }  
   
     
    public void componentMoved(ComponentEvent e) { keysDown = new TreeSet<Integer>(); }  
   
     
    public void componentShown(ComponentEvent e) { keysDown = new TreeSet<Integer>(); }  
   
     
    public void componentResized(ComponentEvent e) { keysDown = new TreeSet<Integer>(); }  
   
     
    public void windowGainedFocus(WindowEvent e) { keysDown = new TreeSet<Integer>(); }  
   
     
    public void windowLostFocus(WindowEvent e) { keysDown = new TreeSet<Integer>(); }  
   
     
    private static BranchGroup createBranchGroup () {  
   
        BranchGroup bg = new BranchGroup();  
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);  
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);  
        bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);  
        bg.setCapability(BranchGroup.ALLOW_DETACH);  
        bg.setPickable(false);  
        bg.setCollidable(false);  
        return bg;  
    }  
   
     
    private static TransformGroup createTransformGroup () {  
   
        TransformGroup tg = new TransformGroup();  
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);  
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);  
        tg.setPickable(false);  
        tg.setCollidable(false);  
        return tg;  
    }  
   
     
    private static Background createBackground () {  
   
        Background background = new Background();  
        background.setCapability(Background.ALLOW_COLOR_WRITE);  
        background.setCapability(Background.ALLOW_IMAGE_WRITE);  
        background.setCapability(Background.ALLOW_GEOMETRY_WRITE);  
        background.setApplicationBounds(INFINITE_BOUNDS);  
        return background;  
    }  
   
     
    private static Texture createTexture (String imageURL) {  
   
        TextureLoader loader;  
        try {  
            loader = new TextureLoader(imageURL, "RGBA", TextureLoader.Y_UP, new Container());  
        } catch (Exception e) {  
            throw new RuntimeException ("Could not read from the file '" + imageURL + "'");  
        }  
   
        Texture texture = loader.getTexture();  
        texture.setBoundaryModeS(Texture.WRAP);  
        texture.setBoundaryModeT(Texture.WRAP);  
        texture.setBoundaryColor( new Color4f( 0.0f, 1.0f, 0.0f, 0.0f ) );  
   
        return texture;  
    }  
   
    private static Appearance createBlankAppearance () {  
        Appearance ap = new Appearance();  
        ap.setCapability(Appearance.ALLOW_MATERIAL_READ);  
        ap.setCapability(Appearance.ALLOW_MATERIAL_WRITE);  
        ap.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);  
        ap.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);  
        return ap;  
    }  
   
     
    private static Appearance createAppearance (String imageURL, boolean fill) {   
   
        Appearance ap = createBlankAppearance();  
   
        PolygonAttributes pa = new PolygonAttributes();  
        if (!fill) pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);  
        pa.setCullFace(PolygonAttributes.CULL_NONE);  
        ap.setPolygonAttributes(pa);  
   
        if (imageURL != null) {  
   
            Texture texture = createTexture(imageURL);  
            TextureAttributes texAttr = new TextureAttributes();  
            texAttr.setTextureMode(TextureAttributes.REPLACE);  
   
            ap.setTexture(texture);  
            ap.setTextureAttributes(texAttr);  
        }  
   
        Color3f col = new Color3f(penColor);  
        Color3f black = new Color3f(0, 0, 0);  
        Color3f specular = new Color3f(GRAY);  
   
        // Material properties  
        Material material = new Material(col, black, col, specular, 64);  
        material.setCapability(Material.ALLOW_COMPONENT_READ);  
        material.setCapability(Material.ALLOW_COMPONENT_WRITE);  
        material.setLightingEnable(true);  
        ap.setMaterial(material);  
   
        // Transparecy properties  
        float alpha = ((float)penColor.getAlpha()) / 255;  
        if (alpha < 1.0) {  
            TransparencyAttributes t = new TransparencyAttributes();  
            t.setTransparencyMode(TransparencyAttributes.BLENDED);  
            t.setTransparency(1 - alpha);  
            ap.setTransparencyAttributes(t);  
        }  
   
        LineAttributes la = new LineAttributes();  
        la.setLineWidth(penRadius);  
        la.setLineAntialiasingEnable(view.getSceneAntialiasingEnable());  
   
        PointAttributes poa = new PointAttributes();  
        poa.setPointAntialiasingEnable(view.getSceneAntialiasingEnable());  
   
        ColoringAttributes ca = new ColoringAttributes();  
        ca.setShadeModel(ColoringAttributes.SHADE_GOURAUD);  
        ca.setColor(col);  
   
        ap.setLineAttributes(la);  
        ap.setPointAttributes(poa);  
        ap.setColoringAttributes(ca);  
   
        return ap;  
    }  
   
    // FIX THIS check that adding specular didn't mess up custom shapes (lines, triangles, points)  
    private static Appearance createCustomAppearance (boolean fill) {  
        Appearance ap = createBlankAppearance();  
   
        PolygonAttributes pa = new PolygonAttributes();  
        if (!fill) pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);  
        pa.setCullFace(PolygonAttributes.CULL_NONE);  
   
        LineAttributes la = new LineAttributes();  
        la.setLineWidth(penRadius);  
        la.setLineAntialiasingEnable(view.getSceneAntialiasingEnable());  
   
        PointAttributes poa = new PointAttributes();  
        poa.setPointAntialiasingEnable(view.getSceneAntialiasingEnable());  
   
        ap.setPolygonAttributes(pa);  
        ap.setLineAttributes(la);  
        ap.setPointAttributes(poa);  
   
        Color3f col = new Color3f(penColor);  
        Color3f black = new Color3f(0, 0, 0);  
        Color3f specular = new Color3f(GRAY);  
   
        // Material properties  
        Material material = new Material(col, black, col, specular, 64);  
        material.setCapability(Material.ALLOW_COMPONENT_READ);  
        material.setCapability(Material.ALLOW_COMPONENT_WRITE);  
        material.setLightingEnable(true);  
        ap.setMaterial(material);  
   
        return ap;  
    }  
   
    private static Shape3D createShape3D (Geometry geom) {  
        Shape3D shape = new Shape3D(geom);  
        shape.setPickable(false);  
        shape.setCollidable(false);  
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);  
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);  
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);  
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);  
   
        return shape;  
    }  
   
     
    private static BufferedImage createBufferedImage () {  
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  
    }  
   
     
    private static Vector3d createVector3d (Vector3D v) {  
        return new Vector3d(v.x, v.y, v.z);  
    }  
   
     
    private static Vector3f createVector3f (double x, double y, double z) {  
        return new Vector3f((float)x, (float)y, (float)z);  
    }  
   
     
    private static Vector3f createVector3f (Vector3D v) {  
        return createVector3f(v.x, v.y, v.z);  
    }  
   
     
    private static Point3f createPoint3f (Vector3D v) {  
        return createPoint3f(v.x, v.y, v.z);  
    }  
   
     
    private static Point3f createPoint3f (double x, double y, double z) {  
        return new Point3f((float)x, (float)y, (float)z);  
    }  
   
     
    private static Scanner createScanner (String s) {  
   
        Scanner scanner;  
        String charsetName = "ISO-8859-1";  
        java.util.Locale usLocale = new java.util.Locale("en", "US");  
        try {  
            // first try to read file from local file system  
            File file = new File(s);  
            if (file.exists()) {  
                scanner = new Scanner(file, charsetName);  
                scanner.useLocale(usLocale);  
                return scanner;  
            }  
   
            // next try for website  
            URL url = new URL(s);  
   
            URLConnection site = url.openConnection();  
            InputStream is     = site.getInputStream();  
            scanner            = new Scanner(new BufferedInputStream(is), charsetName);  
            scanner.useLocale(usLocale);  
            return scanner;  
        }  
        catch (IOException ioe) {  
            System.err.println("Could not open " + s + ".");  
            return null;  
        }  
    }  
   
     
   
     
    public static void setCanvasSize (int w, int h) {  
   
        setCanvasSize(w, h, false);  
    }  
   
    private static void setCanvasSize (int w, int h, boolean fs) {  
   
        fullscreen = fs;  
   
        if (w < 1 || h < 1) throw new RuntimeException("Dimensions must be positive integers!");  
        width = w;  
        height = h;  
   
        aspectRatio = (double)width / (double)height;  
        initialize();  
    }  
   
     
    public static void setScale () { setScale(DEFAULT_MIN, DEFAULT_MAX); }  
   
     
    public static void setScale (double minimum, double maximum) {  
   
        min = minimum;  
        max = maximum;  
        zoom = (max - min) / 2;  
        double center = min + zoom;  
   
        //double nominalDist = camera.getPosition().z;  
        camera.setPosition(center, center, zoom * (2 + Math.sqrt(2)));  
   
        double orbitScale = 0.5 * zoom;  
   
        orbit.setZoomFactor(orbitScale);  
        orbit.setTransFactors(orbitScale, orbitScale);  
   
        setOrbitCenter(new Point3d(center, center, center));  
   
        view.setFrontClipDistance(DEFAULT_FRONT_CLIP * zoom);  
        view.setBackClipDistance(DEFAULT_BACK_CLIP * zoom);  
    }  
   
     
    private static float scaleX (double x) {  
   
        double scale = 1;  
        if (width > height) scale = 1 / aspectRatio;  
   
        return (float)(width  * (x * scale - min) / (2 * zoom));  
    }  
   
     
    private static float scaleY (double y) {  
   
        double scale = 1;  
        if (height > width) scale = aspectRatio;  
   
        return (float)(height * (max - y * scale) / (2 * zoom));  
    }  
   
     
    private static double factorX (double w) {  
   
        double scaleDist = width;  
        if (width > height) scaleDist = height;  
   
        return scaleDist  * (w / (2 * zoom));  
    }  
   
     
    private static double factorY (double h) {  
   
        double scaleDist = height;  
        if (height > width) scaleDist = width;  
   
        return scaleDist * (h / (2 * zoom));  
    }  
   
     
    private static double unscaleX (double xs) {  
   
        double scale = 1;  
        if (width > height) scale = 1 / aspectRatio;  
   
        return (xs * (2 * zoom) / width + min) / scale;  
    }  
   
     
    private static double unscaleY (double ys) {  
   
        double scale = 1;  
        if (height > width) scale = aspectRatio;  
        //System.out.println("unscaleY scale = " + scale);  
        return (max - ys * (2 * zoom) / height) / scale;  
    }  
   
     
   
     
    public static void setPenColor (Color col, int alpha) {  
        setPenColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha));  
    }  
   
     
    public static void  setPenColor () { penColor = DEFAULT_PEN_COLOR; }  
   
     
    public static void  setPenColor (Color col) { penColor = col; }  
   
    public static void setPenColor (int r, int g, int b) {  
        penColor = new Color(r, g, b);     
    }  
   
     
    public static Color getPenColor () { return penColor; }  
   
     
    public static void setPenRadius () { setPenRadius(DEFAULT_PEN_RADIUS); }  
   
     
    public static void  setPenRadius (double r) { penRadius = (float)r * 500; }  
   
     
    public static float getPenRadius () { return penRadius/500f; }  
   
     
    public static void setFont () { font = DEFAULT_FONT; }  
   
     
    public static void setFont (Font f) { font = f; }  
   
     
    public static Font getFont () { return font; }  
   
    public static void setInfoDisplay (boolean enabled) {  
        infoDisplay = enabled;  
        infoCheckBox.setSelected(enabled);  
        camera.move(0, 0, 0);  
        infoDisplay();  
    }  
   
     
   
    public static void fullscreen () {  
   
        frame.setResizable(true);  
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);  
        int w = frame.getSize().width;  
        int h = frame.getSize().height;  
   
        //int w = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();  
        //int h = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();  
   
        int borderY = frame.getInsets().top + frame.getInsets().bottom;  
        int borderX = frame.getInsets().left + frame.getInsets().right;  
   
        setCanvasSize(w - borderX, h - borderY - menuBar.getHeight(), true);  
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);  
    }  
   
     
    public static void setAntiAliasing (boolean enabled) {  
        view.setSceneAntialiasingEnable(enabled);  
        antiAliasingButton.setSelected(enabled);  
        //System.out.println("Anti aliasing enabled: " + enabled);  
    }  
   
     
    public static boolean getAntiAliasing () { return antiAliasingButton.isSelected(); }  
   
     
    public static void setNumDivisions (int N) { numDivisions = N; }  
   
     
    public static int  getNumDivisions () { return numDivisions; }  
   
     
    public static int  getCameraMode () { return cameraMode; }  
   
     
    public static void setCameraMode (int mode) {  
   
        cameraMode = mode;  
   
        if (cameraMode == ORBIT_MODE) {  
            orbit.setRotateEnable(true);  
            if (view.getProjectionPolicy() != View.PARALLEL_PROJECTION)  
                orbit.setZoomEnable(true);  
            orbit.setTranslateEnable(true);  
            orbit.setRotationCenter(orbitCenter);  
            orbitModeButton.setSelected(true);  
        } else {  
            orbit.setRotateEnable(false);  
            orbit.setZoomEnable(false);  
            orbit.setTranslateEnable(false);  
        }  
        if (cameraMode == FPS_MODE) {  
            fpsModeButton.setSelected(true);  
            camera.rotateFPS(0, 0, 0);  
        }  
        if (cameraMode == AIRPLANE_MODE) {  
            airplaneModeButton.setSelected(true);  
        }  
        if (cameraMode == LOOK_MODE) {  
            //System.out.println("Camera in look mode.");  
            lookModeButton.setSelected(true);  
        }  
        if (cameraMode == FIXED_MODE) {  
            //System.out.println("Camera in fixed mode.");  
            fixedModeButton.setSelected(true);  
        }  
        if (cameraMode == IMMERSIVE_MODE) {  
   
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);  
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(  
                    cursorImg, new java.awt.Point(0, 0), "blank cursor");  
            frame.getContentPane().setCursor(blankCursor);  
            //System.out.println("Camera in fps mode.");  
        } else {  
            frame.getContentPane().setCursor(Cursor.getDefaultCursor());  
        }  
    }  
   
     
    public static void setCameraMode() { setCameraMode(DEFAULT_CAMERA_MODE); }  
   
     
    public static void setOrbitCenter(double x, double y, double z) {  
        setOrbitCenter(new Point3d(x, y, z));  
    }  
   
     
    public static void setOrbitCenter(Vector3D v) {  
        setOrbitCenter(new Point3d(v.x, v.y, v.z));  
    }  
   
    private static void setOrbitCenter(Point3d center) {  
        orbitCenter = center;  
        orbit.setRotationCenter(orbitCenter);  
    }  
   
    public static Vector3D getOrbitCenter() {  
        return new Vector3D(orbitCenter);  
    }  
   
     
    public static void setPerspectiveProjection () {  
        setPerspectiveProjection(DEFAULT_FOV);  
    }  
   
     
    public static void setPerspectiveProjection (double fov) {  
   
        view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);  
        view.setWindowEyepointPolicy(View.RELATIVE_TO_FIELD_OF_VIEW);  
        view.setFieldOfView(fov);  
        setScreenScale(1);  
        orbit.setZoomEnable(true);  
        perspectiveButton.setSelected(true);  
        if ((Double)fovSpinner.getValue() != fov)  
            fovSpinner.setValue(fov);  
        //if (view.getProjectionPolicy() == View.PERSPECTIVE_PROJECTION) return;  
    }  
   
     
    public static void setParallelProjection () {  
   
        if (view.getProjectionPolicy() == View.PARALLEL_PROJECTION) return;  
        view.setProjectionPolicy(View.PARALLEL_PROJECTION);  
        orbit.setZoomEnable(false);  
        parallelButton.setSelected(true);  
   
        setScreenScale(0.3 / zoom);  
    }  
   
    private static void setScreenScale (double scale) {  
        double ratio = scale / view.getScreenScale();  
        view.setScreenScale(scale);  
        view.setFrontClipDistance(view.getFrontClipDistance() * ratio);  
        view.setBackClipDistance(view.getBackClipDistance() * ratio);  
    }  
   
     
   
     
    public static boolean mousePressed () {  
        synchronized (mouseLock) {  
            return (mouse1Pressed() || mouse2Pressed() || mouse3Pressed());  
        }  
    }  
   
     
    public static boolean mouse1Pressed () {  
        synchronized (mouseLock) {  
            return mouse1;  
        }  
    }  
   
     
    public static boolean mouse2Pressed () {  
        synchronized (mouseLock) {  
            return mouse2;  
        }  
    }  
   
     
    public static boolean mouse3Pressed () {  
        synchronized (mouseLock) {  
            return mouse3;  
        }  
    }  
   
     
    public static double mouseX () {  
        synchronized (mouseLock) {  
            return unscaleX(mouseX);        
        }  
    }  
   
     
    public static double mouseY () {  
        synchronized (mouseLock) {  
            return unscaleY(mouseY);        
        }  
    }  
   
     
    public void mouseClicked (MouseEvent e) {  
    }  
   
     
    public void mouseEntered (MouseEvent e) { }  
   
     
    public void mouseExited  (MouseEvent e) {  
   
    }  
   
     
    public void mousePressed (MouseEvent e) {  
        synchronized (mouseLock) {  
            mouseX = e.getX();  
            mouseY = e.getY();  
            if (e.getButton() == 1) mouse1 = true;  
            if (e.getButton() == 2) mouse2 = true;  
            if (e.getButton() == 3) mouse3 = true;  
            //System.out.println("Mouse button = " + e.getButton());  
        }  
    }  
   
     
    public void mouseReleased (MouseEvent e) {  
        synchronized (mouseLock) {  
            if (e.getButton() == 1) mouse1 = false;  
            if (e.getButton() == 2) mouse2 = false;  
            if (e.getButton() == 3) mouse3 = false;  
        }  
    }  
   
     
    public void mouseDragged (MouseEvent e)  {  
        synchronized (mouseLock) {  
   
            mouseMotionEvents(e, e.getX(), e.getY(), true);  
            mouseX = e.getX();  
            mouseY = e.getY();  
        }  
    }  
   
     
    public void mouseMoved (MouseEvent e) {  
        synchronized (mouseLock) {  
   
            //System.out.println(e.getX() + " " + e.getY());  
            mouseMotionEvents(e, e.getX(), e.getY(), false);  
        }  
    }     
   
     
    public void mouseWheelMoved (MouseWheelEvent e) {  
   
        double notches = e.getWheelRotation();  
        //System.out.println(notches);  
        if ((cameraMode == ORBIT_MODE) && (view.getProjectionPolicy() == View.PARALLEL_PROJECTION)) {  
            camera.moveRelative(0, 0, notches * zoom / 20);  
        }  
    }  
   
    private static void mouseMotionEvents (MouseEvent e, double newX, double newY, boolean dragged) {  
   
        //System.out.println("x = " + mouseX() + " y = " + mouseY());  
   
        if (cameraMode == FIXED_MODE) return;  
   
        if (cameraMode == FPS_MODE) {  
            if (dragged || immersive) {  
                camera.rotateFPS((mouseY - newY)/4, (mouseX - newX)/4, 0);  
            }  
            return;  
        }  
   
        if ((cameraMode == AIRPLANE_MODE)) {  
            if (dragged || immersive)  
                camera.rotateRelative((mouseY - newY)/4, (mouseX - newX)/4, 0);  
            return;  
        }  
   
        if ((cameraMode == LOOK_MODE)) {  
            if (dragged || immersive)  
                camera.rotateFPS((mouseY - newY)/4, (mouseX - newX)/4, 0);  
            return;  
        }  
   
        if ((cameraMode == ORBIT_MODE) && (dragged && isKeyPressed(KeyEvent.VK_ALT)) && (view.getProjectionPolicy() == View.PARALLEL_PROJECTION)) {  
            camera.moveRelative(0, 0, (double)(newY - mouseY) * zoom / 50);  
            return;  
        }  
    }  
   
     
   
     
    public static boolean hasNextKeyTyped () {  
        synchronized (keyLock) {  
            return !keysTyped.isEmpty();  
        }  
    }  
   
     
    public static char nextKeyTyped () {  
        synchronized (keyLock) {  
            return keysTyped.removeLast();  
        }  
    }  
   
     
    public static boolean isKeyPressed (int key) {  
        synchronized (keyLock) {  
            return keysDown.contains(key);  
        }  
    }  
   
     
    public void keyTyped (KeyEvent e) {  
        synchronized (keyLock) {  
   
            char c = e.getKeyChar();  
            keysTyped.addFirst(c);  
   
            if (c == '`') setCameraMode((getCameraMode() + 1) % 5);  
        }  
    }  
   
     
    public void keyPressed (KeyEvent e)   {  
        synchronized (keyLock) {  
            keysDown.add(e.getKeyCode());  
            //System.out.println((int)e.getKeyCode() + " pressed");  
        }  
    }  
   
     
    public void keyReleased (KeyEvent e)   {  
        synchronized (keyLock) {  
            keysDown.remove(e.getKeyCode());  
            //System.out.println((int)e.getKeyCode() + " released");  
        }  
    }  
   
     
    private static void moveEvents (int time) {  
   
        infoDisplay();  
   
        if (isKeyPressed(KeyEvent.VK_CONTROL)) return;  
   
        if (cameraMode == FPS_MODE) {  
            double move = 0.00015 * time * (zoom);  
            if (isKeyPressed('W') || isKeyPressed(KeyEvent.VK_UP))        camera.moveRelative(0,     0,  move * 3);  
            if (isKeyPressed('S') || isKeyPressed(KeyEvent.VK_DOWN))      camera.moveRelative(0,     0, -move * 3);  
            if (isKeyPressed('A') || isKeyPressed(KeyEvent.VK_LEFT))      camera.moveRelative(-move, 0, 0);  
            if (isKeyPressed('D') || isKeyPressed(KeyEvent.VK_RIGHT))     camera.moveRelative( move, 0, 0);  
            if (isKeyPressed('Q') || isKeyPressed(KeyEvent.VK_PAGE_UP))   camera.moveRelative(0,     move, 0);  
            if (isKeyPressed('E') || isKeyPressed(KeyEvent.VK_PAGE_DOWN)) camera.moveRelative(0,    -move, 0);  
        }  
        if (cameraMode == AIRPLANE_MODE) {  
            double move = 0.00015 * time * (zoom);  
            if (isKeyPressed('W') || isKeyPressed(KeyEvent.VK_UP))        camera.moveRelative(0,     0,  move * 3);  
            if (isKeyPressed('S') || isKeyPressed(KeyEvent.VK_DOWN))      camera.moveRelative(0,     0, -move * 3);  
            if (isKeyPressed('A') || isKeyPressed(KeyEvent.VK_LEFT))      camera.moveRelative(-move, 0, 0);  
            if (isKeyPressed('D') || isKeyPressed(KeyEvent.VK_RIGHT))     camera.moveRelative( move, 0, 0);  
            if (isKeyPressed('Q') || isKeyPressed(KeyEvent.VK_PAGE_UP))   camera.rotateRelative(0, 0,  move * 250 / zoom);  
            if (isKeyPressed('E') || isKeyPressed(KeyEvent.VK_PAGE_DOWN)) camera.rotateRelative(0, 0, -move * 250 / zoom);  
        }  
    }  
   
     
   
    private static void save3DAction () {  
   
        FileDialog chooser = new FileDialog(StdDraw3D.frame, "Save as a 3D file for loading later.", FileDialog.SAVE);  
        chooser.setVisible(true);  
        String filename = chooser.getFile();  
        if (filename != null) {  
            StdDraw3D.saveScene3D(chooser.getDirectory() + File.separator + chooser.getFile());  
        }  
   
        keysDown.remove(KeyEvent.VK_META);  
        keysDown.remove(KeyEvent.VK_CONTROL);  
        keysDown.remove(KeyEvent.VK_E);  
    }    
   
    private static void loadAction () {  
   
        FileDialog chooser = new FileDialog(frame, "Pick a .obj or .ply file to load.", FileDialog.LOAD);  
        chooser.setVisible(true);  
        String filename = chooser.getDirectory() + chooser.getFile();  
        model(filename);  
   
        keysDown.remove(KeyEvent.VK_META);  
        keysDown.remove(KeyEvent.VK_CONTROL);  
        keysDown.remove(KeyEvent.VK_L);  
    }  
   
    private static void saveAction () {  
   
        FileDialog chooser = new FileDialog(frame, "Use a .png or .jpg extension.", FileDialog.SAVE);  
        chooser.setVisible(true);  
        String filename = chooser.getFile();  
   
        if (filename != null) {  
            StdDraw3D.save(chooser.getDirectory() + File.separator + chooser.getFile());  
        }  
   
        keysDown.remove(KeyEvent.VK_META);  
        keysDown.remove(KeyEvent.VK_CONTROL);  
        keysDown.remove(KeyEvent.VK_S);  
    }    
   
    private static void quitAction () {  
   
        WindowEvent wev = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);  
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);  
   
        keysDown.remove(KeyEvent.VK_META);  
        keysDown.remove(KeyEvent.VK_CONTROL);  
        keysDown.remove(KeyEvent.VK_Q);  
    }  
   
     
   
     
    public static void setBackground (Color color) {  
   
        if (!color.equals(bgColor)) {  
            bgColor = color;  
   
            rootGroup.removeChild(bgGroup);  
            bgGroup.removeChild(background);  
   
            background = createBackground();  
            background.setColor(new Color3f(bgColor));  
   
            bgGroup.addChild(background);  
            rootGroup.addChild(bgGroup);  
        }  
    }  
   
     
    public static void setBackground (String imageURL) {  
   
        rootGroup.removeChild(bgGroup);  
        bgGroup.removeChild(background);  
   
        background = createBackground();  
   
        BufferedImage bi = null;  
        try { bi = ImageIO.read(new File(imageURL)); }  
        catch (IOException ioe) {ioe.printStackTrace(); }  
   
        if (bi == null) {  
            try { ImageIO.read(new URL(imageURL)); }  
            catch (Exception e) { e.printStackTrace(); }  
        }  
   
        ImageComponent2D imageComp = new ImageComponent2D(ImageComponent.FORMAT_RGB, bi);  
        background.setImage(imageComp);  
        background.setImageScaleMode(Background.SCALE_FIT_ALL);  
   
        bgGroup.addChild(background);  
        rootGroup.addChild(bgGroup);  
    }  
   
     
    public static void setBackgroundSphere (String imageURL) {  
   
        Sphere sphere = new Sphere(1.1f, Sphere.GENERATE_NORMALS  
                | Sphere.GENERATE_NORMALS_INWARD  
                | Sphere.GENERATE_TEXTURE_COORDS, numDivisions);  
   
        Appearance ap = sphere.getAppearance();  
   
        Texture texture = createTexture(imageURL);  
   
        TextureAttributes texAttr = new TextureAttributes();  
        texAttr.setTextureMode(TextureAttributes.REPLACE);  
   
        ap.setTexture(texture);  
        ap.setTextureAttributes(texAttr);  
   
        sphere.setAppearance(ap);  
   
        BranchGroup backGeoBranch = createBranchGroup();  
        backGeoBranch.addChild(sphere);  
   
        rootGroup.removeChild(bgGroup);  
        bgGroup.removeChild(background);  
   
        background = createBackground();  
        background.setGeometry(backGeoBranch);  
   
        bgGroup.addChild(background);  
        rootGroup.addChild(bgGroup);  
   
    }  
   
    public static void clearLight () {  
        lightGroup.removeAllChildren();     
    }  
   
     
    public static void setDefaultLight () {  
        clearLight();  
        directionalLight(-4f, 7f, 12f, LIGHT_GRAY);  
        directionalLight(4f, -7f, -12f, WHITE);  
        ambientLight(new Color(0.1f, 0.1f, 0.1f));  
    }  
   
     
    public static Light directionalLight (Vector3D dir, Color col) {  
        return directionalLight(dir.x, dir.y, dir.z, col);  
    }  
   
     
    public static Light directionalLight (double x, double y, double z, Color col) {  
   
        DirectionalLight light = new DirectionalLight();  
        light.setColor(new Color3f(col));  
   
        light.setInfluencingBounds(INFINITE_BOUNDS);  
        light.setCapability(DirectionalLight.ALLOW_STATE_WRITE);  
        light.setCapability(DirectionalLight.ALLOW_COLOR_WRITE);  
        light.setEnable(true);  
   
        BranchGroup bg = createBranchGroup();  
        TransformGroup tg = createTransformGroup();  
        tg.addChild(light);  
        bg.addChild(tg);  
        lightGroup.addChild(bg);  
   
        Light l = new Light(bg, tg, light);  
        l.setDirection(new Vector3D(x, y, z));  
        return l;  
    }  
   
     
    public static Light ambientLight (Color col) {  
   
        Color3f lightColor = new Color3f(col);  
        AmbientLight light = new AmbientLight(lightColor);  
   
        light.setInfluencingBounds(INFINITE_BOUNDS);  
        light.setCapability(AmbientLight.ALLOW_STATE_WRITE);  
        light.setCapability(AmbientLight.ALLOW_COLOR_WRITE);  
   
        BranchGroup bg = createBranchGroup();  
        TransformGroup tg = createTransformGroup();  
        tg.addChild(light);  
        bg.addChild(tg);  
        lightGroup.addChild(bg);  
   
        return new Light(bg, tg, light);  
    }  
   
    public static Light pointLight (Vector3D origin, Color col) {  
        return pointLight(origin.x, origin.y, origin.z, col, 1.0);  
    }  
   
    public static Light pointLight (double x, double y, double z, Color col) {  
        return pointLight(x, y, z, col, 1.0);  
    }  
   
    public static Light pointLight (Vector3D origin, Color col, double power) {  
        return pointLight(origin.x, origin.y, origin.z, col, power);  
    }  
   
    public static Light pointLight (double x, double y, double z, Color col, double power) {  
   
        PointLight light = new PointLight();  
        light.setColor(new Color3f(col));  
   
        light.setInfluencingBounds(INFINITE_BOUNDS);  
        light.setCapability(PointLight.ALLOW_STATE_WRITE);  
        light.setCapability(PointLight.ALLOW_COLOR_WRITE);  
        light.setCapability(PointLight.ALLOW_ATTENUATION_WRITE);  
   
        float scale = (float)zoom;  
        float linearFade = 0.03f;  
        float quadraticFade = 0.03f;  
        light.setAttenuation(1.0f, linearFade / scale, quadraticFade / (scale * scale));  
   
        BranchGroup bg = createBranchGroup();  
        TransformGroup tg = createTransformGroup();  
        tg.addChild(light);  
        bg.addChild(tg);  
        lightGroup.addChild(bg);  
   
        Light l = new Light(bg, tg, light);  
        l.setPosition(x, y, z);  
        l.scalePower(power);  
   
        return l;  
    }  
   
     
    public static Color randomColor () {  
        return new Color(new Random().nextInt());  
    }  
   
     
    public static Color randomRainbowColor () {  
        return Color.getHSBColor((float)Math.random(), 1.0f, 1.0f);  
    }  
   
     
    public static Vector3D randomDirection() {  
   
        double theta = Math.random() * Math.PI * 2;  
        double phi   = Math.random() * Math.PI;  
        return new Vector3D(Math.cos(theta) * Math.sin(phi), Math.sin(theta) * Math.sin(phi), Math.cos(phi));  
    }  
   
     
   
     
    public static void clear () {  
        clear3D();  
        clearOverlay();  
    }  
   
    public static void clear (Color color) {  
        setBackground(color);  
        clear();  
    }  
   
     
    public static void clear3D() {  
        clear3D = true;  
        offscreenGroup = createBranchGroup();  
    }  
   
     
    public static void clearOverlay () {  
        clearOverlay = true;  
        offscreenImage = createBufferedImage();  
    }  
   
     
    public static void pause (int time) {  
   
        int t = time;  
        int dt = 15;  
   
        while (t > dt) {  
            moveEvents(dt);  
            Toolkit.getDefaultToolkit().sync();  
            try { Thread.currentThread().sleep(dt); }  
            catch (InterruptedException e) { System.out.println("Error sleeping"); }  
   
            t -= dt;  
        }  
   
        moveEvents(t);  
        if (t == 0) return;  
        try { Thread.currentThread().sleep(t); }  
        catch (InterruptedException e) { System.out.println("Error sleeping"); }  
   
        //while (!renderedOnce) {  
        //     try { Thread.currentThread().sleep(1); }  
        //     catch (InterruptedException e) { System.out.println("Error sleeping"); }  
        //  
        //renderedOnce = false;  
        //showedOnce = true;  
    }  
   
     
    public static void finished () {  
   
        show(1000000000);  
    }  
   
     
    public static void show () { show(0); }  
   
     
    public static void show (int time) {  
   
        renderOverlay();  
        render3D();  
        pause(time);  
    }  
   
    public static void showOverlay () { showOverlay(0); }  
   
     
    public static void showOverlay (int time) {  
   
        renderOverlay();  
        pause(time);  
    }  
   
    private static void renderOverlay () {  
        if (clearOverlay) {  
            clearOverlay = false;  
            onscreenImage = offscreenImage;  
        } else {  
            Graphics2D graphics = (Graphics2D) onscreenImage.getGraphics();  
            graphics.drawRenderedImage(offscreenImage, new AffineTransform());  
        }  
        offscreenImage = createBufferedImage();  
    }  
   
    public static void show3D () { show3D(0); }  
   
     
    public static void show3D (int time) {  
   
        render3D();  
        pause(time);  
    }  
   
    private static void render3D () {  
   
        rootGroup.addChild(offscreenGroup);  
   
        if (clear3D) {  
            clear3D = false;  
            rootGroup.removeChild(onscreenGroup);  
            onscreenGroup = offscreenGroup;  
        } else {  
            Enumeration children = offscreenGroup.getAllChildren();  
            while(children.hasMoreElements()) {  
                Node child = (Node)children.nextElement();  
                offscreenGroup.removeChild(child);  
                onscreenGroup.addChild(child);  
            }  
        }  
   
        offscreenGroup = createBranchGroup();  
        //System.out.println("off = " + offscreenGroup.numChildren());  
        //System.out.println("on  = " + onscreenGroup.numChildren());  
   
        rootGroup.removeChild(offscreenGroup);  
   
        //StdOut.println("showed once = true");  
    }  
     
   
     
    public static Shape sphere (double x, double y, double z, double r) {  
        return sphere(x, y, z, r, 0, 0, 0, null);  
    }  
   
     
    public static Shape sphere (double x, double y, double z, double r, double xA, double yA, double zA) {  
        return sphere(x, y, z, r, xA, yA, zA, null);  
    }  
   
     
    public static Shape sphere (double x, double y, double z, double r, String imageURL) {  
        return sphere(x, y, z, r, 0, 0, 0, imageURL);  
    }  
   
     
    public static Shape sphere (double x, double y, double z, double r, double xA, double yA, double zA, String imageURL) {  
   
        Vector3f dimensions = createVector3f(0, 0, r);  
        Sphere sphere = new Sphere(dimensions.z, PRIMFLAGS, numDivisions);  
        sphere.setAppearance(createAppearance(imageURL, true));  
        return primitive(sphere, x, y, z, new Vector3d(xA, yA, zA), null);  
    }  
   
     
    public static Shape wireSphere (double x, double y, double z, double r) {  
        return wireSphere(x, y, z, r, 0, 0, 0);  
    }  
   
     
    public static Shape wireSphere (double x, double y, double z, double r, double xA, double yA, double zA) {  
   
        Vector3f dimensions = createVector3f(0, 0, r);  
        Sphere sphere = new Sphere(dimensions.z, PRIMFLAGS, numDivisions);  
        sphere.setAppearance(createAppearance(null, false));  
        return primitive(sphere, x, y, z, new Vector3d(xA, yA, zA), null);  
    }  
   
    public static Shape ellipsoid (double x, double y, double z, double w, double h, double d) {  
        return ellipsoid(x, y, z, w, h, d, 0, 0, 0, null);  
    }  
   
     
    public static Shape ellipsoid (double x, double y, double z, double w, double h, double d, double xA, double yA, double zA) {  
        return ellipsoid(x, y, z, w, h, d, xA, yA, zA, null);  
    }  
   
     
    public static Shape ellipsoid (double x, double y, double z, double w, double h, double d, String imageURL) {  
        return ellipsoid(x, y, z, w, h, d, 0, 0, 0, imageURL);  
    }  
   
     
    public static Shape ellipsoid (double x, double y, double z, double w, double h, double d, double xA, double yA, double zA, String imageURL) {  
   
        Sphere sphere = new Sphere(1, PRIMFLAGS, numDivisions);  
        sphere.setAppearance(createAppearance(imageURL, true));  
        return primitive(sphere, x, y, z, new Vector3d(xA, yA, zA), new Vector3d(w, h, d));  
    }  
   
     
    public static Shape wireEllipsoid (double x, double y, double z, double w, double h, double d) {  
        return wireEllipsoid(x, y, z, w, h, d, 0, 0, 0);  
    }  
   
     
    public static Shape wireEllipsoid (double x, double y, double z, double w, double h, double d, double xA, double yA, double zA) {  
   
        Sphere sphere = new Sphere(1, PRIMFLAGS, numDivisions);  
        sphere.setAppearance(createAppearance(null, false));  
        return primitive(sphere, x, y, z, new Vector3d(xA, yA, zA), new Vector3d(w, h, d));  
    }  
   
    public static Shape cube (double x, double y, double z, double r) {  
        return cube(x, y, z, r, 0, 0, 0, null);  
    }  
   
     
    public static Shape cube (double x, double y, double z, double r, double xA, double yA, double zA) {  
        return cube(x, y, z, r, xA, yA, zA, null);  
    }  
   
        
    public static Shape cube (double x, double y, double z, double r, String imageURL) {  
        return cube(x, y, z, r, 0, 0, 0, imageURL);  
    }  
   
     
    public static Shape cube (double x, double y, double z, double r, double xA, double yA, double zA, String imageURL) {  
        return box(x, y, z, r, r, r, xA, yA, zA, imageURL);  
    }  
   
     
    public static Shape wireCube (double x, double y, double z, double r, double xA, double yA, double zA) {  
   
        return wireBox(x, y, z, r, r, r, 0, 0, 0);  
    }  
   
     
    public static Shape wireCube (double x, double y, double z, double r) {  
   
        double[] xC = new double[]  
            { x+r, x+r, x-r, x-r, x+r, x+r, x+r, x-r, x-r, x+r, x+r, x+r, x-r, x-r, x-r, x-r };  
        double[] yC = new double[]  
            { y+r, y-r, y-r, y+r, y+r, y+r, y-r, y-r, y+r, y+r, y-r, y-r, y-r, y-r, y+r, y+r };  
        double[] zC = new double[]  
            { z+r, z+r, z+r, z+r, z+r, z-r, z-r, z-r, z-r, z-r, z-r, z+r, z+r, z-r, z-r, z+r };  
   
        return lines(xC, yC, zC);  
    }  
   
    public static Shape box (double x, double y, double z, double w, double h, double d) {  
        return box(x, y, z, w, h, d, 0, 0, 0, null);  
    }  
   
     
    public static Shape box (double x, double y, double z, double w, double h, double d, double xA, double yA, double zA) {  
        return box(x, y, z, w, h, d, xA, yA, zA, null);  
    }  
   
     
    public static Shape box (double x, double y, double z, double w, double h, double d, String imageURL) {  
        return box(x, y, z, w, h, d, 0, 0, 0, imageURL);  
    }  
   
     
    public static Shape box (double x, double y, double z, double w, double h, double d, double xA, double yA, double zA, String imageURL) {  
   
        Appearance ap = createAppearance(imageURL, true);  
   
        Vector3f dimensions = createVector3f(w, h, d);  
   
        com.sun.j3d.utils.geometry.Box box = new  
            com.sun.j3d.utils.geometry.Box(dimensions.x, dimensions.y, dimensions.z, PRIMFLAGS, ap, numDivisions);  
        return primitive(box, x, y, z, new Vector3d(xA, yA, zA), null);  
    }  
   
     
    public static Shape wireBox (double x, double y, double z, double w, double h, double d) {  
        return wireBox(x, y, z, w, h, d, 0, 0, 0);  
    }  
   
     
    public static Shape wireBox (double x, double y, double z, double w, double h, double d, double xA, double yA, double zA) {  
   
        Appearance ap = createAppearance(null, false);  
   
        Vector3f dimensions = createVector3f(w, h, d);  
   
        com.sun.j3d.utils.geometry.Box box = new  
            com.sun.j3d.utils.geometry.Box(dimensions.x, dimensions.y, dimensions.z, PRIMFLAGS, ap, numDivisions);  
        return primitive(box, x, y, z, new Vector3d(xA, yA, zA), null);  
    }  
   
    public static Shape cylinder (double x, double y, double z, double r, double h) {  
        return cylinder(x, y, z, r, h, 0, 0, 0, null);  
    }  
   
     
    public static Shape cylinder (double x, double y, double z, double r, double h, double xA, double yA, double zA) {  
        return cylinder(x, y, z, r, h, xA, yA, zA, null);  
    }  
   
     
    public static Shape cylinder (double x, double y, double z, double r, double h, String imageURL) {  
        return cylinder(x, y, z, r, h, 0, 0, 0, imageURL);  
    }  
   
     
    public static Shape cylinder (double x, double y, double z, double r, double h, double xA, double yA, double zA, String imageURL) {  
   
        Appearance ap = createAppearance(imageURL, true);  
        Vector3f dimensions = createVector3f(r, h, 0);  
        Cylinder cyl = new Cylinder (dimensions.x, dimensions.y, PRIMFLAGS, numDivisions, numDivisions, ap);  
        return primitive(cyl, x, y, z, new Vector3d(xA, yA, zA), null);  
    }  
   
     
    public static Shape wireCylinder (double x, double y, double z, double r, double h) {  
        return wireCylinder(x, y, z, r, h, 0, 0, 0);  
    }  
   
     
    public static Shape wireCylinder (double x, double y, double z, double r, double h, double xA, double yA, double zA) {  
   
        Appearance ap = createAppearance(null, false);  
        Vector3f dimensions = createVector3f(r, h, 0);  
        Cylinder cyl = new Cylinder (dimensions.x, dimensions.y, PRIMFLAGS, numDivisions, numDivisions, ap);  
        return primitive(cyl, x, y, z, new Vector3d(xA, yA, zA), null);  
    }  
   
    public static Shape cone (double x, double y, double z, double r, double h) {  
        return cone(x, y, z, r, h, 0, 0, 0, null);  
    }  
   
     
    public static Shape cone (double x, double y, double z, double r, double h, double xA, double yA, double zA) {  
        return cone(x, y, z, r, h, xA, yA, zA, null);  
    }  
   
     
    public static Shape cone (double x, double y, double z, double r, double h, String imageURL) {  
        return cone(x, y, z, r, h, 0, 0, 0, imageURL);  
    }  
   
     
    public static Shape cone (double x, double y, double z, double r, double h, double xA, double yA, double zA, String imageURL) {  
   
        Appearance ap = createAppearance(imageURL, true);  
        Vector3f dimensions = createVector3f(r, h, 0);  
        Cone cone = new Cone (dimensions.x, dimensions.y, PRIMFLAGS, numDivisions, numDivisions, ap);  
        return primitive(cone, x, y, z, new Vector3d(xA, yA, zA), null);  
    }  
   
     
    public static Shape wireCone (double x, double y, double z, double r, double h) {  
        return wireCone(x, y, z, r, h, 0, 0, 0);  
    }  
   
     
    public static Shape wireCone (double x, double y, double z, double r, double h, double xA, double yA, double zA) {  
   
        Appearance ap = createAppearance(null, false);  
        Vector3f dimensions = createVector3f(r, h, 0);  
        Cone cone = new Cone (dimensions.x, dimensions.y, PRIMFLAGS, numDivisions, numDivisions, ap);  
        return primitive(cone, x, y, z, new Vector3d(xA, yA, zA), null);  
    }  
   
    private static Shape primitive (Primitive shape, double x, double y, double z, Vector3d angles, Vector3d scales) {  
   
        shape.setCapability(Primitive.ENABLE_APPEARANCE_MODIFY);  
        shape.setPickable(false);  
        shape.setCollidable(false);  
   
        TransformGroup tgScale = createTransformGroup();  
        Transform3D scaleTransform = new Transform3D();  
        if (scales != null)  
            scaleTransform.setScale(scales);  
        tgScale.setTransform(scaleTransform);  
        tgScale.addChild(shape);  
   
        TransformGroup tgShape = createTransformGroup();  
        Transform3D transform = new Transform3D();  
        if (angles != null) {  
            angles.scale(Math.PI / 180);  
            transform.setEuler( angles);  
        }  
        Vector3f vector = createVector3f(x, y, z);  
        transform.setTranslation(vector);  
        tgShape.setTransform(transform);  
        tgShape.addChild(tgScale);  
   
        BranchGroup bg = createBranchGroup();  
        bg.addChild(tgShape);  
        offscreenGroup.addChild(bg);  
   
        return new Shape(bg, tgShape);  
    }  
   
     
   
     
    public static Shape point (double x, double y, double z) {  
   
        return points(new double[] {x}, new double[] {y}, new double[] {z});  
    }  
   
     
    public static Shape points (double[] x, double[] y, double[] z) {  
   
        Point3f[] coords = constructPoint3f(x, y, z);  
   
        GeometryArray geom = new PointArray(coords.length, PointArray.COORDINATES);  
        geom.setCoordinates(0, coords);  
   
        Shape3D shape = createShape3D(geom);  
   
        return shape(shape);  
    }  
   
     
    public static Shape points (double[] x, double[] y, double[] z, Color[] colors) {  
   
        Point3f[] coords = constructPoint3f(x, y, z);  
   
        GeometryArray geom = new PointArray(coords.length, PointArray.COORDINATES | PointArray.COLOR_4);  
        geom.setCoordinates(0, coords);  
   
        for (int i = 0; i < x.length; i++)  
            geom.setColor(i, colors[i].getComponents(null));  
   
        Shape3D shape = createShape3D(geom);  
   
        return customShape(shape);  
    }  
   
    public static Shape line (double x1, double y1, double z1, double x2, double y2, double z2) {  
   
        return lines(new double[] {x1, x2}, new double[] {y1, y2}, new double[] {z1, z2});  
    }  
   
     
    public static Shape lines (double[] x, double[] y, double[] z) {  
   
        Point3f[] coords = constructPoint3f(x, y, z);  
   
        GeometryArray geom = new LineStripArray  
            (coords.length, LineArray.COORDINATES, new int[] {coords.length});  
        geom.setCoordinates(0, coords);  
   
        Shape3D shape = createShape3D(geom);  
   
        return shape(shape);  
    }  
   
     
    public static Shape lines (double[] x, double[] y, double[] z, Color[] colors) {  
   
        Point3f[] coords = constructPoint3f(x, y, z);  
   
        GeometryArray geom = new LineStripArray  
            (coords.length, LineArray.COORDINATES | LineArray.COLOR_4, new int[] {coords.length});  
        geom.setCoordinates(0, coords);  
   
        for (int i = 0; i < x.length; i++)  
            geom.setColor(i, colors[i].getComponents(null));  
   
        Shape3D shape = createShape3D(geom);  
   
        return customShape(shape);  
    }  
   
     
    public static Shape tube (double x1, double y1, double z1, double x2, double y2, double z2, double r) {  
   
        Vector3D mid = new Vector3D(x1 + x2, y1 + y2, z1 + z2).times(0.5);  
        Vector3D line = new Vector3D(x2 - x1, y2 - y1, z2 - z1);  
   
        Shape s = cylinder(mid.x, mid.y, mid.z, r, line.mag());  
   
        Vector3D yAxis = new Vector3D(0, 1, 0);  
        Vector3D cross = line.cross(yAxis);  
        double angle = line.angle(yAxis);  
        s.rotateAxis(cross, -angle);  
   
        return combine(s);  
    }  
   
     
    public static Shape tubes (double[] x, double[] y, double[] z, double r) {  
   
        StdDraw3D.Shape[] shapes = new StdDraw3D.Shape[(x.length-1) * 2];  
   
        for (int i = 0; i < x.length - 1; i++) {  
            shapes[i]  = tube(x[i], y[i], z[i], x[i+1], y[i+1], z[i+1], r);  
            shapes[i + x.length - 1] = sphere(x[i+1], y[i+1], z[i+1], r);  
        }  
   
        return combine(shapes);  
    }  
   
     
    public static Shape tubes (double[] x, double[] y, double[] z, double r, Color[] colors) {  
   
        StdDraw3D.Shape[] shapes = new StdDraw3D.Shape[(x.length-1) * 2];  
   
        for (int i = 0; i < x.length - 1; i++) {  
            StdDraw3D.setPenColor(colors[i]);  
            shapes[i]  = tube(x[i], y[i], z[i], x[i+1], y[i+1], z[i+1], r);  
            shapes[i + x.length - 1] = sphere(x[i+1], y[i+1], z[i+1], r);  
        }  
   
        return combine(shapes);  
    }  
   
    public static Shape polygon (double[] x, double[] y, double[] z) {  
        return polygon(x, y, z, true);  
    }  
   
     
    public static Shape wirePolygon (double[] x, double[] y, double[] z) {  
        return polygon(x, y, z, false);  
    }  
   
     
    private static Shape polygon (double[] x, double[] y, double[] z, boolean filled) {  
   
        Point3f[] coords = constructPoint3f(x, y, z);  
        GeometryArray geom = new TriangleFanArray(coords.length, LineArray.COORDINATES, new int[] {coords.length});  
        geom.setCoordinates(0, coords);  
   
        GeometryInfo geoinfo = new GeometryInfo((GeometryArray)geom);  
        NormalGenerator normalGenerator = new NormalGenerator();  
        normalGenerator.generateNormals(geoinfo);  
   
        Shape3D shape = createShape3D(geoinfo.getIndexedGeometryArray());  
   
        if (filled)  
            return shape(shape);  
        else  
            return wireShape(shape);  
    }  
   
    public static Shape triangles (double[][] points) {  
        return triangles(points, true);  
    }  
   
    public static Shape wireTriangles (double[][] points) {  
        return triangles(points, false);  
    }  
   
    private static Shape triangles (double[][] points, boolean filled) {  
   
        int size = points.length;  
        Point3f[] coords = new Point3f[size*3];  
   
        for (int i = 0; i < size; i++) {  
            coords[3*i]   = new Point3f(createVector3f(points[i][0], points[i][1], points[i][2]));  
            coords[3*i+1] = new Point3f(createVector3f(points[i][3], points[i][4], points[i][5]));  
            coords[3*i+2] = new Point3f(createVector3f(points[i][6], points[i][7], points[i][8]));  
        }  
   
        GeometryArray geom = new TriangleArray(size*3, TriangleArray.COORDINATES);  
        geom.setCoordinates(0, coords);  
   
        GeometryInfo geoinfo = new GeometryInfo(geom);  
        NormalGenerator normalGenerator = new NormalGenerator();  
        normalGenerator.generateNormals(geoinfo);  
   
        Shape3D shape = createShape3D(geoinfo.getIndexedGeometryArray());  
   
        if (filled)  
            return shape(shape);  
        else  
            return wireShape(shape);  
    }  
   
     
    public static Shape triangles (double[][] points, Color[] colors) {  
        return triangles(points, colors, true);  
    }  
   
     
    public static Shape wireTriangles (double[][] points, Color[] colors) {  
        return triangles(points, colors, false);  
    }  
   
    private static Shape triangles (double[][] points, Color[] colors, boolean filled) {  
        int size = points.length;  
        Point3f[] coords = new Point3f[size*3];  
   
        for (int i = 0; i < size; i++) {  
            coords[3*i]   = new Point3f(createVector3f(points[i][0], points[i][1], points[i][2]));  
            coords[3*i+1] = new Point3f(createVector3f(points[i][3], points[i][4], points[i][5]));  
            coords[3*i+2] = new Point3f(createVector3f(points[i][6], points[i][7], points[i][8]));  
        }  
   
        GeometryArray geom = new TriangleArray(size*3, TriangleArray.COORDINATES | TriangleArray.COLOR_4);  
        geom.setCoordinates(0, coords);  
   
        for (int i = 0; i < colors.length; i++) {  
            geom.setColor(3 * i + 0, colors[i].getComponents(null));  
            geom.setColor(3 * i + 1, colors[i].getComponents(null));  
            geom.setColor(3 * i + 2, colors[i].getComponents(null));  
        }  
   
        GeometryInfo geoinfo = new GeometryInfo(geom);  
        NormalGenerator normalGenerator = new NormalGenerator();  
        normalGenerator.generateNormals(geoinfo);  
   
        Shape3D shape = createShape3D(geoinfo.getIndexedGeometryArray());  
   
        if (filled)  
            return shape(shape);  
        else  
            return wireShape(shape);  
    }  
   
    public static Shape text3D (double x, double y, double z, String text) {  
   
        return text3D(x, y, z, text, 0, 0, 0);  
    }  
   
     
    public static Shape text3D (double x, double y, double z, String text, double xA, double yA, double zA) {  
   
        Line2D.Double line = new Line2D.Double(0, 0, TEXT3D_DEPTH, 0);  
        FontExtrusion extrudePath = new FontExtrusion(line);  
        Font3D font3D = new Font3D(font, extrudePath);  
        Point3d pos = new Point3d(x, y, z);  
        javax.media.j3d.Text3D t = new javax.media.j3d.Text3D(font3D, text, createPoint3f(x, y, z));  
   
        // FIX THIS TO NOT HAVE SCALE INCLUDED  
        Transform3D shrinker = new Transform3D();  
        shrinker.setEuler(new Vector3d(xA, yA, zA));  
        shrinker.setTranslation(new Vector3d(x, y, z));  
        shrinker.setScale(TEXT3D_SHRINK_FACTOR);  
        Shape3D shape = createShape3D((Geometry)t);  
        return shape(shape, true, shrinker, false);  
    }  
   
    private static Point3f[] constructPoint3f (double[] x, double[] y, double[] z) {  
   
        int size = x.length;  
        Point3f[] coords = new Point3f[size];  
   
        for (int i = 0; i < size; i++)  
            coords[i] = new Point3f(createVector3f(x[i], y[i], z[i]));  
   
        return coords;  
    }  
   
     
    private static Shape drawPLY (String filename, boolean colored) {  
   
        Scanner scanner = createScanner(filename);  
   
        int vertices = -1;  
        int triangles = -1;  
        int properties = -1;  
   
        while (true) {  
            String s = scanner.next();  
            if (s.equals("vertex"))  
                vertices = scanner.nextInt();  
            else if (s.equals("face"))  
                triangles = scanner.nextInt();  
            else if (s.equals("property")) {  
                properties++;  
                scanner.next();  
                scanner.next();  
            }  
            else if (s.equals("end_header"))  
                break;  
        }  
   
        System.out.println(vertices + " " + triangles + " " + properties);  
   
        if ((vertices == -1) || (triangles == -1) || (properties == -1))  
            throw new RuntimeException("Cannot read format of .ply file!");  
   
        double[][] parameters = new double[properties][vertices];  
   
        for (int i = 0; i < vertices; i++) {  
            if ((i % 10000) == 0)  
                System.out.println("vertex " + i);  
            for (int j = 0; j < properties; j++) {  
                parameters[j][i] = scanner.nextDouble();  
            }  
        }  
   
        double[][] points = new double[triangles][9];  
   
        for (int i = 0; i < triangles; i++) {  
            int edges = scanner.nextInt();  
            if (edges != 3)  
                throw new RuntimeException("Only triangular faces supported!");  
   
            if ((i % 10000) == 0)  
                System.out.println("face " + i);  
   
            int index = scanner.nextInt();  
            points[i][0] = parameters[0][index];  
            points[i][1] = parameters[1][index];  
            points[i][2] = parameters[2][index];  
   
            index = scanner.nextInt();  
            points[i][3] = parameters[0][index];  
            points[i][4] = parameters[1][index];  
            points[i][5] = parameters[2][index];  
   
            index = scanner.nextInt();  
            points[i][6] = parameters[0][index];  
            points[i][7] = parameters[1][index];  
            points[i][8] = parameters[2][index];  
        }  
   
        return triangles(points);  
    }  
   
    private static Shape drawLWS (String filename) {  
   
        Lw3dLoader loader = new Lw3dLoader();  
        try {  
            BranchGroup bg = loader.load(filename).getSceneGroup();  
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);  
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);  
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);  
            bg.setCapability(BranchGroup.ALLOW_DETACH);  
   
            TransformGroup transGroup = new TransformGroup();  
            transGroup.addChild(bg);  
            BranchGroup bg2 = createBranchGroup();  
            bg2.addChild(transGroup);  
            offscreenGroup.addChild(bg2);  
            return new Shape(bg2, transGroup);  
        } catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }  
        return null;  
    }  
   
    private static Shape drawOBJ (String filename, boolean colored, boolean resize) {  
   
        int params = 0;  
        if (resize) params = ObjectFile.RESIZE | Loader.LOAD_ALL;  
   
        ObjectFile loader = new ObjectFile(params);  
        try {  
            BranchGroup bg = loader.load(filename).getSceneGroup();  
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);  
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);  
            bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);  
            bg.setCapability(BranchGroup.ALLOW_DETACH);  
   
            //System.out.println("Children: " + bg.numChildren());  
   
            for (int i = 0; i < bg.numChildren(); i++) {  
                Node child = bg.getChild(i);  
                if (child instanceof Shape3D) {  
                    Shape3D shape = (Shape3D) child;  
                    //System.out.println("shape3d");  
                    //                     Appearance ap = shape.getAppearance();  
                    //                     PolygonAttributes pa = ap.getPolygonAttributes();  
                    //                     if (pa == null) pa = new PolygonAttributes();  
                    //                     pa.setCullFace(PolygonAttributes.CULL_NONE);  
                    //                     ap.setPolygonAttributes(pa);  
                    //                     Material m = ap.getMaterial();  
                    //                     m.setSpecularColor(new Color3f(GRAY));  
                    //                     m.setShininess(64);  
                    if (colored)  
                        shape.setAppearance(createAppearance(null, true));  
                    else {  
                        Appearance ap = shape.getAppearance();  
                        PolygonAttributes pa = ap.getPolygonAttributes();  
                        if (pa == null) pa = new PolygonAttributes();  
                        pa.setCullFace(PolygonAttributes.CULL_NONE);  
                        ap.setPolygonAttributes(pa);  
                    }  
                    //for (int j = 0; j < shape.numGeometries(); j++) {  
                    //    Geometry g = shape.getGeometry(j);  
                    //    if (g instanceof GeometryArray) {  
                    //        GeometryArray ga = (GeometryArray) g;  
                    //System.out.println("GeometryArray");  
                    //System.out.println("format: " + ga.getVertexFormat());  
                    //float[] colors = ga.getInterleavedVertices();  
                    //for (int k = 0; k < colors.length; k++)  
                    //    System.out.println(colors[k]);  
                    //    }  
                    //}  
                }  
            }  
   
            TransformGroup transGroup = new TransformGroup();  
            transGroup.addChild(bg);  
            BranchGroup bg2 = createBranchGroup();  
            bg2.addChild(transGroup);  
            offscreenGroup.addChild(bg2);  
            return new Shape(bg2, transGroup);  
        } catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }  
        return null;  
    }  
   
    public static Shape model (String filename) {  
        return model(filename, false);  
    }  
   
    public static Shape model (String filename, boolean resize) {  
        return model(filename, false, resize);  
    }  
   
    public static Shape coloredModel (String filename) {  
        return model(filename, true, true);  
    }  
   
    public static Shape coloredModel (String filename, boolean resize) {  
        return model(filename, true, resize);  
    }  
   
    private static Shape model (String filename, boolean colored, boolean resize) {  
   
        if (filename == null) return null;  
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);  
        String extension = suffix.toLowerCase();  
   
        if (suffix.equals("ply"))  
            return drawPLY(filename, colored);  
        else if (suffix.equals("obj"))  
            return drawOBJ(filename, colored, resize);  
        //else if (suffix.equals("lws"))  
        //    return drawLWS(filename);  
        else  
            throw new RuntimeException("Format not supported!");  
    }  
   
     
    private static Shape shape (Shape3D shape) {  
        return shape(shape, true, null, false);  
    }  
   
     
    private static Shape wireShape (Shape3D shape) {  
        return shape(shape, false, null, false);  
    }  
   
    private static Shape customShape (Shape3D shape) {  
        return shape(shape, true, null, true);  
    }  
   
    private static Shape customWireShape (Shape3D shape) {  
        return shape(shape, false, null, true);  
    }  
   
     
    private static Shape shape (Shape3D shape, boolean fill, Transform3D transform, boolean custom) {  
   
        Appearance ap;  
   
        if (custom) ap = createCustomAppearance(fill);  
        else        ap = createAppearance(null, fill);  
   
        shape.setAppearance(ap);  
   
        TransformGroup transGroup = new TransformGroup();  
        if (transform != null)  
            transGroup.setTransform(transform);  
   
        transGroup.addChild(shape);  
   
        BranchGroup bg = createBranchGroup();  
        bg.addChild(transGroup);  
        offscreenGroup.addChild(bg);  
        return new Shape(bg, transGroup);  
    }  
   
     
   
     
    public static void overlayPixel (double x, double y) {  
        getGraphics2D(offscreenImage).fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);  
    }  
   
     
    public static void overlayPoint (double x, double y) {  
        float r = penRadius;  
        if (r <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).fill(new Ellipse2D.Double(scaleX(x) - r/2, scaleY(y) - r/2, r, r));  
    }  
   
     
    public static void overlayLine (double x0, double y0, double x1, double y1) {  
        getGraphics2D(offscreenImage).draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));  
    }  
   
     
    public static void overlayCircle (double x, double y, double r) {  
   
        if (r < 0) throw new RuntimeException("circle radius can't be negative");  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*r);  
        double hs = factorY(2*r);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));  
    }  
   
     
    public static void overlayFilledCircle (double x, double y, double r) {  
   
        if (r < 0) throw new RuntimeException("circle radius can't be negative");  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*r);  
        double hs = factorY(2*r);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));  
    }  
   
     
    public static void overlayEllipse (double x, double y, double semiMajorAxis, double semiMinorAxis) {  
        if (semiMajorAxis < 0) throw new RuntimeException("ellipse semimajor axis can't be negative");  
        if (semiMinorAxis < 0) throw new RuntimeException("ellipse semiminor axis can't be negative");  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*semiMajorAxis);  
        double hs = factorY(2*semiMinorAxis);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));  
    }  
   
     
    public static void overlayFilledEllipse (double x, double y, double semiMajorAxis, double semiMinorAxis) {  
        if (semiMajorAxis < 0) throw new RuntimeException("ellipse semimajor axis can't be negative");  
        if (semiMinorAxis < 0) throw new RuntimeException("ellipse semiminor axis can't be negative");  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*semiMajorAxis);  
        double hs = factorY(2*semiMinorAxis);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));  
    }  
   
     
    public static void overlayArc (double x, double y, double r, double angle1, double angle2) {  
        if (r < 0) throw new RuntimeException("arc radius can't be negative");  
        while (angle2 < angle1) angle2 += 360;  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*r);  
        double hs = factorY(2*r);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).draw(new Arc2D.Double(xs - ws/2, ys - hs/2, ws, hs, angle1, angle2 - angle1, Arc2D.OPEN));  
    }  
   
     
    public static void overlaySquare (double x, double y, double r) {  
        if (r < 0) throw new RuntimeException("square side length can't be negative");  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*r);  
        double hs = factorY(2*r);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));  
    }  
   
     
    public static void overlayFilledSquare (double x, double y, double r) {  
        if (r < 0) throw new RuntimeException("square side length can't be negative");  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*r);  
        double hs = factorY(2*r);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));  
    }  
   
     
    public static void overlayRectangle (double x, double y, double halfWidth, double halfHeight) {  
        if (halfWidth  < 0) throw new RuntimeException("half width can't be negative");  
        if (halfHeight < 0) throw new RuntimeException("half height can't be negative");  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*halfWidth);  
        double hs = factorY(2*halfHeight);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));  
    }  
   
     
    public static void overlayFilledRectangle (double x, double y, double halfWidth, double halfHeight) {  
        if (halfWidth  < 0) throw new RuntimeException("half width can't be negative");  
        if (halfHeight < 0) throw new RuntimeException("half height can't be negative");  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(2*halfWidth);  
        double hs = factorY(2*halfHeight);  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else getGraphics2D(offscreenImage).fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));  
    }  
   
     
    public static void overlayPolygon (double[] x, double[] y) {  
        int N = x.length;  
        GeneralPath path = new GeneralPath();  
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));  
        for (int i = 0; i < N; i++)  
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));  
        path.closePath();  
        getGraphics2D(offscreenImage).draw(path);  
    }  
   
     
    public static void overlayFilledPolygon (double[] x, double[] y) {  
        int N = x.length;  
        GeneralPath path = new GeneralPath();  
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));  
        for (int i = 0; i < N; i++)  
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));  
        path.closePath();  
        getGraphics2D(offscreenImage).fill(path);  
    }  
   
     
    public static void overlayText (double x, double y, String text) {  
        Graphics2D graphics = getGraphics2D(offscreenImage);  
        FontMetrics metrics = graphics.getFontMetrics();  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        int ws = metrics.stringWidth(text);  
        int hs = metrics.getDescent();  
        graphics.drawString(text, (float) (xs - ws/2.0), (float) (ys + hs));  
    }  
   
     
    public static void overlayText (double x, double y, String text, double degrees) {  
        Graphics2D graphics = getGraphics2D(offscreenImage);  
        FontMetrics metrics = graphics.getFontMetrics();  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        int ws = metrics.stringWidth(text);  
        int hs = metrics.getDescent();  
        graphics.rotate(Math.toRadians(-degrees), xs, ys);  
        graphics.drawString(text, (float) (xs - ws/2.0), (float) (ys + hs));  
        graphics.rotate(Math.toRadians(+degrees), xs, ys);  
    }     
   
     
    public static void overlayTextLeft (double x, double y, String text) {  
        Graphics2D graphics = getGraphics2D(offscreenImage);  
        FontMetrics metrics = graphics.getFontMetrics();  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        int ws = metrics.stringWidth(text);  
        int hs = metrics.getDescent();  
        graphics.drawString(text, (float) (xs), (float) (ys + hs));  
    }  
   
     
    public static void overlayTextRight (double x, double y, String text) {  
        Graphics2D graphics = getGraphics2D(offscreenImage);  
        FontMetrics metrics = graphics.getFontMetrics();  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        int ws = metrics.stringWidth(text);  
        int hs = metrics.getDescent();  
        graphics.drawString(text, (float) (xs - ws), (float) (ys + hs));  
    }  
   
     
    public static void overlayPicture (double x, double y, String s) {  
        Image image = getImage(s);  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        int ws = image.getWidth(null);  
        int hs = image.getHeight(null);  
        if (ws < 0 || hs < 0) throw new RuntimeException("image " + s + " is corrupt");  
        getGraphics2D(offscreenImage).drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);  
    }  
   
     
    public static void overlayPicture (double x, double y, String s, double degrees) {  
        Image image = getImage(s);  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        int ws = image.getWidth(null);  
        int hs = image.getHeight(null);  
        if (ws < 0 || hs < 0) throw new RuntimeException("image " + s + " is corrupt");  
   
        Graphics2D graphics = getGraphics2D(offscreenImage);  
        graphics.rotate(Math.toRadians(-degrees), xs, ys);  
        graphics.drawImage(image, (int) Math.round(xs - ws/2.0), (int) Math.round(ys - hs/2.0), null);  
        graphics.rotate(Math.toRadians(+degrees), xs, ys);  
    }  
   
     
    public static void overlayPicture (double x, double y, String s, double w, double h) {  
        Image image = getImage(s);  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        if (w < 0) throw new RuntimeException("width is negative: " + w);  
        if (h < 0) throw new RuntimeException("height is negative: " + h);  
        double ws = factorX(w);  
        double hs = factorY(h);  
        if (ws < 0 || hs < 0) throw new RuntimeException("image " + s + " is corrupt");  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
        else {  
            getGraphics2D(offscreenImage).drawImage(image, (int) Math.round(xs - ws/2.0),  
                (int) Math.round(ys - hs/2.0),  
                (int) Math.round(ws),  
                (int) Math.round(hs), null);  
        }  
    }  
   
     
    public static void overlayPicture (double x, double y, String s, double w, double h, double degrees) {  
        Image image = getImage(s);  
        double xs = scaleX(x);  
        double ys = scaleY(y);  
        double ws = factorX(w);  
        double hs = factorY(h);  
        if (ws < 0 || hs < 0) throw new RuntimeException("image " + s + " is corrupt");  
        if (ws <= 1 && hs <= 1) overlayPixel(x, y);  
   
        Graphics2D graphics = getGraphics2D(offscreenImage);  
        graphics.rotate(Math.toRadians(-degrees), xs, ys);  
        graphics.drawImage(image, (int) Math.round(xs - ws/2.0),  
            (int) Math.round(ys - hs/2.0),  
            (int) Math.round(ws),  
            (int) Math.round(hs), null);  
        graphics.rotate(Math.toRadians(+degrees), xs, ys);  
    }  
   
     
    private static Image getImage (String filename) {  
   
        // to read from file  
        ImageIcon icon = new ImageIcon(filename);  
   
        // try to read from URL  
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {  
            try {  
                URL url = new URL(filename);  
                icon = new ImageIcon(url);  
            } catch (Exception e) { }  
        }  
   
        // in case file is inside a .jar  
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {  
            URL url = StdDraw3D.class.getResource(filename);  
            if (url == null) throw new RuntimeException("image " + filename + " not found");  
            icon = new ImageIcon(url);  
        }  
   
        return icon.getImage();  
    }  
   
    private static Graphics2D getGraphics2D (BufferedImage image) {  
   
        Graphics2D graphics = (Graphics2D) image.getGraphics();  
        graphics.setColor(penColor);  
        graphics.setFont(font);  
        BasicStroke stroke = new BasicStroke(penRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);  
        graphics.setStroke(stroke);  
   
        //if (getAntiAliasing())  
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
   
        return graphics;  
    }  
   
    private static void infoDisplay () {  
   
        if (!infoDisplay) {  
            infoImage = createBufferedImage();  
            return;  
        }  
   
        BufferedImage bi = createBufferedImage();  
        Graphics2D g = (Graphics2D) bi.getGraphics();  
        g.setFont(new Font("Courier", Font.PLAIN, 11));  
        g.setStroke(new BasicStroke(  
                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));  
   
        double center = (min + max) / 2;  
        double r  = zoom;  
        double b = zoom * 0.1f;  
   
        DecimalFormat df = new DecimalFormat(" 0.000;-0.000");  
   
        Vector3D pos = camera.getPosition();  
        String s = "(" + df.format(pos.x) + "," + df.format(pos.y) + "," + df.format(pos.z) + ")";  
        g.setColor(BLACK);  
        g.drawString("Position: " + s, 21, 26);  
        g.setColor(LIGHT_GRAY);  
        g.drawString("Position: " + s, 20, 25);  
   
        Vector3D rot = camera.getOrientation();  
        String s2 = "(" + df.format(rot.x) + "," + df.format(rot.y) + "," + df.format(rot.z) + ")";  
        g.setColor(BLACK);  
        g.drawString("Rotation: " + s2, 21, 41);  
        g.setColor(LIGHT_GRAY);  
        g.drawString("Rotation: " + s2, 20, 40);  
   
        String mode;  
        if (cameraMode == ORBIT_MODE)        mode = "Camera: ORBIT_MODE";  
        else if (cameraMode == FPS_MODE)          mode = "Camera: FPS_MODE";  
        else if (cameraMode == AIRPLANE_MODE)     mode = "Camera: AIRPLANE_MODE";  
        else if (cameraMode == LOOK_MODE)         mode = "Camera: LOOK_MODE";  
        else if (cameraMode == FIXED_MODE)        mode = "Camera: FIXED_MODE";  
        else throw new RuntimeException("Unknown camera mode!");  
   
        g.setColor(BLACK);  
        g.drawString(mode, 21, 56);  
        g.setColor(LIGHT_GRAY);  
        g.drawString(mode, 20, 55);  
   
        double d = b / 4;  
        g.draw(new Line2D.Double(scaleX(d + center), scaleY(0 + center), scaleX(-d + center), scaleY(0 + center)));  
        g.draw(new Line2D.Double(scaleX(0 + center), scaleY(d + center), scaleX( 0 + center), scaleY(-d + center)));  
   
        infoImage = bi;  
    }  
     
   
     
    public static void save (String filename) {  
   
        //canvas.setVisible(false);  
        int oldCameraMode = getCameraMode();  
        setCameraMode(FIXED_MODE);  
   
        GraphicsContext3D context = canvas.getGraphicsContext3D();  
   
        BufferedImage buf = createBufferedImage();  
        ImageComponent2D imageComp = new ImageComponent2D(ImageComponent.FORMAT_RGB, buf);  
        javax.media.j3d.Raster ras = new javax.media.j3d.Raster(  
                new Point3f(-1.0f,-1.0f,-1.0f),  
                javax.media.j3d.Raster.RASTER_COLOR,  
                0, 0,  
                width, height,  
                imageComp, null);  
   
        context.readRaster(ras);  
   
        BufferedImage image = (ras.getImage()).getImage();  
   
        File file = new File(filename);  
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);  
   
        String extension = suffix.toLowerCase();  
   
        // png files  
        if (extension.equals("png")) {  
            try { ImageIO.write(image, suffix, file); }  
            catch (IOException e) { e.printStackTrace(); }  
        }  
   
        // need to change from ARGB to RGB for jpeg  
        // reference: http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727  
        else if (extension.equals("jpg")) {  
            WritableRaster raster = image.getRaster();  
            WritableRaster newRaster;  
            newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, new int[] {0, 1, 2});  
            BufferedImage rgbBuffer = new BufferedImage(image.getColorModel(), newRaster, false,  null);  
            try { ImageIO.write(rgbBuffer, suffix, file); }  
            catch (IOException e) { e.printStackTrace(); }  
        }  
        else {  
            System.out.println("Invalid image file type: " + suffix);  
        }  
   
        setCameraMode(oldCameraMode);  
        //canvas.setVisible(true);  
    }  
   
    public static void saveScene3D (String filename) {  
   
        File file = new File(filename);  
        //System.out.println("on: " + onscreenGroup.numChildren() + " off: " + offscreenGroup.numChildren());  
   
        try {  
            SceneGraphFileWriter writer = new SceneGraphFileWriter(file, universe, false, "3D scene saved from StdDraw3D.", null);  
            writer.writeBranchGraph(offscreenGroup);  
            writer.close();  
            System.out.println("Scene successfully written to " + filename + "!");  
        }  
        catch (IOException ioe)                  { ioe.printStackTrace(); }  
        catch (UnsupportedUniverseException uue) { uue.printStackTrace(); }   
        //catch (NamedObjectException noe)         { noe.printStackTrace(); }  
    }  
   
    public static void loadScene3D (String filename) {  
   
        File file = new File(filename);  
   
        try {  
            SceneGraphFileReader reader = new SceneGraphFileReader(file);  
            System.out.println("Branch graph count = " + reader.getBranchGraphCount());  
            //BranchGroup[] bgs = reader.readAllBranchGraphs();  
            //BranchGroup bg = (BranchGroup) reader.getNamedObject("rendered");  
            BranchGroup bg = reader.readBranchGraph(0)[0];  
            offscreenGroup = bg;  
            //reader.dereferenceBranchGraph(offscreenGroup);  
            System.out.println("Scene successfully loaded from " + filename + "!");  
        }  
        catch (IOException ioe)                  { ioe.printStackTrace(); }  
        //catch (ObjectNotLoadedException onle)    { onle.printStackTrace(); }   
        //catch (NamedObjectException noe)         { noe.printStackTrace(); }  
    }  
   
     
   
     
    public static Shape combine (Shape... shapes) {  
   
        BranchGroup    combinedGroup = createBranchGroup();  
        TransformGroup combinedTransform = new TransformGroup();  
   
        for (int i = 0; i < shapes.length; i++) {  
            BranchGroup bg = shapes[i].bg;  
            TransformGroup tg = shapes[i].tg;  
   
            offscreenGroup.removeChild(bg);  
            onscreenGroup.removeChild(bg);  
   
            bg.removeChild(tg);  
            combinedTransform.addChild(shapes[i].tg);  
        }  
   
        combinedGroup.addChild(combinedTransform);  
        offscreenGroup.addChild(combinedGroup);  
        return new Shape(combinedGroup, combinedTransform);  
    }  
   
     
    public static Shape copy (Shape shape) {  
        TransformGroup tg = shape.tg;  
        BranchGroup bg = shape.bg;  
        TransformGroup tg2 = (TransformGroup)tg.cloneTree();  
        BranchGroup bg2 = createBranchGroup();  
   
        bg2.addChild(tg2);  
        offscreenGroup.addChild(bg2);  
   
        return new Shape(bg2, tg2);  
    }  
   
     
    private static class Transformable {  
   
        private TransformGroup tg;  
   
        private Transformable (TransformGroup tg0) {  
            this.tg = tg0;  
        }  
   
        private Transform3D getTransform () {  
            Transform3D t = new Transform3D();  
            tg.getTransform(t);  
            return t;  
        }  
   
        private void setTransform (Transform3D t) {  
            tg.setTransform(t);  
        }  
   
        private Vector3D relToAbs (Vector3D r) {  
            Transform3D t = getTransform();  
   
            Matrix3d m = new Matrix3d();  
            t.get(m);  
            Vector3d zero = new Vector3d(0, 0, 0);  
            Transform3D rotation = new Transform3D(m, zero, 1.0);  
            Vector3f vec = createVector3f(r);  
            rotation.transform(vec);  
   
            return new Vector3D(vec);  
        }  
   
        private Vector3D absToRel (Vector3D r) {  
            Transform3D t = getTransform();  
   
            Matrix3d m = new Matrix3d();  
            t.get(m);  
            Vector3d zero = new Vector3d(0, 0, 0);  
            Transform3D rotation = new Transform3D(m, zero, 1.0);  
            Vector3f vec = createVector3f(r);  
            rotation.invert();  
            rotation.transform(vec);  
   
            return new Vector3D(vec);  
        }  
   
        private void rotateQuat (double x, double y, double z, double w) {  
            rotateQuat(new Quat4d(x, y, z, w));  
        }  
   
        private void rotateQuat (Quat4d quat) {  
            Transform3D t = getTransform();  
   
            Transform3D t1 = new Transform3D();  
            t1.setRotation(quat);  
            t.mul(t1);  
   
            setTransform(t);  
        }  
   
        private void setQuaternion (double x, double y, double z, double w) {  
            setQuaternion(new Quat4d(x, y, z, w));  
        }  
   
        private void setQuaternion (Quat4d quat) {  
            Transform3D t = getTransform();  
   
            t.setRotation(quat);  
   
            setTransform(t);  
        }  
   
        private Quat4d getQuaternion () {  
            Transform3D t = getTransform();  
   
            Matrix3d m = new Matrix3d();  
            t.get(m);  
   
            double w = Math.sqrt(Math.max(0, 1 + m.m00 + m.m11 + m.m22))/2;  
            double x = Math.sqrt(Math.max(0, 1 + m.m00 - m.m11 - m.m22))/2;  
            double y = Math.sqrt(Math.max(0, 1 - m.m00 + m.m11 - m.m22))/2;  
            double z = Math.sqrt(Math.max(0, 1 - m.m00 - m.m11 + m.m22))/2;  
            if (m.m21 - m.m12 < 0) x = -x;  
            if (m.m02 - m.m20 < 0) y = -y;  
            if (m.m10 - m.m01 < 0) z = -z;  
   
            return new Quat4d(x, y, z, w);  
        }  
   
        private void orientAxis (Vector3D axis, double angle) {  
            Transform3D t = getTransform();  
   
            AxisAngle4d aa = new AxisAngle4d(axis.x, axis.y, axis.z, Math.toRadians(angle));  
            t.setRotation(aa);  
   
            setTransform(t);  
        }  
   
        public void rotateAxis (Vector3D axis, double angle) {  
            if(angle == 0) return;  
            Transform3D t = getTransform();  
   
            Vector3D aRel = absToRel(axis);  
            AxisAngle4d aa = new AxisAngle4d(aRel.x, aRel.y, aRel.z, Math.toRadians(angle));  
            Transform3D t1 = new Transform3D();  
            t1.setRotation(aa);  
            t.mul(t1);  
   
            setTransform(t);  
        }  
   
        public void move (double x, double y, double z) {  
            move(new Vector3D(x, y, z));  
        }  
   
        public void move (Vector3D move) {  
            Transform3D t = getTransform();  
   
            Vector3f r = new Vector3f();  
            t.get(r);  
            r.add(createVector3f(move));  
            t.setTranslation(r);  
   
            setTransform(t);  
        }  
   
        public void moveRelative (double right, double up, double forward) {  
            moveRelative(new Vector3D(right, up, forward));  
        }  
   
        public void moveRelative (Vector3D move) {  
            move(relToAbs(move.times(1, 1, -1)));  
        }  
   
        public void setPosition (double x, double y, double z) {  
            setPosition(new Vector3D(x, y, z));  
        }  
   
        public void setPosition (Vector3D pos) {  
            Transform3D t = getTransform();  
   
            t.setTranslation(createVector3f(pos));  
   
            setTransform(t);  
        }  
   
        public Vector3D getPosition () {  
            Transform3D t = getTransform();  
            Vector3d r = new Vector3d();  
            t.get(r);  
            return new Vector3D(r);  
        }  
   
        public void rotate (double xAngle, double yAngle, double zAngle) {  
            rotate(new Vector3D(xAngle, yAngle, zAngle));  
        }  
   
        public void rotate (Vector3D angles) {  
            Transform3D t = getTransform();  
   
            Transform3D tX = new Transform3D();  
            Transform3D tY = new Transform3D();  
            Transform3D tZ = new Transform3D();  
   
            Vector3D xR = absToRel(xAxis);  
            Vector3D yR = absToRel(yAxis);  
            Vector3D zR = absToRel(zAxis);  
   
            Vector3D radians = angles.times(Math.PI / 180.);  
            tX.setRotation(new AxisAngle4d(xR.x, xR.y, xR.z, radians.x));  
            tY.setRotation(new AxisAngle4d(yR.x, yR.y, yR.z, radians.y));  
            tZ.setRotation(new AxisAngle4d(zR.x, zR.y, zR.z, radians.z));  
   
            t.mul(tX);  
            t.mul(tY);  
            t.mul(tZ);  
   
            setTransform(t);  
        }  
   
        public void rotateRelative (double pitch, double yaw, double roll) {  
            rotateRelative(new Vector3D(pitch, yaw, roll));  
        }  
   
        public void rotateRelative (Vector3D angles) {  
            Transform3D t = getTransform();  
   
            Transform3D tX = new Transform3D();  
            Transform3D tY = new Transform3D();  
            Transform3D tZ = new Transform3D();  
   
            Vector3D radians = angles.times(Math.PI / 180.);  
            tX.setRotation(new AxisAngle4d(1, 0, 0, radians.x));  
            tY.setRotation(new AxisAngle4d(0, 1, 0, radians.y));  
            tZ.setRotation(new AxisAngle4d(0, 0, 1, radians.z));  
   
            t.mul(tX);  
            t.mul(tY);  
            t.mul(tZ);  
   
            setTransform(t);  
        }  
   
        public void setOrientation (double xAngle, double yAngle, double zAngle) {  
            setOrientation(new Vector3D(xAngle, yAngle, zAngle));  
        }  
   
        public void setOrientation (Vector3D angles) {  
            if (Math.abs(angles.y) == 90)  
                System.err.println("Gimbal lock when the y-angle is vertical!");  
   
            Transform3D t = getTransform();  
   
            Vector3D radians = angles.times(Math.PI / 180.);  
            Transform3D t1 = new Transform3D();  
            t1.setEuler(createVector3d(radians));  
            Vector3d r = new Vector3d();  
            t.get(r);  
            t1.setTranslation(r);  
            t1.setScale(t.getScale());  
   
            setTransform(t1);  
        }  
   
        public Vector3D getOrientation () {  
            Transform3D t = getTransform();  
   
            Matrix3d mat = new Matrix3d();  
            t.get(mat);  
   
            double xA, yA, zA;  
   
            yA = -Math.asin(mat.m20);  
            double C = Math.cos(yA);  
            if ( Math.abs(C) > 0.005 ) {  
                xA  = -Math.atan2( -mat.m21 / C, mat.m22 / C );  
                zA  = -Math.atan2( -mat.m10 / C, mat.m00 / C );  
            } else {  
                xA  = 0;         
                zA  = -Math.atan2( mat.m01, mat.m11 );  
            }  
   
            xA = Math.toDegrees(xA);  
            yA = Math.toDegrees(yA);  
            zA = Math.toDegrees(zA);  
   
             
            if (xA < 0) xA += 360;  
            if (yA < 0) yA += 360;  
            if (zA < 0) zA += 360;  
   
            return new Vector3D(xA, yA, zA);  
        }  
   
        public void lookAt (Vector3D center) {  
            lookAt(center, yAxis);     
        }  
   
        public void lookAt (Vector3D center, Vector3D up) {  
            Transform3D t = getTransform();  
   
            Transform3D t2 = new Transform3D(t);  
   
            Vector3f translation = new Vector3f();  
            t2.get(translation);  
   
            Vector3d scales = new Vector3d();  
            t2.getScale(scales);  
   
            Point3d trans = new Point3d(translation);  
            Point3d c = new Point3d(center.x, center.y, center.z);  
            Vector3d u = createVector3d(up);  
            t2.lookAt(trans, c, u);  
   
            try {  
                t2.invert();  
                t2.setScale(scales);  
                setTransform(t2);  
            } catch (SingularMatrixException sme) { System.out.println("Singular matrix, bad lookAt()!"); }  
   
        }  
   
        public void setDirection (Vector3D direction) {  
            setDirection(direction, yAxis);  
        }  
   
        public void setDirection (Vector3D direction, Vector3D up) {  
            Vector3D center = getPosition().plus(direction);  
            lookAt(center, up);  
        }  
   
        public Vector3D getDirection () {  
            return relToAbs(zAxis.times(-1)).direction();  
        }  
   
        private void match (Transformable s) {  
            setOrientation(s.getOrientation());  
            setPosition(s.getPosition());  
        }  
    }  
   
    public static Vector3D getCameraPosition () {  
        return camera.getPosition();  
    }  
   
    public static Vector3D getCameraOrientation () {  
        return camera.getOrientation();  
    }  
   
    public static Vector3D getCameraDirection () {  
        return camera.getDirection();  
    }  
   
    public static void setCameraPosition (double x, double y, double z) {  
        setCameraPosition(new Vector3D(x, y, z));  
    }  
   
    public static void setCameraPosition (Vector3D position) {  
        camera.setPosition(position);  
    }  
   
    public static void setCameraOrientation (double xAngle, double yAngle, double zAngle) {  
        setCameraOrientation(new Vector3D(xAngle, yAngle, zAngle));  
    }  
   
    public static void setCameraOrientation (Vector3D angles) {  
        camera.setOrientation(angles);  
    }  
   
    public static void setCameraDirection (double x, double y, double z) {  
        setCameraDirection(new Vector3D(x, y, z));  
    }  
   
    public static void setCameraDirection (Vector3D direction) {  
        camera.setDirection(direction);  
    }  
   
    public static void setCamera (double x, double y, double z, double xAngle, double yAngle, double zAngle) {  
        camera.setPosition(x, y, z);  
        camera.setOrientation(xAngle, yAngle, zAngle);  
    }  
   
    public static void setCamera (Vector3D position, Vector3D angles) {  
        camera.setPosition(position);  
        camera.setOrientation(angles);  
    }  
   
     
    public static Camera camera () {  
        return camera;  
    }  
   
     
    public static class Camera extends Transformable {  
   
        private TransformGroup tg;  
        private Shape pair;  
   
        private Camera (TransformGroup tg) {  
            super(tg);  
            this.tg = tg;  
        }  
   
        public void match (Shape s) {  
            super.match(s);  
        }  
   
        public void pair (Shape s) {  
            pair = s;  
        }  
   
        public void unpair () {  
            pair = null;  
        }  
   
        public void moveRelative (Vector3D move) {  
            if ((view.getProjectionPolicy() == View.PARALLEL_PROJECTION)) {  
                setScreenScale(view.getScreenScale() * (1 + move.z / zoom));  
                super.move(super.relToAbs(move.times(1, 1, 0)));  
            } else super.move(super.relToAbs(move.times(1, 1, -1)));  
        }  
   
        public void rotateFPS (Vector3D angles) {  
            rotateFPS(angles.x, angles.y, angles.z);  
        }  
   
        public void rotateFPS (double xAngle, double yAngle, double zAngle) {  
   
            double xA = Math.toRadians(xAngle);  
            double yA = Math.toRadians(yAngle);  
            double zA = Math.toRadians(zAngle);  
   
            Vector3D shift = super.relToAbs(new Vector3D(-yA, xA, zA));  
            Vector3D dir = super.getDirection().plus(shift);  
            double angle = dir.angle(yAxis);  
            if (angle > 90) angle = 180 - angle;  
            if (angle < 5) return;  
            super.setDirection(super.getDirection().plus(shift));  
        }  
    }  
   
     
    public static class Shape extends Transformable {  
   
        private BranchGroup bg;  
        private TransformGroup tg;  
   
        private Shape (BranchGroup bg, TransformGroup tg) {  
            super(tg);  
            this.bg = bg;  
            this.tg = tg;  
            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);  
            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);  
        }  
   
        public void scale (double scale) {  
            Transform3D t = super.getTransform();  
   
            t.setScale(t.getScale() * scale);  
   
            super.setTransform(t);  
        }  
   
        //         public void scale (double xScale, double yScale, double zScale) {  
        //             scale(new Vector3D(xScale, yScale, zScale));  
        //         }  
        //          
        //         public void scale (Vector3D scales) {  
        //             Transform3D t = getTransform();  
        //             throw new UnsupportedOperationException("Doesn't work with rotation!");  
        //             setTransform(t);  
        //         }  
   
        public void hide () {  
            offscreenGroup.removeChild(bg);  
            onscreenGroup.removeChild(bg);  
        }  
   
        public void unhide () {  
            hide();  
            offscreenGroup.addChild(bg);  
        }  
   
        public void match (Shape s) {  
            super.match(s);  
        }  
   
        public void match (Camera c) {  
            super.match(c);  
        }  
   
        public void setColor (Color c) {  
            setColor(tg, c);  
        }  
   
        public void setColor (Color c, int alpha) {  
            setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));  
        }  
   
        private void setColor (Group g, Color c) {  
            for (int i = 0; i < g.numChildren(); i++) {  
                Node child = g.getChild(i);  
                if (child instanceof Shape3D) {  
                    Shape3D shape = (Shape3D)child;  
                    Appearance ap = shape.getAppearance();  
                    setColor(ap, c);  
                } else if (child instanceof Primitive) {  
                    Primitive primitive = (Primitive)child;  
                    Appearance ap = primitive.getAppearance();  
                    setColor(ap, c);  
                } else if (child instanceof Group) {  
                    setColor((Group)child, c);  
                }  
            }  
        }  
   
        private void setColor (Appearance ap, Color c) {  
            Material m = ap.getMaterial();  
            m.setAmbientColor(new Color3f(c));  
            m.setDiffuseColor(new Color3f(c));  
   
            float alpha = ((float)c.getAlpha()) / 255;  
            if (alpha < 1.0) {  
                TransparencyAttributes t = new TransparencyAttributes();  
                t.setTransparencyMode(TransparencyAttributes.BLENDED);  
                t.setTransparency(1 - alpha);  
                ap.setTransparencyAttributes(t);  
            } else ap.setTransparencyAttributes(null);  
        }  
    }  
   
     
    public static class Light extends Transformable {  
   
        javax.media.j3d.Light light;  
        BranchGroup bg;  
   
        private Light (BranchGroup bg, TransformGroup tg, javax.media.j3d.Light light) {  
            super(tg);  
            this.light = light;  
            this.bg = bg;  
        }  
   
        public void hide () {  
            light.setEnable(false);  
        }  
   
        public void unhide () {  
            light.setEnable(true);  
        }  
   
        public void match (Shape s) {  
            super.match(s);  
        }  
   
        public void match (Camera c) {  
            super.match(c);  
        }  
   
        public void setColor (Color col) {  
            light.setColor(new Color3f(col));  
        }  
   
        public void scalePower (double power) {  
   
            if (light instanceof PointLight) {  
   
                double attenuationScale = 1.0 / (0.999 * power + 0.001);  
   
                PointLight pl = (PointLight)light;  
                Point3f attenuation = new Point3f();  
                pl.getAttenuation(attenuation);  
                attenuation.y *= attenuationScale;  
                attenuation.z *= attenuationScale * attenuationScale;  
   
                pl.setAttenuation(attenuation);  
            } else {  
                System.err.println("Can only scale power for point lights!");  
            }  
        }  
    }  
   
     
    public static class Vector3D {  
   
         
        public final double x;  
   
         
        public final double y;  
   
         
        public final double z;  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D () {  
   
            this.x = 0;  
            this.y = 0;  
            this.z = 0;  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D (double x, double y, double z) {  
   
            this.x = x;  
            this.y = y;  
            this.z = z;  
        }  
   
         
        //--------------------------------------------------------------------------  
   
        public Vector3D (double[] c) {  
   
            if (c.length != 3)  
                throw new RuntimeException("Incorrect number of dimensions!");  
            this.x = c[0];  
            this.y = c[1];  
            this.z = c[2];  
        }  
   
        //--------------------------------------------------------------------------  
   
        private Vector3D (Vector3d v) {  
   
            this.x = v.x;  
            this.y = v.y;  
            this.z = v.z;  
        }  
   
        //--------------------------------------------------------------------------  
   
        private Vector3D (Vector3f v) {  
   
            this.x = v.x;  
            this.y = v.y;  
            this.z = v.z;  
        }  
   
        //--------------------------------------------------------------------------  
   
        private Vector3D (Point3d p) {  
   
            this.x = p.x;  
            this.y = p.y;  
            this.z = p.z;  
   
        }  
   
        //--------------------------------------------------------------------------  
   
        private Vector3D (Point3f p) {  
   
            this.x = p.x;  
            this.y = p.y;  
            this.z = p.z;  
   
        }  
   
        //--------------------------------------------------------------------------  
         
        //--------------------------------------------------------------------------  
         
        public double dot (Vector3D that) {  
   
            return (this.x * that.x + this.y * that.y + this.z * that.z);  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public double mag () {  
   
            return Math.sqrt(this.dot(this));  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public double angle (Vector3D that) {  
            return Math.toDegrees(Math.acos(this.dot(that)/(this.mag() * that.mag())));  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public double distanceTo (Vector3D that) {  
            return this.minus(that).mag();  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D plus (Vector3D that) {  
   
            double cx = this.x + that.x;  
            double cy = this.y + that.y;  
            double cz = this.z + that.z;  
            Vector3D c = new Vector3D(cx, cy, cz);  
            return c;  
        }  
   
        public Vector3D plus (double x, double y, double z) {  
            double cx = this.x + x;  
            double cy = this.y + y;  
            double cz = this.z + z;  
            return new Vector3D(cx, cy, cz);  
        }  
        //--------------------------------------------------------------------------  
   
         
        public Vector3D minus (Vector3D that) {  
   
            double cx = this.x - that.x;  
            double cy = this.y - that.y;  
            double cz = this.z - that.z;  
            Vector3D c = new Vector3D(cx, cy, cz);  
            return c;  
        }  
   
        public Vector3D minus (double x, double y, double z) {  
            double cx = this.x - x;  
            double cy = this.y - y;  
            double cz = this.z - z;  
            return new Vector3D(cx, cy, cz);  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D times (double k) {  
   
            return times(k, k, k);  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D times (double a, double b, double c) {  
   
            double vx = this.x * a;  
            double vy = this.y * b;  
            double vz = this.z * c;  
            Vector3D v = new Vector3D(vx, vy, vz);  
            return v;  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D direction () {  
   
            if (this.mag() == 0.0) throw new RuntimeException("Zero-vector has no direction");  
            return this.times(1.0 / this.mag());  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D proj (Vector3D line) {  
   
            Vector3D normal = line.direction();  
   
            return normal.times(this.dot(normal));  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D cross (Vector3D that) {  
   
            Vector3D a = this;  
            Vector3D b = that;  
   
            return new Vector3D(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public Vector3D reflect (Vector3D line) {  
   
            return this.proj(line).times(2).minus(this);  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public String toString() {  
   
            DecimalFormat df = new DecimalFormat("0.000000");  
            return ("( " + df.format(this.x) + ", " + df.format(this.y) + ", " + df.format(this.z) + " )");  
        }  
   
        //--------------------------------------------------------------------------  
   
         
        public void draw () {  
   
            StdDraw3D.sphere(this.x, this.y, this.z, 0.01);  
        }  
   
        //--------------------------------------------------------------------------  
   
   
         
         
    }  
   
    public static void main (String[] args) {  
   
        // Sets the scale  
        StdDraw3D.setScale(-1, 1);  
   
        // Turns off the default info HUD display.  
        StdDraw3D.setInfoDisplay(false);  
   
        // Draws the white square border.  
        StdDraw3D.setPenColor(StdDraw3D.WHITE);  
        StdDraw3D.overlaySquare(0, 0, 0.98);  
   
        // Draws the two red circles.  
        StdDraw3D.setPenRadius(0.06);  
        StdDraw3D.setPenColor(StdDraw3D.RED, 220);  
        StdDraw3D.overlayCircle(0, 0, 0.8);  
        StdDraw3D.setPenColor(StdDraw3D.RED, 220);  
        StdDraw3D.overlayCircle(0, 0, 0.6);  
   
        // Draws the information text.  
        StdDraw3D.setPenColor(StdDraw3D.WHITE);  
        StdDraw3D.overlayText(0, 0.91, "Standard Draw 3D - Test Program");  
        StdDraw3D.overlayText(0, -0.95, "You should see rotating text. Drag the mouse to orbit.");  
   
        // Creates the 3D text object and centers it.  
        StdDraw3D.setPenColor(StdDraw3D.YELLOW);  
        StdDraw3D.setFont(new Font("Arial", Font.BOLD, 16));  
        StdDraw3D.Shape text = StdDraw3D.text3D(0, 0, 0, "StdDraw3D");  
        text.scale(3.5);  
        text.move(-0.7, -0.1, 0);  
        text = StdDraw3D.combine(text);  
   
        while (true) {  
   
            // Rotates the 3D text by 1.2 degrees along the y-axis.  
            text.rotate(0, 1.2, 0);  
   
            // Shows the frame for 20 milliseconds.  
            StdDraw3D.show(20);  
        }  
    }  
}  
