package line;

import javax.media.j3d.*;  

public class lineShape4 extends Shape3D {  
    int[] index={ 1, 0, 0 , 3, };  
    int VertexCount=4;  
  
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
  
    public lineShape4() {  
  
        IndexedLineArray line = new IndexedLineArray(6,   
                  IndexedLineArray.COORDINATES|  
                  IndexedLineArray.COLOR_3,VertexCount);  
          line.setCoordinates(0,vert);  
          line.setColors(0,color);  
          line.setCoordinateIndices(0,index);  
          line.setColorIndices(0,index);  
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
