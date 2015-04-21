package com.mashhype.fallingblocks;
import java.util.ArrayList;
import java.util.Random;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Parts of this java class file can be attributed to:
 * Mobile App Development for Android Platform Module 14a & 14b
 * offered through JHU EP authored by Scott Stanchfield
 */

public class GameScreen extends View {
	private Paint paint;
	private GameThread gameThread;
	Random random = new Random();
	int randColor;
	private int blockLeft = 40, blockTop = 40, blockRight = 80, blockBottom = 80;
	private float previousXAccel, previousYAccel;
	private float currentXAccel, currentYAccel;
	private float velocityX, velocityY;
	private float currentXMag, currentYMag;
	private int count = 0;
	private float x, y;
	
	private BitmapDrawable bitmapDrawableMario = new BitmapDrawable();
	private Rect marioBounds = new Rect();
	private int marioLeft, marioTop, marioRight, marioBottom;

	private RectF rectFBounds = new RectF();
	
	private ArrayList<ShapeDrawable> blocks = new ArrayList<ShapeDrawable>();
	private Rect blockBounds = new Rect();
	float dx = 10, dy = 10;
	private ShapeDrawable square;
	private ShapeDrawable circle;
	private ShapeDrawable triangle;
	private ShapeDrawable square2;
	private ShapeDrawable circle2;
	private ShapeDrawable triangle2;
	
	private static final String TAG = GameScreen.class.getSimpleName();
	private Handler handler = new Handler();
	
	private Runnable invalidator = new Runnable() {
		@Override public void run() {
			invalidate();
		}
	};
	
	
	public GameScreen(Context context) {
		super(context);
	}
	
