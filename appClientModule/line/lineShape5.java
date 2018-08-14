package line;

import javax.media.j3d.*;  

public class lineShape5 extends Shape3D {  
    int StripCount[] = new int[3];  
    int[] index={ 0 , 1 , 3 , 2 , 3 , 5 ,  
                  4 , 6 , 7 , 8 , 6 } ;  
       int vertexCount = 11;  
    private float vert[] = {   
        -.3f , .8f , .0f,  
         .3f , .8f , .0f,  
        -.3f , .4f , .0f,  
         .3f , .4f , .0f,  
        -.3f , .0f , .0f,  
         .3f , .0f , .0f,  
        -.3f , -.4f , .0f,  
         .3f , -.4f , .0f,  
        -.3f , -.8f , .0f,  
         .3f , -.8f , .0f,  
       };  
  
    private float color[] = {  
        0.0f,0.5f,1.0f,  
        0.5f,0.0f,1.0f,  
        0.0f,0.8f,2.0f,  
        1.0f,0.0f,0.3f,  
        0.0f,1.0f,0.3f,  
        0.3f,0.8f,0.0f,  
        0.0f,0.5f,1.0f,  
        0.5f,0.0f,1.0f,  
        0.0f,0.8f,2.0f,  
        1.0f,0.0f,0.3f  
      };  
  
    public lineShape5() {  
              StripCount[0] = 4;  
              StripCount[1] = 3;  
              StripCount[2] = 4;  
        IndexedLineStripArray line = new IndexedLineStripArray(10 ,  
                  IndexedLineStripArray.COORDINATES|  
                  IndexedLineStripArray.COLOR_3, vertexCount , StripCount);  
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
