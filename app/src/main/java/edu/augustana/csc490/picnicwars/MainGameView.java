// CannonView.java
// Displays and controls the Cannon Game
package edu.augustana.csc490.picnicwars;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
/*
 * Created by Reed Kottke:  main game view of Picnic wars
 *      Skeleton for code: Forrest Stonedahl - http://moodle.augustana.edu/course/view.php?id=7207 [GameStarter.zip]
 */
public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "PicnicWars"; // for Log.w(TAG, ...)

    private GameThread gameThread; // runs the main game loop
    private Activity mainActivity; // keep a reference to the main Activity

    private boolean isGameOver = true; // set to false when the game should no longer run
    private boolean dialogIsDisplayed = false;// set to false to hide the dialog

    private int screenWidth; //for variable screen size
    private int screenHeight;

    //variables for the ant speed, num ants and time of easy v. hard game settings
    private double X_ANT_SPEED_EASY = 200;
    private double Y_ANT_SPEED_EASY = 25;
    private double X_ANT_SPEED_HARD = 300;
    private double Y_ANT_SPEED_HARD = 30;
    private int NUM_ANTS_EASY = 10;
    private double TIME_EASY = 20;
    private int NUM_ANTS_HARD = 30;
    private double TIME_HARD = 30;

    private int[][] activeAnts; //2d array of the x and y position of active ants
    private int[][] antRelease; //2d array of the [0] time of ant release and [1] status of the ant
                                // -1 means ant not created or successfully in basket,
                                // 0 means ant is on the screen
                                // 1 means ant was killed by player
    private double timeLeft; // time remaining in seconds
    private double totalElapsedTime; // elapsed seconds
    private int successfulAnts;//count of ants that get to the basket
    private double x_speed; //depending on the game setting, this variable will be set to the constant ant speed
    private double y_speed; //depending on the game setting, this variable will be set to the constant ant speed
    private int num_ants; //depedning on the game setting, this variable will be the max iterator of loops/number of ants
    private double time_ants; //depending on the game setting, this variable will represent game time.

    private Paint myPaint; // paint for general drawing
    private Paint backgroundPaint; //paint for background
    private Paint textPaint; //paint for text
    private Paint antPaint; //paint for ants

    private Bitmap drawing; //will be the background drawing Source: http://www.canstockphoto.com/illustration/grass.html
                            //source: http://www.fotosearch.com/clip-art/picnic-basket.html
    boolean dif; //boolean to represent difficulty: true is hard, false iseasy

    private SharedPreferences difficulty; //to access the sharedPreferences which represents chosen difficulty

    public MainGameView(Context context, AttributeSet atts)
    {

        super(context, atts);
        mainActivity = (Activity) context;

        difficulty = getContext().getSharedPreferences("difficulty",Context.MODE_PRIVATE);

        drawing = BitmapFactory.decodeResource(getResources(), R.drawable.grass);

        getHolder().addCallback(this);

        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(15, 140, 63));
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        antPaint = new Paint();
        antPaint.setColor(Color.BLACK);
        antPaint.setStrokeWidth(4);

    }

    //populate the ants with random y position and random start times.
    public void populateAnts() {
        int i = 0;
        antRelease = new int[num_ants][2];

        for (int[] ints : activeAnts = new int[num_ants][2]) {
            activeAnts[i][0]=(int) (screenWidth * .01);
            activeAnts[i][1]= randInt((int) (screenHeight*.3),(int) (screenHeight*.7));
            antRelease[i][0] = randInt(3, (int) (time_ants  - 10));
            antRelease[i][1] = -1;
            i++;
        }
        antRelease[0][0] = 1;
    }

    //if ants are on the screen, update them to move at variable speeds
    private void updateAnts(double interval, Canvas canvas) {
        int toggleY = randInt(0,1);

        for (int i = 0;i<num_ants;i++) {
           if ((antRelease[i][1] == -1) && (antRelease[i][0] <= totalElapsedTime)) {
                antRelease[i][1] = 0;
           }
            if (antRelease[i][1] == 0) {
                activeAnts[i][0] += interval * x_speed;
                if ((toggleY == 1) && (activeAnts[i][1] < (screenHeight * .8))) {
                    activeAnts[i][1] += interval * y_speed;
                } else if ((toggleY != 1) && (activeAnts[i][1] > (screenHeight * .2))) {
                    activeAnts[i][1] += -1* interval * y_speed;
                }
                drawAnt(activeAnts[i][0], activeAnts[i][1], canvas);
            }
            toggleY = randInt(0,1);
        }
    }

    //check to see if the touch was near an ant.  If it was, change the ants status to 1 (dead)
    private void checkTouch(int x, int y){
        for (int i=0;i<num_ants;i++) {
            if ((Math.abs(activeAnts[i][0] - x) < 30) && (Math.abs(activeAnts[i][1] - y) < 30)) {
                antRelease[i][1] = -2;//ant killed, assigned -2
                antRelease[i][0] = (int) time_ants + 1;
            }
        }

    }

    //draw the ants
    private void drawAnt(float x, float y, Canvas canvas) {
        float radiusMiddle = 4;
        float radiusHead = 10;
        float radiusBack = 12;
        canvas.drawCircle(x,y,radiusMiddle,antPaint);
        canvas.drawCircle(x + 12,y,radiusHead,antPaint);
        canvas.drawCircle(x - 15,y,radiusBack,antPaint);

        canvas.drawLine(x - 20, y - 20, x, y, antPaint);
        canvas.drawLine(x-20,y-20,x-27,y-23, antPaint);
        canvas.drawLine(x-20,y+20,x,y,antPaint);
        canvas.drawLine(x-20,y+20,x-27,y+23, antPaint);

        canvas.drawLine(x,y-15,x,y,antPaint);
        canvas.drawLine(x-5,y-20,x,y-15, antPaint);
        canvas.drawLine(x,y+15,x,y,antPaint);
        canvas.drawLine(x-5,y+20,x,y+15, antPaint);

        canvas.drawLine(x,y,x+9,y-20,antPaint);
        canvas.drawLine(x,y,x + 9,y+20, antPaint);
        canvas.drawLine(x+9,y-20,x+12,y -20,antPaint);
        canvas.drawLine(x+9,y+20,x+12,y+20, antPaint);

        canvas.drawLine(x,y,x + 30,y - 5,antPaint);
        canvas.drawLine(x,y,x+ 30,y + 5,antPaint);
    }

    //count how many ants made it to the basket
    private int countSuccess() {
        successfulAnts = 0;
        for (int i=0;i<num_ants;i++) {
            if ((antRelease[i][0] == (int) time_ants +1) && (antRelease[i][1]!=-2)) { //ant made it to the picnic basket
                successfulAnts++;
            }
        }
        return successfulAnts;
    }

    //check to see if all ants are dead. Return true if all ants are dead.
    private boolean checkAntStatus() {
        int countDead = 0;
        int countDoneAnts = 0;
        for (int i=0;i<num_ants;i++) {
            countDead += antRelease[i][1];
            if ((antRelease[i][1]<=-1) && (antRelease[i][0] == (int) time_ants + 1)) {
                countDoneAnts++;
            }
            if ((activeAnts[i][0] > (screenWidth * .85))&&(antRelease[i][1]==0)) { //ant made it to the picnic basket
                antRelease[i][1]= -1; //ant returned to pre game status
                antRelease[i][0]= (int) time_ants + 1; //ant wont' be released during this game.
            }
        }
        if ((countDoneAnts == num_ants) || (countDead == -2* num_ants)) return true;
        else return false;
    }

    // called when the size changes (and first time, when view is created)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;

        drawing = drawing.createScaledBitmap(drawing,w,h,true);

        startNewGame();
        textPaint.setTextSize(w / 20); // text size 1/20 of screen width

    }

    //start a new game, check to see what the difficulty constants should be set to, reset timer
    public void startNewGame() {
       String difString = difficulty.getString("difficulty","");

       if (difString.equals("Easy")){
           dif = false;
       } else {
           dif = true;
       }

       if (dif) {
           x_speed = X_ANT_SPEED_HARD;
           y_speed = Y_ANT_SPEED_HARD;
           num_ants = NUM_ANTS_HARD;
           time_ants = TIME_HARD;
       } else {
           x_speed = X_ANT_SPEED_EASY;
           y_speed = Y_ANT_SPEED_EASY;
           num_ants = NUM_ANTS_EASY;
           time_ants = TIME_EASY;
       }

        timeLeft = time_ants; // start the countdown at 10 seconds
        totalElapsedTime=0;

        populateAnts();

        if (isGameOver)  {
            isGameOver = false;
            gameThread = new GameThread(getHolder());
            gameThread.start(); // start the main game loop going
        }
    }

    //called in game thread loop, creates view, check to see if game should end
    public void updateView(Canvas canvas, double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0; // convert to seconds

        if (canvas != null) {
            canvas.drawBitmap(drawing, 0, 0, null);
            updateAnts(interval, canvas);
            insertTime(canvas);
        }

        timeLeft-=interval;

        if (timeLeft <= 0.0)
        {
            timeLeft = 0.0;
            isGameOver = true; // the game is over
            gameThread.setRunning(false); // terminate thread
            showGameOverDialog(R.string.lose); // show the losing dialog
        } else if (checkAntStatus()){
            isGameOver = true;
            gameThread.setRunning(false);
            if (countSuccess() > 0) showGameOverDialog(R.string.lose); else
            showGameOverDialog(R.string.win); //show winning dialog
        }
    }

    // display an AlertDialog when the game ends
    private void showGameOverDialog(final int messageId) {

        final DialogFragment gameResult =
                new DialogFragment() {
                    // create an AlertDialog and return it
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        // create dialog displaying String resource for messageId
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(messageId));
                        builder.setMessage(getResources().getString(R.string.results, countSuccess()));
                        builder.setPositiveButton(R.string.reset_button_string,
                                new DialogInterface.OnClickListener() {
                                    // called when "Reset Game" Button is pressed
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogIsDisplayed = false;
                                        startNewGame(); // set up and start a new game
                                    }
                                } // end anonymous inner class
                        ); // end call to setPositiveButton
                       builder.setNegativeButton(R.string.return_to_main,
                               new DialogInterface.OnClickListener() {

                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialogIsDisplayed = false;
                                       stopGame();
                                       Intent myIntent = new Intent(getContext(), SplashActivity.class);
                                       startActivityForResult(myIntent, 0);
                                   }
                               });

                        return builder.create(); // return the AlertDialog
                    } // end method onCreateDialog
                }; // end DialogFragment anonymous inner class
                // in GUI thread, use FragmentManager to display the DialogFragment
                mainActivity.runOnUiThread(
                        new Runnable() {
                            public void run() {
                                dialogIsDisplayed = true;
                                gameResult.setCancelable(false); // modal dialog
                                gameResult.show(mainActivity.getFragmentManager(), "results");
                            }
                        } // end Runnable
                ); // end call to runOnUiThread
    } // end method showGameOverDialog

    //add the time remaining and number of ants in basket to screen
    public void insertTime(Canvas canvas) {
        canvas.drawText(getResources().getString(R.string.time_left, timeLeft), 30, 50, textPaint);
        canvas.drawText(getResources().getString(R.string.results,countSuccess()),30,(float) screenWidth/2, textPaint);
    }

    //copied from http://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
    //create random variables method for starting y position and starting time of ants
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

    //when a touch or click happens, get the position of the touch
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int xTouch = 0;
        int yTouch = 0;

        if (e.getAction() == MotionEvent.ACTION_DOWN)
        {
            xTouch = (int) e.getX();
            yTouch = (int) e.getY();
        }

        checkTouch(xTouch,yTouch);

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

        //main running thread to populate view continuously
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