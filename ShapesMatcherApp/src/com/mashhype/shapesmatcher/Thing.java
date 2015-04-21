package com.mashhype.shapesmatcher;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;

public class Thing extends ShapeDrawable {

	//rectangle made of floats
	private RectF bounds = new RectF();
	private Type type;
	private int position;
	public static enum Type {
		Square, Circle, Triangle;
	}
	
	public Thing() {

	}
	
	public Thing(Type type) {
		this.type = type;
	}

	public RectF getRectFBounds() {
		return bounds;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getPosition() {
		return position;
	}
	
	//method to swap shape positions
	public RectF swap() {
		return bounds;
	}
	
	
	
}
