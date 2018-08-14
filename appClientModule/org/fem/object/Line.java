package org.fem.object;

import java.util.List;

import javax.media.j3d.LineStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public class Line {
	
	
	private List<Point> endpoints;

	private float[] vertex;
	
	public float[] getVertex() {
		return vertex;
	}

	public void setVertex(float[] vertex) {
		this.vertex = vertex;
	}

	public List<Point> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<Point> endpoints) {
		this.endpoints = endpoints;
	}
	
	public LineStripArray generateLineShape() {
		System.out.println("dianshu:"+endpoints.size());
//		vertex = new float[endpoints.size()*3];
//		for (int i=0;i<endpoints.size();i++) {
//			vertex[0+3*i] = endpoints.get(i).getX();
//			vertex[1+3*i] = endpoints.get(i).getY();
//			vertex[2+3*i] = endpoints.get(i).getZ();
//		}
//		 int StripCount[] = new int[1];
//		 StripCount[0] = 3;
//		 LineStripArray line = new LineStripArray(3,   
//                 LineStripArray.COORDINATES|  
//                 LineStripArray.COLOR_3,StripCount);  
//         line.setCoordinates(0,vertex);
//         float color[] = {  
//                 0.0f,0.5f,1.0f,  
//               0.5f,0.0f,1.0f,  
//               0.0f,0.8f,2.0f,  
//             };
//         line.setColors(0,color);  
         
         Point3f verts[] = new Point3f[endpoints.size()];
         Color3f colors[] = new Color3f[endpoints.size()];
        
         for (int i=0;i<endpoints.size();i++) {
        	 verts[i] = new Point3f( endpoints.get(i).getX(),  endpoints.get(i).getY(),  endpoints.get(i).getZ());
        	 colors[i] = new Color3f(1.0f, 0.0f, 0.0f);
         }
         int pointNum = (endpoints.size()-1)*2;
         Point3f pnts[] = new Point3f[pointNum];
         Color3f clrs[] = new Color3f[pointNum];
         for (int i=0;i<pointNum;i++) {
        	 pnts[i] = verts[(i+1)/2];
        	 clrs[i] = colors[(i+1)/2];
         }
         
//         Point3f pnts[] = new Point3f[4];
//         Color3f clrs[] = new Color3f[4];
//         pnts[0] = verts[0];
//         clrs[0] = colors[0];
//         pnts[1] = verts[1];
//         clrs[1] = colors[1];
//         pnts[2] = verts[1];
//         clrs[2] = colors[1];
//         pnts[3] = verts[2];
//         clrs[3] = colors[2];
         
         int StripCount[] = new int[endpoints.size()-1];
         for (int i=0;i<StripCount.length;i++) {
        	 StripCount[i] = 2;
         }
		 LineStripArray line = new LineStripArray(pointNum,   
       LineStripArray.COORDINATES|  
       LineStripArray.COLOR_3,StripCount); 
         line.setCoordinates(0, pnts);
         line.setColors(0, clrs);
		return line;
	}
}
