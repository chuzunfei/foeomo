
import javax.media.j3d.*;  
  
public class pointShape extends Shape3D {  
  
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
  
    public pointShape() {  
  
        int[] index={ 0 , 2 , 3 , 4 };  
        int VertexCount=4;  
        IndexedPointArray point = new IndexedPointArray(6,   
                            IndexedPointArray.COORDINATES|  
                            IndexedPointArray.COLOR_3,  
                            VertexCount);  
          point.setCoordinates(0,vert);  
          point.setColors(0,color);  
          point.setCoordinateIndices(0,index);  
          point.setColorIndices(0,index);  
        PointAttributes pa = new PointAttributes();  
          pa.setPointSize(20.0f);  
          pa.setPointAntialiasingEnable(true);  
        Appearance ap = new Appearance();  
         ap.setPointAttributes(pa);  
        this.setGeometry(point);  
        this.setAppearance(ap);   
    }  

}
