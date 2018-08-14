package line;

import javax.media.j3d.*;  

public class lineShape1 extends Shape3D {  
  
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
  
    public lineShape1() {  
        LineArray line = new LineArray(6,   
                  LineArray.COORDINATES|LineArray.COLOR_3);  
          line.setCoordinates(0,vert);  
          line.setColors(0,color);  
        LineAttributes la = new LineAttributes();  
          la.setLineWidth(5.0f);  
          la.setLineAntialiasingEnable(true);  
        Appearance ap = new Appearance();  
         ap.setLineAttributes(la);  
        this.setGeometry(line);  
        this.setAppearance(ap);   
    }  
} 
