package com.vic.java3d;

import com.sun.j3d.utils.geometry.*;  
import com.sun.j3d.utils.universe.*;  
import javax.media.j3d.*;  
import javax.vecmath.*;  
  
public class TestJava3DHello {  
 public TestJava3DHello(){  
   // 创建一个虚拟空间  
     SimpleUniverse universe = new SimpleUniverse();  
     // 创建一个用来包含对象的数据结构  
     BranchGroup group = new BranchGroup();  
      
     // 创建一个球并把它加入到group中  
     Sphere sphere = new Sphere(0.5f); // 小球的半径为0.5米  
     group.addChild(sphere);  
      
     Color3f light1Color = new Color3f(1.8f, 0.1f, 0.1f);  
     // 设置光线的颜色  
     BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);  
     // 设置光线的作用范围  
     Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);  
     // 设置光线的方向  
     DirectionalLight light1= new DirectionalLight(light1Color, light1Direction);  
     // 指定颜色和方向，产生单向光源  
     light1.setInfluencingBounds(bounds);  
     // 把光线的作用范围加入光源中  
     group.addChild(light1);  
     // 将光源加入group组,安放观察点  
     universe.getViewingPlatform().setNominalViewingTransform();  
     // 把group加入到虚拟空间中  
     universe.addBranchGraph(group);  
 }  
 public static void main(String[] args) {  
  new TestJava3DHello();  
 }  
}
