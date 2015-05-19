// Displays and controls the Picnic Wars
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
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
 * Created by Reed Kottke:  main game view of picnic_background wars
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
    private double X_FIRE_ANT_SPEED_EASY = 260;
    private double X_ANT_SPEED_EASY = 200;
    private double X_BUTTERFLY_SPEED_EASY = 170;
    private double X_BEETLE_SPEED_EASY = 140;
    private double Y_BUG_SPEED_EASY = 25;
    private double X_ANT_SPEED_HARD = 300;
    private double Y_BUG_SPEED_HARD = 30;
    private int NUM_ANTS_EASY = 10;
    private double TIME_EASY = 20;
    private int NUM_ANTS_HARD = 30;
    private double TIME_HARD = 30;

    private List<Bug> allBugs; //List of all Bug containing their position and properties

    //private int[][] activeAnts; //2d array of the x and y position of active ants
    //private int[][] antRelease; //2d array of the [0] time of ant release and [1] status of the ant
        // -1 means ant not created or successfully in basket,
        // 0 means ant is on the screen
        // 1 means ant was killed by player
    private double timeLeft; // time remaining in seconds
    private double totalElapsedTime; // elapsed seconds
    private int successfulAnts;//count of ants that get to the basket
    private double x_speed; //depending on the game setting, this variable will be set to the constant ant speed
    private double y_speed; //depending on the game setting, this variable will be set to the constant ant speed
    private int num_ants; //depending on the game setting, this variable will be the max iterator of loops/number of ants
    private double time_ants; //depending on the game setting, this variable will represent game time.

    private Paint myPaint; // paint for general drawing
    private Paint backgroundPaint; //paint for background
    private Paint textPaint; //paint for text
    private Paint blackAntPaint; //paint for ants
    private Paint fireAntPaint; //paint for fire ants

    //Survival Characteristics
    int score; //Keeps track of score for survival mode
    Timer timer; //Timer that increases as opposed to the decreasing timer used in the Classic mode. Used for Survival Mode
    MyTimerTask myTimerTask; //Task to increment timer every second
    int seconds; //Integer representing seconds passed
    int level; //Variable that helps determine how many ants to release every second
    int lives; //Set to 3, used to determine when to end Survival Mode
    boolean addMoreBugs;//Boolean used to determine when to add bugs to the arraylist
    boolean earthquakePowerUp;//Boolean that is used to determine whether the Earthquake has been used yet.

    String highScore1; //String containing the high score number and name
    String highScore2;//String containing the 2nd high score number and name
    String highScore3;//String containing the 3rd high score number and name
    String highScore4;//String containing the 4th high score number and name
    String highScore5;//String containing the 5th high score number and name

    int highScore1Number; //Integer used to compare whether a highscore has been scored or not.
    int highScore2Number; //Integer used to compare whether a highscore has been scored or not.
    int highScore3Number; //Integer used to compare whether a highscore has been scored or not.
    int highScore4Number; //Integer used to compare whether a highscore has been scored or not.
    int highScore5Number; //Integer used to compare whether a highscore has been scored or not.



    private static Bitmap drawing; //old background drawing Source: http://www.canstockphoto.com/illustration/grass.html
    //new Image created by Michael Madden within photoshop, images used: http://cliparts.co/cliparts/6cy/XjB/6cyXjBeEi.jpg, http://www.frontrowsociety.com/boutique/modules/contest/images/detail/1317314724Padr%C3%A3otoalhapiquenique75dpi.jpg
    private static Bitmap antLifeDrawing; //Ant to use for lives in future http://www.moxiedot.com/wp-content/uploads/2013/07/ant.jpg
    private static Bitmap beetleDrawing; //Beetle drawing created by Michael Madden
    //source: http://www.fotosearch.com/clip-art/picnic-basket.html
    private static Bitmap butterflyDrawing; //Butterfly drawing created by Michael Madden
    private static Bitmap earthquakeDrawing;
    //source: http://www.beready.iowa.gov/Images/icons/earthquake_100px.png




    boolean dif; //boolean to represent difficulty: true is hard, false is easy
    boolean mode; //boolean to represent game modes; true is survival, false is classic

    private SharedPreferences difficulty; //to access the sharedPreferences which represents chosen difficulty
    private SharedPreferences gameMode; // To access which game Mode to implement
    private SharedPreferences highScoreOne; // To access the high scores
    private SharedPreferences highScoreTwo; // To access the high scores
    private SharedPreferences highScoreThree; // To access the high scores
    private SharedPreferences highScoreFour; // To access the high scores
    private SharedPreferences highScoreFive; // To access the high scores
    private String userName = "Null"; //String from user input if they get a high score
    boolean saveHighScore = false; //Boolean to determine whether a high score has been scored or not.



    public MainGameView(Context context, AttributeSet atts)
    {
        super(context, atts);
        mainActivity = (Activity) context;

        //Get various shared preferences that determine the game mode, the difficulty, and the high scores.
        difficulty = getContext().getSharedPreferences("difficulty",Context.MODE_PRIVATE);
        gameMode = getContext().getSharedPreferences("mode", Context.MODE_PRIVATE);
        highScoreOne = getContext().getSharedPreferences("highScoreOne", Context.MODE_PRIVATE);
        highScoreTwo = getContext().getSharedPreferences("highScoreTwo", Context.MODE_PRIVATE);
        highScoreThree = getContext().getSharedPreferences("highScoreThree", Context.MODE_PRIVATE);
        highScoreFour = getContext().getSharedPreferences("highScoreFour", Context.MODE_PRIVATE);
        highScoreFive = getContext().getSharedPreferences("highScoreFive", Context.MODE_PRIVATE);


        // only load the images the first time, if they haven't been loaded already

        //Set the Bitmaps to the sources
            drawing = BitmapFactory.decodeResource(getResources(), R.drawable.picnicgrass);
            antLifeDrawing = BitmapFactory.decodeResource(getResources(), R.mipmap.ant_live);
            beetleDrawing = BitmapFactory.decodeResource(getResources(), R.mipmap.beetle);
            butterflyDrawing = BitmapFactory.decodeResource(getResources(), R.mipmap.butterfly);
            earthquakeDrawing = BitmapFactory.decodeResource(getResources(), R.mipmap.earthquake_100px);

        getHolder().addCallback(this);

        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(15, 140, 63));
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);

        //Set ant characteristics
        blackAntPaint = new Paint();
        blackAntPaint.setColor(Color.BLACK);
        blackAntPaint.setStrokeWidth(4);

        //Set fire ant characteristics
        fireAntPaint = new Paint();
        fireAntPaint.setColor(Color.RED);
        fireAntPaint.setStrokeWidth(4);

        //ArrayList used to store bugs in Classic and Survival Mode
        allBugs = new ArrayList<Bug>();

        //Instantiate the Timer Task
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(myTimerTask, 0, 1000);
    }

    //populate the ants with random y position and random start times. For classic mode, the number is determined by the game mode. For Survival,
    //it is set to ten. That way there are ten ants set to appear at the beginning of the game. All ants are added to
    public void populateAnts() {
        int randomFireAnt;
        double timer;
        //if statement is used to determine which game mode is being used.
        if(!mode){
            for(int i = 0; i < num_ants; i++)
            {
                randomFireAnt = randInt(0,100);
                timer = randDouble(3,(int) (time_ants - 5));

                if(i == 0){
                    timer = 1.0;
                }
                allBugs.add(new Bug((int) (screenWidth * .01), randInt((int) (screenHeight * .3), (int) (screenHeight * .7)), 0, 1, timer, 1, x_speed));
                if(randomFireAnt >= 95)
                {
                    allBugs.add(new Bug((int) (screenWidth * .01), randInt((int) (screenHeight * .3), (int) (screenHeight * .7)), 1, 1, timer, 1, x_speed * 1.4));
                }
            }
        }
        else{
            for(int i = 0; i < 10; i++){
                randomFireAnt = randInt(0,100);
                timer = randDouble(0,10);

                if(randomFireAnt >= 95){
                    allBugs.add(new Bug(randInt(-1000, (int) (screenWidth * .01)), randInt((int) (screenHeight * .3), (int) (screenHeight * .7)), 1, 1, timer,1, x_speed * 1.4));
                }
                else{
                    allBugs.add(new Bug(randInt(-1000, (int) (screenWidth * .01)), randInt((int) (screenHeight * .3), (int) (screenHeight * .7)), 0, 1, timer,1, x_speed));
                }

            }
        }
    }

    //if ants are on the screen, update them to move at variable speeds
    private void updateAnts(double interval, Canvas canvas) {
        int toggleY = randInt(0,1);

        if(mode && addMoreBugs){
            for(int i = 0; i <= level; i++){
                int randomFireAnt = randInt(0, 100);
                double timer = randDouble(seconds,seconds + 10);

                if(randomFireAnt >= 95){
                    allBugs.add(new Bug(randInt(-1000, (int) (screenWidth * .01)), randInt((int) (screenHeight * .3), (int) (screenHeight * .7)), 1, 1, timer,1, x_speed * 1.4)); //add red ant to List of bugs
                }
                else if(randomFireAnt >= 90){
                    allBugs.add(new Bug(randInt(-1000, (int) (screenWidth * .01)), randInt((int) (screenHeight * .3), (int) (screenHeight * .7)), 1, 3, timer,2, X_BEETLE_SPEED_EASY)); //add beetle to List of bugs
                }
                else if(randomFireAnt >= 85){
                    allBugs.add(new Bug(randInt(-1000, (int) (screenWidth * .01)), randInt((int) (screenHeight * .3), (int) (screenHeight * .7)), 1, 1, timer,3, X_BUTTERFLY_SPEED_EASY)); //add butterfly to List of bugs
                }
                else{
                    allBugs.add(new Bug(randInt(-1000, (int) (screenWidth * .01)), randInt((int) (screenHeight * .3), (int) (screenHeight * .7)), 0, 1, timer,1, x_speed));//add black ant (most common)
                }
                addMoreBugs = false;
            }
        }
        Collections.sort(allBugs, new CustomComparator());
        for(Bug bug: allBugs)
        {
            if((bug.health > 0 && mode) || (!mode && bug.health > 0 && (bug.time <= time_ants - timeLeft)))
            {
                bug.xCoordinate += interval * bug.speed;
                int randomYMovement = randInt(-2,2);
                bug.yCoordinate += randomYMovement;
                if(toggleY == 1 && bug.xCoordinate < (screenHeight * .8))
                {
                    bug.yCoordinate += interval * y_speed;
                }
                else if(toggleY != 1 && (bug.yCoordinate > (screenHeight * .2)))
                {
                    bug.yCoordinate -= -1 * interval * y_speed;
                }
                if(bug.antColor == 0){
                    drawBug(bug.xCoordinate, bug.yCoordinate, canvas, blackAntPaint, bug.bugType);
                }
                else{
                    drawBug(bug.xCoordinate, bug.yCoordinate, canvas, fireAntPaint, bug.bugType);
                }
            }
            toggleY = randInt(0,1);
        }

    }
    //Method to 'kill' all the ants. Removes them from the screen.
    private void earthQuake(){

        for(Bug bug:allBugs){
            bug.health = - 1;
        }
        seconds = 0;
        level = 0;
    }
    //check to see if the touch was near a bug.  If it was, change the bugs status to dead
    private void checkTouch(int x, int y){
        //for each bug in the list of bugs, check to see if a bug is touched. If a bug is touched, its health is set to 0.
        if(mode && earthquakePowerUp && (x >= 25 && x <= 75) && (y >= screenHeight-75 && y <= screenHeight - 25)){
            earthQuake();
            earthquakePowerUp = false;
        }

        for(Bug bug: allBugs)
        {
            //Different checks for the different types of bugs. They are different sizes so they need to be checked at different sizes.
            if(bug.bugType == 1){
                if((Math.abs(bug.xCoordinate - x) < 30) && (Math.abs(bug.yCoordinate - y) < 30)){
                    bug.health -=1;//ant killed, assigned 0
                    bug.time = Integer.MAX_VALUE;//set Bug time to 'respawn' to after game so they never appear again
                    bug.xCoordinate = Integer.MIN_VALUE;//sets ants xCoordinate to be off the screen so you don't get points when clicking where they died.
                    if(bug.antColor == 0){
                        score+=10;
                    }
                    else{
                        score+=20;
                    }
                    //antsToRemove.add(ant);
                }
            }
            else if(bug.bugType == 3){
                if((x > bug.xCoordinate + 25 && x < bug.xCoordinate + 115) && (y > bug.yCoordinate + 30 && y < bug.yCoordinate + 120)){
                    bug.health -=1;//bug killed, assigned 0
                    bug.time = Integer.MAX_VALUE;//set Bug time to 'respawn' to after game so they never appear again
                    bug.xCoordinate = Integer.MIN_VALUE;//sets ants xCoordinate to be off the screen so you don't get points when clicking where they died.
                    lives-=1;
                }
            }
            else{
                if((x > bug.xCoordinate + 20 && x < bug.xCoordinate + 130) && (y > bug.yCoordinate + 40 && y < bug.yCoordinate + 100)){
                    bug.health -=1;//bug killed, assigned 0
                    if(bug.health <=0){
                        bug.time = Integer.MAX_VALUE;//set Bug time to 'respawn' to after game so they never appear again
                        bug.xCoordinate = Integer.MIN_VALUE;//sets ants xCoordinate to be off the screen so you don't get points when clicking where they died.
                        score += 30;
                    }
                }
            }

        }
        //Simpler way to remove bugs from the list, unfortunately this caused the Application to crash, so we had to improvise.
       // for(Bug ant: antsToRemove) {
            //allBugs.remove(ant);
        //}

    }

    //draw the ants or place bitmaps of the other bug types
    private void drawBug(float x, float y, Canvas canvas, Paint antColor, int bugType) {
        if(bugType == 1){
            float radiusMiddle = 4;
            float radiusHead = 10;
            float radiusBack = 12;
            canvas.drawCircle(x,y,radiusMiddle,antColor);
            canvas.drawCircle(x + 12,y,radiusHead,antColor);
            canvas.drawCircle(x - 15,y,radiusBack,antColor);

            canvas.drawLine(x - 20, y - 20, x, y, antColor);
            canvas.drawLine(x-20,y-20,x-27,y-23, antColor);
            canvas.drawLine(x-20,y+20,x,y,antColor);
            canvas.drawLine(x-20,y+20,x-27,y+23, antColor);

            canvas.drawLine(x,y-15,x,y,antColor);
            canvas.drawLine(x-5,y-20,x,y-15, antColor);
            canvas.drawLine(x,y+15,x,y,antColor);
            canvas.drawLine(x-5,y+20,x,y+15, antColor);

            canvas.drawLine(x,y,x+9,y-20,antColor);
            canvas.drawLine(x,y,x + 9,y+20, antColor);
            canvas.drawLine(x+9,y-20,x+12,y -20,antColor);
            canvas.drawLine(x+9,y+20,x+12,y+20, antColor);

            canvas.drawLine(x,y,x + 30,y - 5,antColor);
            canvas.drawLine(x,y,x+ 30,y + 5,antColor);
        }
       else if(bugType == 3){
            canvas.drawBitmap(butterflyDrawing,x,y,null);
            //canvas.drawCircle(x+70,y+75,45,antColor);
        }
        else{
            canvas.drawBitmap(beetleDrawing, x, y, null);
        }
    }

    //count how many ants made it to the basket

    private int countSuccess() {
        successfulAnts = 0;
        for(Bug ant: allBugs)
        {
            if((ant.health == -2)){
                successfulAnts++;
            }
        }
        return successfulAnts;
    }

    //check to see if all ants are dead/off the screen. Return true if so.
    private boolean checkAntStatusClassic() {
        int countDoneAnts = 0;
        for(Bug ant: allBugs)
        {
            if(ant.health <= 0){
                countDoneAnts++;
            }
            if((ant.xCoordinate > (screenWidth)) && (ant.health > 0)){//ant made it to the picnic basket
                ant.health = -2;//ant returned to pre game status
                ant.time = Integer.MAX_VALUE; //ant wont' be released during this game.
            }
        }
        if ((countDoneAnts == allBugs.size())) return true;
        else return false;
    }
    //check to see if you are out of lives
    private boolean checkAntStatusSurvival() {
        for(Bug bug: allBugs)
        {
            if((bug.xCoordinate > (screenWidth)) && (bug.health >= 1)){//ant made it to the picnic basket
                if(bug.bugType != 3){
                    lives-=1;
                }
                bug.health = -1;//ant returned to pre game status
                bug.time = Integer.MAX_VALUE; //ant wont' be released during this game.
            }
        }
        if (lives <= 0) return true;
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
        beetleDrawing = beetleDrawing.createScaledBitmap(beetleDrawing,150,150,true);
        butterflyDrawing = butterflyDrawing.createScaledBitmap(butterflyDrawing,150,150,true);

        startNewGame();
        textPaint.setTextSize(w / 25); // text size 1/25 of screen width

    }

    //start a new game, check to see what the difficulty constants should be set to, reset timer and reset Survival fields
    public void startNewGame() {
        String difString = difficulty.getString("difficulty","");
        String modeString = gameMode.getString("mode","");

        highScore1 = highScoreOne.getString("highScoreOne", "0 Null");
        highScore2 = highScoreTwo.getString("highScoreTwo", "0 Null");
        highScore3 = highScoreThree.getString("highScoreThree", "0 Null");
        highScore4 = highScoreFour.getString("highScoreFour", "0 Null");
        highScore5 = highScoreFive.getString("highScoreFive", "0 Null");

        highScore1Number = Integer.parseInt(highScore1.substring(0, highScore1.indexOf(" ")));
        highScore2Number = Integer.parseInt(highScore2.substring(0, highScore2.indexOf(" ")));
        highScore3Number = Integer.parseInt(highScore3.substring(0, highScore3.indexOf(" ")));
        highScore4Number = Integer.parseInt(highScore4.substring(0, highScore4.indexOf(" ")));
        highScore5Number = Integer.parseInt(highScore5.substring(0, highScore5.indexOf(" ")));




        if(modeString.equals("Classic")){
            mode = false;
        }else{
            mode = true;
        }

        if (difString.equals("Easy")){
            dif = false;
        } else {
            dif = true;
        }

        if (dif) {
            x_speed = X_ANT_SPEED_HARD;
            y_speed = Y_BUG_SPEED_HARD;
            num_ants = NUM_ANTS_HARD;
            time_ants = TIME_HARD;
        } else {
            x_speed = X_ANT_SPEED_EASY;
            y_speed = Y_BUG_SPEED_EASY;
            num_ants = NUM_ANTS_EASY;
            time_ants = TIME_EASY;
        }

        timeLeft = time_ants; // start the countdown at 10 seconds
        totalElapsedTime=0;
        successfulAnts = 0;
        score = 0;

        seconds = 0;
        level = 0;
        lives = 3;
        addMoreBugs = true;
        earthquakePowerUp = true;

        allBugs.clear();
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

        //if statement used to determine game mode. If mode is false, it's classic, else, it's survival.
        if(!mode){
            if (timeLeft <= 0.0)
            {
                timeLeft = 0.0;
                isGameOver = true; // the game is over
                gameThread.setRunning(false); // terminate thread
                showGameOverDialog(R.string.lose, R.string.gameOver); // show the losing dialog
            } else if (checkAntStatusClassic()){
                isGameOver = true;
                gameThread.setRunning(false);
                if (countSuccess() > 0) showGameOverDialog(R.string.lose, R.string.gameOver); else
                    showGameOverDialog(R.string.win, R.string.gameOver); //show winning dialog
            }
        }
        else{
            if(checkAntStatusSurvival()){
                insertTime(canvas);
                timeLeft = 0.0;
                isGameOver = true; // the game is over
                gameThread.setRunning(false); // terminate thread
                SharedPreferences.Editor highScoreEditor;
                if(score > highScore1Number){
                    showGameOverDialog(R.string.highScoreTitle, R.string.highScore); // show the losing dialog
                }
                else if(score > highScore5Number){
                    showGameOverDialog(R.string.highScoreTitle, R.string.closeHighScore); // show the losing dialog
                }
                else{
                    showGameOverDialog(R.string.gameOver, R.string.endSurvival); // show the losing dialog
                }
            }
        }
    }

    // display an AlertDialog when the game ends
    private void showGameOverDialog(final int messageId, final int message) {
        final DialogFragment gameResult =
                new DialogFragment() {
                    // create an AlertDialog and return it
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        // create dialog displaying String resource for messageId
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(messageId));
                        final EditText input = new EditText(getContext());
                        if(messageId == R.string.highScoreTitle){
                            saveHighScore = true;
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);
                        }

                        if(!mode){
                            builder.setMessage(getResources().getString(message, successfulAnts));
                        }
                        else{
                            builder.setMessage(getResources().getString(message, score, highScore1Number));
                        }
                        builder.setPositiveButton(R.string.reset_button_string,
                                new DialogInterface.OnClickListener() {
                                    // called when "Reset Game" Button is pressed
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(saveHighScore){
                                            userName = input.getText().toString();
                                            saveHighScore(userName);
                                            saveHighScore = false;
                                        }
                                        dialogIsDisplayed = false;
                                        startNewGame(); // set up and start a new game
                                    }
                                } // end anonymous inner class
                        ); // end call to setPositiveButton
                        builder.setNegativeButton(R.string.return_to_main,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(saveHighScore){
                                            userName = input.getText().toString();
                                            saveHighScore(userName);
                                            saveHighScore = false;
                                        }
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

    //method that will update shared preferences for high scores that have been changed.
    public void saveHighScore(String name){
        SharedPreferences.Editor highScoreEditor;
        if(score > highScore1Number){
            highScoreEditor = highScoreOne.edit();
            highScoreEditor.putString("highScoreOne", score + " " + name);
            highScoreEditor.apply();
            highScoreEditor = highScoreTwo.edit();
            highScoreEditor.putString("highScoreTwo", highScore1);
            highScoreEditor.apply();
            highScoreEditor = highScoreThree.edit();
            highScoreEditor.putString("highScoreThree", highScore2);
            highScoreEditor.apply();
            highScoreEditor = highScoreFour.edit();
            highScoreEditor.putString("highScoreFour", highScore3);
            highScoreEditor.apply();
            highScoreEditor = highScoreFive.edit();
            highScoreEditor.putString("highScoreFive", highScore4);
            highScoreEditor.apply();
        }
        else if(score > highScore2Number){
            highScoreEditor = highScoreTwo.edit();
            highScoreEditor.putString("highScoreTwo", score + " " + name);
            highScoreEditor.apply();
            highScoreEditor = highScoreThree.edit();
            highScoreEditor.putString("highScoreThree", highScore2);
            highScoreEditor.apply();
            highScoreEditor = highScoreFour.edit();
            highScoreEditor.putString("highScoreFour", highScore3);
            highScoreEditor.apply();
            highScoreEditor = highScoreFive.edit();
            highScoreEditor.putString("highScoreFive", highScore4);
            highScoreEditor.apply();

        }
        else if(score > highScore3Number){
            highScoreEditor = highScoreThree.edit();
            highScoreEditor.putString("highScoreThree", score + " " + name);
            highScoreEditor.apply();
            highScoreEditor = highScoreFour.edit();
            highScoreEditor.putString("highScoreFour", highScore3);
            highScoreEditor.apply();
            highScoreEditor = highScoreFive.edit();
            highScoreEditor.putString("highScoreFive", highScore4);
            highScoreEditor.apply();
        }
        else if(score > highScore4Number){
            highScoreEditor = highScoreFour.edit();
            highScoreEditor.putString("highScoreFour", score + " " + name);
            highScoreEditor.apply();
            highScoreEditor = highScoreFive.edit();
            highScoreEditor.putString("highScoreFive", highScore4);
            highScoreEditor.apply();
        }
        else if(score > highScore5Number){
            highScoreEditor = highScoreFive.edit();
            highScoreEditor.putString("highScoreFive", score + " " + name);
            highScoreEditor.apply();
        }
    }


    //add the time remaining and number of ants in basket to screen OR add the lives and score to the screen.
    public void insertTime(Canvas canvas) {
        Paint topBar = new Paint();
        topBar.setColor(Color.rgb(64,64,64));

        canvas.drawRect(0,0,screenWidth, 55, topBar);

        if(mode) {
           /* antLifeDring = antLifeDrawing.createScaledBitmap(antLifeDrawing,50,50,true);
            for(int i = 1; i <= lives; i++){

                canvas.drawBitmap(antLifeDrawing, screenWidth - (25 * i), 45, null);

            }*/ //Code for drawing ants instead of using numbers to represent lives
            canvas.drawText("Score: " + score, 25, 45, textPaint);
            //ensure that lives display is never under 0
            if (lives < 0){
                lives = 0;
            }
            canvas.drawText("Lives: " + lives, screenWidth - 200, 45, textPaint);
            if(earthquakePowerUp){
                earthquakeDrawing = earthquakeDrawing.createScaledBitmap(earthquakeDrawing,50,50,true);
                canvas.drawBitmap(earthquakeDrawing, 25, screenHeight - 75, null);
            }
        }
        else{
            canvas.drawText(getResources().getString(R.string.time_left, timeLeft), 25, 45, textPaint);
            canvas.drawText(getResources().getString(R.string.results,countSuccess()),25, screenHeight - 45, textPaint);
        }
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

    public static double randDouble(int min, int max) {
        Random rand = new Random();

        double randomNum = rand.nextDouble();
        randomNum = randInt(min,max) * randomNum;
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
    //Timer Task to increment the level
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            seconds += 1;
            level = seconds / 10;
            addMoreBugs = true;
            if(level < 1){
                level = 1;
            }
        }

    }
    //Class to sort bugs so Butterflies are always on top
    // Code help from http://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
    public class CustomComparator implements Comparator<Bug> {
        @Override
        public int compare(Bug o1, Bug o2) {
            return Integer.compare(o1.bugType,o2.bugType);
        }
    }
}