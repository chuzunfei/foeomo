package line;

import javax.media.j3d.*;  

public class lineShape2 extends Shape3D {  
    int StripCount[] = new int[1];  
  
    private float vert[] = {   
        .8f, 0.8f,0.0f,  
        -0.8f, 0.8f,0.0f,  
        0.5f, 0.0f,0.0f,  
        -0.5f, 0.0f,0.0f,  
        -0.8f,-0.8f,0.0f,  
        0.8f,-0.8f,0.0f,  
       };  
  
    private float color[] = {  
          0.0f,0.5f,1.0f,  
        0.5f,0.0f,1.0f,  
        0.0f,0.8f,2.0f,  
        1.0f,0.0f,0.3f,  
        0.0f,1.0f,0.3f,  
        0.3f,0.8f,0.0f,  
      };  
  
    public lineShape2() {
        StripCount[0] = 6;  
  
        LineStripArray line = new LineStripArray(6,   
                  LineStripArray.COORDINATES|  
                  LineStripArray.COLOR_3,StripCount);  
          line.setCoordinates(0,vert);  
          line.setColors(0,color);  
        LineAttributes la = new LineAttributes();  
          la.setLineWidth(30.0f);  
          la.setLineAntialiasingEnable(true);  
          la.setLinePattern(LineAttributes.PATTERN_DASH);  
        Appearance ap = new Appearance();  
         ap.setLineAttributes(la);  
        this.setGeometry(line);  
        this.setAppearance(ap);   
    }  
}
