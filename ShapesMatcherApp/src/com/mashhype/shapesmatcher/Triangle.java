package com.mashhype.shapesmatcher;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.drawable.shapes.Shape;

public class Triangle extends Shape {

	private float strokeSize;
	//path is synonymous with line, represents a "line" from point a to point b
	private Path path;
	
	public Triangle(float strokeSize) {
		this.strokeSize = strokeSize;
	}
	
	//when the size of the shape changes, recalculate its path
	@Override protected void onResize(float width, float height) {
		super.onResize(width, height);
		path = new Path();
		path.moveTo(width/2, 0);
		path.lineTo(0, height);
		path.lineTo(width, height);
		path.close();
	}

	//this method draws the triangle and paints it
	@Override
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawPath(path, paint);
		
		//paint the stroke (border) black
		//paint.setColor(Color.BLACK);
		//paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(strokeSize);
		canvas.drawPath(path, paint);

	}

}