	public GameScreen(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public GameScreen(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void start() {
		stop();
		gameThread = new GameThread();
		gameThread.start();
	}

	public void stop() {
		if (gameThread != null) {
			gameThread.interrupt();
		gameThread = null;
		}
	}

	private class GameThread extends Thread {
		@Override public void run() {
			while (!isInterrupted()) {
				// update everything
				update();
				handler.post(invalidator);
				try {
					sleep(75);
				} catch (InterruptedException e) {
					interrupt();
				}
			}
		}
	}
			
	// onSizeChanged is called at the beginning because Android
	// doesn't know the size of your view
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	}
	
	public void update() {
		for(int i = 0; i < blocks.size(); i++)  {
			// if i'm about to fall off the screen replace reset my position
			if (blocks.get(i).getBounds().bottom >= getHeight()) {
				int index = blocks.indexOf(blocks.get(i));
				randColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
				blocks.get(i).getPaint().setColor(randColor);
				blockBounds = new Rect(blockLeft + getWidth()*index/3, blockTop, blockRight + getWidth()*index/3, blockBottom);
				blocks.get(i).setBounds(blockBounds);	
			} else {
				blocksMove(blocks.get(i));
				playerMove(bitmapDrawableMario);
				collision(bitmapDrawableMario, blocks.get(i));
				isGameOver(bitmapDrawableMario);
			}
		}
		
	}
	
	public void changeMag(float left, float top) {
		currentXMag = left;
		currentYMag = top;
	}
	
	// these x and y values come over from 
	// the accelerometer in MainActivity
	public void changeAccel(float left, float top) {
		currentXAccel = left;
		currentYAccel = top;
	}
	
	public void isGameOver(BitmapDrawable bmd) {
		marioBounds = bmd.copyBounds();
		Log.d(TAG, "Mario's Bounds bottom:" + marioBounds.bottom);
		Log.d(TAG, "getHeight():" + getHeight());
		if (marioBounds.bottom >= getHeight()) {
			stop();
			Intent gameOverScreen = new Intent(this.getContext(), GameOverActivity.class);
			((Activity)getContext()).startActivity(gameOverScreen);
			((Activity)getContext()).finish();
		}
	}
	
	
	public void playerMove(BitmapDrawable bmd) { 
		if (count == 0) {
			previousXAccel = 0;
			previousYAccel = 0;	
			
		}
		count++;
		// accelerate
		velocityX += currentXAccel + previousXAccel;
		velocityY += currentYAccel + previousYAccel;
		
		Log.d(TAG, "Velocity X: " + velocityX);
		Log.d(TAG, "Velocity Y: " + velocityY);
		
		previousXAccel = currentXAccel;
		previousYAccel = currentYAccel;
		
		// move
		x += velocityX*.1;	
		y += velocityY*.1;
	
		Log.d(TAG, "X: " + x);
		Log.d(TAG, "Y: " + y);
		
		// check bounds
		if (x < 0) {
			x = 0;
			currentXAccel = 0;
			velocityX = 0;
		}
		if (x > getWidth()-bmd.getIntrinsicWidth()) {
			x = getWidth()-bmd.getIntrinsicWidth();
			currentXAccel = 0;
			velocityX = 0;
		}
		if (y < 0) {
			y = 0;
			currentYAccel = 0; 
			velocityY = 0;
		}
		if (y > getHeight()-bmd.getIntrinsicHeight()) {
			y = getHeight()-bmd.getIntrinsicHeight();
			currentYAccel = 0; 
			velocityY = 0;
		}
		
		//we use a RectF for higher accuracy
		rectFBounds.left = x;
		rectFBounds.top = y;
		rectFBounds.right = bmd.getIntrinsicWidth() + rectFBounds.left;
		rectFBounds.bottom = bmd.getIntrinsicHeight() + rectFBounds.top;
		rectFBounds.round(marioBounds);
		bmd.setBounds(marioBounds);
	}
	
	public void collision(BitmapDrawable bmd, ShapeDrawable s) {
		// if the bounds of one of the shapes has intersected the
		// bounds of the player, then move the player and
		// vibrate the device, else do nothing
		// boolean collision = false;
		blockBounds = s.copyBounds();
		marioBounds = bmd.copyBounds();
		if(Rect.intersects(blockBounds, marioBounds)) {
			Log.d(TAG, "The shape intersected Mario!!");
			/*rectFBounds.left = x;
			rectFBounds.top =  y;
			//Log.d(TAG, "rectFBounds left/right values: " + x + "/" + y);
			rectFBounds.right = bitmapDrawableMario.getIntrinsicWidth() + rectFBounds.left;
			rectFBounds.bottom = bitmapDrawableMario.getIntrinsicHeight() + rectFBounds.top;
			rectFBounds.round(marioBounds);*/
			velocityX = 0;
			velocityY = 0;
			currentXAccel = 0;
			currentYAccel = 0;
			bmd.setBounds(marioBounds.left, marioBounds.top + 100, marioBounds.right, marioBounds.bottom + 100);
			
			// https://code.google.com/p/android/issues/detail?id=65037
			// be aware of a bug in the audio server that causes the app to crash
			// after a few seconds of collisions
			try {
		    	Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		    	Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
		    	r.play();
		    	Vibrator vibrate = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
				if(vibrate != null)
					vibrate.vibrate(2000);
			} catch (Exception e) {
		    	e.printStackTrace();
			}	
		}
	}
	
	public void blocksMove(ShapeDrawable shape) {
		// dx, dy control how fast the blocks fall
		
		blockBounds = shape.copyBounds();
		int direction = random.nextInt(3);
		if (blockBounds.left <= 0) {
			blockBounds.left += dx;
			blockBounds.right += dx;
		}
		if (blockBounds.right >= getWidth()) {
			blockBounds.left -= dx;
			blockBounds.right -= dx;
		}
		if (direction == 0) {
			blockBounds.left -= dx;
			blockBounds.right -= dx;
		} else {
			blockBounds.left += dx;
			blockBounds.right += dx;
		}
		if (direction == 1) {
			blockBounds.left += dx*(currentXMag/100);
			blockBounds.right += dx*(currentXMag/100);
		} else {
			blockBounds.left -= dx*(currentXMag/100);
			blockBounds.right -= dx*(currentXMag/100);
		}
		
		blockBounds.top += dy;
		blockBounds.bottom += dy;
		shape.setBounds(blockBounds);
	}
	
	public void initializeBlocks() {
		square = new ShapeDrawable(new RectShape());
		circle = new ShapeDrawable(new OvalShape());
		triangle = new ShapeDrawable(new Triangle(10));
		square2 = new ShapeDrawable(new RectShape());
		circle2 = new ShapeDrawable(new OvalShape());
		triangle2 = new ShapeDrawable(new Triangle(10));
		blocks.add(circle);
		blocks.add(square);
		blocks.add(triangle);
		blocks.add(circle2);
		blocks.add(square2);
		blocks.add(triangle2);
		
		for (int i = 0; i < blocks.size(); i++) {
			randColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
			blocks.get(i).getPaint().setColor(randColor);
			blockBounds = new Rect(blockLeft + getWidth()*i/blocks.size(), blockTop, blockRight + getWidth()*i/blocks.size(), blockBottom);
			blocks.get(i).setBounds(blockBounds);
		}
	}
	
	public void initializePlayer() {
		// get the resource from the res/drawable folder
		Bitmap mario = BitmapFactory.decodeResource(getResources(), R.drawable.mario);
		// resize the bitmap image
		Bitmap marioResized = Bitmap.createScaledBitmap(mario, (int)(getWidth()*.1), (int)(getHeight()*.1), true);
		bitmapDrawableMario = new BitmapDrawable(getResources(), marioResized);
		//draw the bitmapDrawable in the middle of the screen
		//these can be local; they never change
		marioLeft = getWidth()/2;
		marioTop = getHeight()/2;
		marioRight = bitmapDrawableMario.getIntrinsicWidth() + marioLeft;
		marioBottom = bitmapDrawableMario.getIntrinsicHeight() + marioTop;
		marioBounds.set(marioLeft, marioTop, marioRight, marioBottom);
		bitmapDrawableMario.setBounds(marioBounds);
	}
	
	public void init() {
		paint = new Paint();
		initializeBlocks();
		initializePlayer();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (paint == null) 
			init();

		for(ShapeDrawable block : blocks) {
			block.draw(canvas);
			//Log.d(TAG, "shape bounds: " + block.getBounds());
		}
		// x and y are generated from the accelerometer
		//bitmapDrawableMario.setBounds(x,  y, marioRight, marioBottom);
		Log.d(TAG, "bitmapDrawableMario bounds is: " + bitmapDrawableMario.getBounds());
		bitmapDrawableMario.draw(canvas);
		//canvas.drawBitmap(player.getPlayer(), x, y, null);
		Log.d(TAG, "x/y: " + x + "/" + y);
	}
}
