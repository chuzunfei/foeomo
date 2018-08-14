package com.java3d.dennist.study;

import javax.media.j3d.Appearance;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;

public class LineShape extends Shape3D{

    // 直线的定点坐标
    private float vert[ ]={
                0.5f,0.5f,0.0f, -0.5f,0.5f,0.0f,
                0.3f,0.0f,0.0f, -0.3f,0.0f,0.0f,
                -0.5f,-0.5f,0.0f, 0.5f,-0.5f,0.0f};
    // 各定点的颜色
    private float color[ ]={
               0.0f,0.5f,1.0f,  0.0f,0.5f,1.0f,
               0.0f,0.8f,2.0f,  1.0f,0.0f,0.3f,
               0.0f,1.0f,0.3f,  0.3f,0.8f,0.0f};
    
    public LineShape( ) {
        // 创建直线数组对象
        LineArray line=new LineArray(6,LineArray.COORDINATES|LineArray.COLOR_3);
        // 设置直线对象的坐标数组
        line.setCoordinates(0,vert);
        // 设置直线对象的颜色数组
        line.setColors(0,color);
        // 创建直线属性对象
        LineAttributes linea=new LineAttributes( );
        // 设置线宽
        linea.setLineWidth(10.0f);
        // 设置直线的渲染效果
        linea.setLineAntialiasingEnable(true);
 
        Appearance app=new Appearance( );  
        app.setLineAttributes(linea);
        this.setGeometry(line);
        this.setAppearance(app);
    }

}