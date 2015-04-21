package com.mashhype.shapesmatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mashhype.shapesmatcher.Thing.Type;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
	//Global Variables
	private Paint paint;
	boolean shuffle = false;
	boolean swapStatus;
	boolean changeColors = true;
	boolean drag = false;
	int color;
	int shuffleCount;
	private Player player = new Player();
	Random random = new Random();
	int rand;
	private Rect rect = new Rect();
	private List<RectF> positions = new ArrayList<RectF>();
	float left, top, right, bottom;
	private ShapeDrawable triangle = new ShapeDrawable(new Triangle(10));
	private ShapeDrawable square = new ShapeDrawable(new RectShape());
	private ShapeDrawable circle = new ShapeDrawable(new OvalShape());
	private List<ShapeDrawable> shapes = new ArrayList<ShapeDrawable>();
			
	private List<Thing> things = new ArrayList<Thing>();
	private Thing selected;
	private Thing thing;
	private List<Type> type = new ArrayList<Type>(Arrays.asList(Type.values()));

	private float selectionOffsetX;
	private float selectionOffsetY;
	//End Global Variables
	
	public DrawingView(Context context) {
		super(context);
	}
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	//for each position, create a shape and set
	//its type and shape.  Use random number
	//generator to populate the positions greater
	//than 2
	private void initializeComponents() {
		for(int i = 0; i < positions.size(); i++) {
			thing = new Thing();
			left = positions.get(i).left + positions.get(i).width()/4;
			top = positions.get(i).top + positions.get(i).height()/4;
			right = positions.get(i).right - positions.get(i).width()/4;
			bottom = positions.get(i).bottom - positions.get(i).height()/4;
			thing.getRectFBounds().set(left, top, right, bottom);
			rand = random.nextInt(shapes.size());
			if (positions.indexOf(positions.get(i)) > shapes.size() - 1) {
				//thing.setShape(shapes.get(rand).getShape());
				thing.setType(type.get(rand));
				thing.setPosition(positions.indexOf(positions.get(i)));
				things.add(thing);
			} else {
				//thing.setShape(shapes.get(i).getShape());
				thing.setType(type.get(i));
				thing.setPosition(positions.indexOf(positions.get(i)));
				things.add(thing);
			}
		}
	}
	
	//returns the shape at the given x, y coordinates
	private Thing findThingAt(float x, float y) {
		for(int i = things.size() - 1; i >= 0; i--) {
			Thing thing = things.get(i);
			if (things.get(i).getRectFBounds().contains(x, y)) {
				return thing;
			}
		}
		return null;
	}// end findThingsAt
	
	//returns true/false based on whether user
	//tapped inside the game board but not on a shape
	private boolean shuffleShapes(float x, float y) {
		boolean shuffle = true;
		RectF gameBoard = new RectF(50, 50, getWidth()-50, getHeight() - 50);
		if(gameBoard.contains(x, y)) {
			for(int j = things.size() - 1; j >= 0; j--) {
				if (things.get(j).getRectFBounds().contains(x, y)) {
					shuffle = false;
					break;
				} 
			}
		}
		//Log.d("SHUFFLE SHAPES", "" + shuffle);
		return shuffle;
	}//end shuffleShapes
	
	private boolean getDrag() {
		return drag;
	}
	
	
	
	private boolean dragOutsideBoard(float x, float y) {
		RectF gameboard = new RectF(50, 50, getWidth()-50, getHeight() - 50);
		if(!gameboard.contains(x, y)) {
			drag = true;
		}
		return drag;
	}
	
	private void printShape() {
		for(Thing thing : things) {
			Log.d("Line Break", "--------------------------------");
			Log.d("Shape Name", "" + thing.getType());
			Log.d("Shape Position", "" + thing.getPosition());
		}
	}
	
	//swaps two shapes regardless
	//of whether they are next to each
	//other or not.  they just need
	//to come into contact with each other
	private void swapShapes() {
		boolean status = false;
		RectF bounds = selected.getRectFBounds();
		for(Thing thing: things) {
			if(RectF.intersects(bounds, thing.getRectFBounds())
					&& selected.getPosition() != thing.getPosition()) {
				int thingPosition = thing.getPosition();
				int selectedPosition = selected.getPosition();
				//Log.d("SELECTED POSITION pre swap", "" + selected.getPosition());
				//Log.d("THING pre swap", "" + thing.getPosition());
				RectF swap;
				
				swap = positions.get(thingPosition);
				left = swap.left + swap.width()/4;
				top = swap.top + swap.height()/4;
				right = swap.right - swap.width()/4;
				bottom = swap.bottom - swap.height()/4;
				selected.getRectFBounds().set(left, top, right, bottom);
				
				swap = positions.get(selectedPosition);
				left = swap.left + swap.width()/4;
				top = swap.top + swap.height()/4;
				right = swap.right - swap.width()/4;
				bottom = swap.bottom - swap.height()/4;
				thing.getRectFBounds().set(left, top, right, bottom);
				
				selected.setPosition(thingPosition);
				thing.setPosition(selectedPosition);
				
				status =  true;
				//Log.d("SELECTED POSITION post swap", "" + selected.getPosition());
				//Log.d("THING post swap", "" + thing.getPosition());
			} else
				status = false;
		}
	}
		
	//iterates over the shapes in the grid
	//and updates the row with new shapes
	//if there is a row of the same shapes.
	//also updates the score
	private void shapeMatcher() {
		int squareRow1 = 0;
		int squareRow2 = 0;
		int squareRow3 = 0;
		int triangleRow1 = 0;
		int triangleRow2 = 0;
		int triangleRow3 = 0;
		int circleRow1 = 0;
		int circleRow2 = 0;
		int circleRow3 = 0;
		
		for(Thing thing: things) {
			switch(thing.getType()) {
			case Square:
				if (thing.getPosition() == 0 | thing.getPosition() == 1 | thing.getPosition() == 2)
					squareRow1++;
					if (squareRow1 == 3) {
						updateRowShapes(3);
						Log.d("Match!", "" + thing.getType() + "squareRow1");
					}
				else if (thing.getPosition() == 3 | thing.getPosition() == 4 | thing.getPosition() == 5)
					squareRow2++;
					if (squareRow2 == 3) {
						updateRowShapes(5);
						Log.d("Match!", "" + thing.getType() + "squareRow2");
					}
				else if (thing.getPosition() == 6 | thing.getPosition() == 7 | thing.getPosition() == 8)
					squareRow3++;
					if (squareRow3 == 3) {
						updateRowShapes(8); 
						Log.d("Match!", "" + thing.getType() + "squareRow3");
					}
				break;
					
			case Circle: 
				if (thing.getPosition() == 0 | thing.getPosition() == 1 | thing.getPosition() == 2)
					circleRow1++;
					if (circleRow1 == 3) {
						updateRowShapes(3);
						Log.d("Match!", "" + thing.getType() + "circleRow1");
					}
				else if (thing.getPosition() == 3 | thing.getPosition() == 4 | thing.getPosition() == 5)
					circleRow2++;
					if (circleRow2 == 3) {
						updateRowShapes(5);
						Log.d("Match!", "" + thing.getType() + "circleRow2");
					}
				else if (thing.getPosition() == 6 | thing.getPosition() == 7 | thing.getPosition() == 8)
					circleRow2++;
					if (circleRow3 == 3) {
						updateRowShapes(8);
						Log.d("Match!", "" + thing.getType() + "circleRow3");
					}
				break;
			case Triangle:
				if (thing.getPosition() == 0 | thing.getPosition() == 1 | thing.getPosition() == 2)
					triangleRow1++;
					if (triangleRow1 == 3) {
						updateRowShapes(3);
						Log.d("Match!", "" + thing.getType() + "triangleRow1");
					}
				else if (thing.getPosition() == 3 | thing.getPosition() == 4 | thing.getPosition() == 5)
					triangleRow2++;
					if (triangleRow2 == 3) {
						updateRowShapes(5);
						Log.d("Match!", "" + thing.getType() + "triangleRow2");
					}
				else if (thing.getPosition() == 6 | thing.getPosition() == 7 | thing.getPosition() == 8)
					triangleRow3++;
					if (triangleRow3 == 3) {
						updateRowShapes(8);
						Log.d("Match!", "" + thing.getType() + "triangleRow3");
					}
				break;
			default:
				break;
			}
			
		}
		Log.d("Line Break", "--------------------------------");
	}

	private void updateRowShapes(int rowIndex) {
		for(int begOfRow = rowIndex - 2; begOfRow <= rowIndex; begOfRow++) {
			rand = random.nextInt(shapes.size());
			//things.remove(begOfRow);
			things.get(begOfRow).setShape(shapes.get(rand).getShape());
			things.get(begOfRow).setType(type.get(rand));
		}
		player.updateScore(10);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//handle the touch of a shape
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			selected = findThingAt(event.getX(), event.getY());
			shuffle = shuffleShapes(event.getX(), event.getY());
			if (selected != null) {
				RectF bounds = selected.getRectFBounds();
				selectionOffsetX = event.getX() - bounds.left;
				selectionOffsetY = event.getY() - bounds.top;
			
			} else if (shuffle) {
				changeColors = true;
				invalidate();
			}
			return true;
		}
		
		//handle dragging of the different shapes from one box to another
		else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (selected != null) {
				RectF bounds = selected.getRectFBounds();
				float width = bounds.right - bounds.left;
				float height = bounds.bottom - bounds.top;
				
				//doing bounds.set below is what allows the 
				//shape to be redrawn as the drag is happening
				bounds.set(event.getX() - selectionOffsetX,
						   event.getY() - selectionOffsetY,
						   event.getX() - selectionOffsetX + width,
						   event.getY() - selectionOffsetY + height);
				changeColors = false;
				invalidate();
			}
			//changeColors = true;
			//invalidate();
			
			//if the user drags outside the board 
			drag = dragOutsideBoard(event.getX(), event.getY());
			
			if (drag) {
				Log.d("Drag Status", "I dragged outside the board!");
			}
			
			//handle the drag outside the game board
			//else if (dragOutsideGameBoard)
			
			return true;
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			changeColors = true;
			invalidate();
			if (selected != null)
				swapShapes();
			invalidate();
			//printShape();
			shapeMatcher();
			invalidate();
			//printShape();
			//invalidate();
			selected = null;
		}
	return true;
	}
		
	public void init() {
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(12);
		
		float x0, x1, x2, x3, y0, y1, y2, y3;
		x0 = 50;
		x1 = getWidth()/3;
		x2 = getWidth()*2/3;
		x3 = getWidth() - 50;
		y0 = 50;
		y1 = getHeight()/3;
		y2 = getHeight()*2/3;
		y3 = getHeight() - 50;
		
		//initialize the game board coordinates
		RectF rect0 = new RectF(x0, y0, x1, y1);
		RectF rect1 = new RectF(x1, y0, x2, y1);
		RectF rect2 = new RectF(x2, y0, x3, y1);
		RectF rect3 = new RectF(x0, y1, x1, y2);
		RectF rect4 = new RectF(x1, y1, x2, y2);
		RectF rect5 = new RectF(x2, y1, x3, y2);
		RectF rect6 = new RectF(x0, y2, x1, y3);
		RectF rect7 = new RectF(x1, y2, x2, y3);
		RectF rect8 = new RectF(x2, y2, x3, y3);
		
		positions.add(rect0);
		positions.add(rect1);
		positions.add(rect2);
		positions.add(rect3);
		positions.add(rect4);
		positions.add(rect5);
		positions.add(rect6);
		positions.add(rect7);
		positions.add(rect8);
		
		shapes.add(square);
		shapes.add(circle);
		shapes.add(triangle);
		
		initializeComponents();
		printShape();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if (paint == null) 
			init();
		//draw the game board		
		for(RectF position : positions) {
			canvas.drawRect(position, paint);
		}
		
		if (shuffle == true && shuffleCount < 100) {
			things.clear();
			initializeComponents();
		}
		
		//update the bounds for each shape and draw it (or in the case of invalidate; redraw it)
		for (Thing thing : things) {
				color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
				switch(thing.getType()) {
				case Triangle:
					thing.getRectFBounds().round(rect);
					triangle.setBounds(rect);
					if(changeColors)
						triangle.getPaint().setColor(color);
					else {
						int tColor = triangle.getPaint().getColor();
						triangle.getPaint().setColor(tColor);
					}
					if (selected != null && selected.getType() == thing.getType()) {
						triangle.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
					}
					triangle.draw(canvas);
					triangle.clearColorFilter();
					break;
				case Square:
					thing.getRectFBounds().round(rect);
					square.setBounds(rect);
					if(changeColors)
						square.getPaint().setColor(color);
					else {
						int sColor = square.getPaint().getColor();
						square.getPaint().setColor(sColor);
					}
					if (selected != null && selected.getType() == thing.getType()) {
						square.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
					}
					square.draw(canvas);
					square.clearColorFilter();
					break;
				case Circle:
					thing.getRectFBounds().round(rect);
					circle.setBounds(rect);
					if(changeColors)
						circle.getPaint().setColor(color);
					else {
						int cColor = circle.getPaint().getColor();
						circle.getPaint().setColor(cColor);
					}
					if (selected != null && selected.getType() == thing.getType()) {
						circle.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
					}
					circle.draw(canvas);
					circle.clearColorFilter();
					break;
				default:
					break;
			}	
		}//end for loop	
	}//end onDraw
}
