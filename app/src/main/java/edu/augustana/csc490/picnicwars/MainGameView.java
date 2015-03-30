// CannonView.java
// Displays and controls the Cannon Game
package edu.augustana.csc490.picnicwars;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;


public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "PicnicWars"; // for Log.w(TAG, ...)

    private GameThread gameThread; // runs the main game loop
    private Activity mainActivity; // keep a reference to the main Activity

    private boolean isGameOver = true;

    private int x;
    private int y;
    private int screenWidth;
    private int screenHeight;

    private double XANTSPEED = 100;
    private double YANTSPEED = 25;
    private int NUM_ANTS_EASY = 3;
    private double TIME_EASY = 20;

    private int[][] activeAnts;
    private int[][] antRelease;
    private double timeLeft; // time remaining in seconds
    private double totalElapsedTime; // elapsed seconds

    private Paint myPaint;
    private Paint backgroundPaint;
    private Paint textPaint;

    public MainGameView(Context context, AttributeSet atts)
    {
        super(context, atts);
        mainActivity = (Activity) context;

        getHolder().addCallback(this);

        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(15, 140, 63));
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    public void populateAnts() {
        int i = 0;
        antRelease = new int[NUM_ANTS_EASY][2];
        //toggleYMovement = new int[NUM_ANTS_EASY];
        for (int[] ints : activeAnts = new int[NUM_ANTS_EASY][2]) {
            activeAnts[i][0]=(int) (screenWidth * .01);
            activeAnts[i][1]= randInt((int) (screenHeight*.3),(int) (screenHeight*.7));
            antRelease[i][0] = randInt(3, (int) (TIME_EASY  - 10));
            antRelease[i][1] = -1;
            //toggleYMovement[i]=randInt(1,2);
            i++;
        }
        antRelease[0][0] = 1;
    }

    private void updateAnts(double interval, Canvas canvas) {
        int toggleY = randInt(0,1);
        for (int i = 0;i<NUM_ANTS_EASY;i++) {
            if ((antRelease[i][1] == -1) && (antRelease[i][0] <= totalElapsedTime)) {
                antRelease[i][1] = 0;
            }
            if (antRelease[i][1] == 0) {
                activeAnts[i][0] += interval * XANTSPEED;
                if ((toggleY == 1) && (activeAnts[i][1] < (screenHeight * .8))) {
                    activeAnts[i][1] += interval * YANTSPEED;
                } else if ((toggleY != 1) && (activeAnts[i][1] > (screenHeight * .2))) {
                    activeAnts[i][1] += -1* interval * YANTSPEED;
                }
                canvas.drawCircle(activeAnts[i][0], activeAnts[i][1], 20, myPaint);
            }
        toggleY = randInt(0,1);
        }
    }

    // called when the size changes (and first time, when view is created)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;

        startNewGame();

        textPaint.setTextSize(w / 20); // text size 1/20 of screen width
       // textPaint.setAntiAlias(true); // smoothes the text
    }

    public void startNewGame()
    {
        this.x = 25;
        this.y = 25;

        timeLeft = TIME_EASY; // start the countdown at 10 seconds

        populateAnts();

        if (isGameOver)
        {
            isGameOver = false;
            gameThread = new GameThread(getHolder());
            gameThread.start(); // start the main game loop going
        }
    }

    public void updateView(Canvas canvas, double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0; // convert to seconds

        //updateAnts(interval, canvas);

        if (canvas != null) {
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
            updateAnts(interval, canvas);
            insertTime(canvas);
        }

        timeLeft-=interval;

        if (timeLeft <= 0.0)
        {
            timeLeft = 0.0;
            isGameOver = true; // the game is over
            gameThread.setRunning(false); // terminate thread
            //showGameOverDialog(R.string.lose); // show the losing dialog
        }
    }

    public void insertTime(Canvas canvas) {
        canvas.drawText(getResources().getString(
                R.string.time_left, timeLeft), 30, 50, textPaint);
    }
    //copied from http://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    // stop the game; may be called by the MainGameFragment onPause
    public void stopGame()
    {
        timeLeft = 0.0; //time remaining set to 0

        if (gameThread != null)
            gameThread.setRunning(false);
    }


    // release resources; may be called by MainGameFragment onDestroy
    public void releaseResources()
    {
        // release any resources (e.g. SoundPool stuff)
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // called when the surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // ensure that thread terminates properly
        boolean retry = true;
        gameThread.setRunning(false); // terminate gameThread

        while (retry)
        {
            try
            {
                gameThread.join(); // wait for gameThread to finish
                retry = false;
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
    }

        @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (e.getAction() == MotionEvent.ACTION_DOWN)
        {
            this.x = (int) e.getX();
            this.y = (int) e.getY();
        }

        return true;
    }

    // Thread subclass to run the main game loop
    private class GameThread extends Thread
    {
        private SurfaceHolder surfaceHolder; // for manipulating canvas
        private boolean threadIsRunning = true; // running by default

        // initializes the surface holder
        public GameThread(SurfaceHolder holder)
        {
            surfaceHolder = holder;
            setName("GameThread");
        }

        // changes running state
        public void setRunning(boolean running)
        {
            threadIsRunning = running;
        }


        @Override
        public void run()
        {
            Canvas canvas = null;
            long previousFrameTime = System.currentTimeMillis();

            while (threadIsRunning)
            {
                try
                {
                    // get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);

                    // lock the surfaceHolder for drawing
                    synchronized(surfaceHolder)
                    {
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        updateView(canvas,elapsedTimeMS); // draw using the canvas
                        totalElapsedTime += elapsedTimeMS / 1000.0;
                        previousFrameTime = currentTime;
                    }
                    Thread.sleep(1); // if you want to slow down the action...
                } catch (InterruptedException ex) {
                    Log.e(TAG,ex.toString());
                }
                finally  // regardless if any errors happen...
                {
                    // make sure we unlock canvas so other threads can use it
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}