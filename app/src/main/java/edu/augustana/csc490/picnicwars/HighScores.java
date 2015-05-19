package edu.augustana.csc490.picnicwars;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by michaelmadden11 on 5/13/2015.
 */
public class HighScores extends Activity {
    private TextView textHighScore1;
    private TextView textHighScore2;
    private TextView textHighScore3;
    private TextView textHighScore4;
    private TextView textHighScore5;


    private SharedPreferences highScoreOne; // To access the high scores
    private SharedPreferences highScoreTwo; // To access the high scores
    private SharedPreferences highScoreThree; // To access the high scores
    private SharedPreferences highScoreFour; // To access the high scores
    private SharedPreferences highScoreFive; // To access the high scores

    String highScore1;
    String highScore2;
    String highScore3;
    String highScore4;
    String highScore5;

        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.high_score);
            setHighScores();
        }

        //Sets the five text views on the High Score screen to the current high scores.
        public void setHighScores(){
            textHighScore1 = (TextView) findViewById(R.id.uiHighOne);
            textHighScore2 = (TextView) findViewById(R.id.uiHighTwo);
            textHighScore3 = (TextView) findViewById(R.id.uiHighThree);
            textHighScore4 = (TextView) findViewById(R.id.uiHighFour);
            textHighScore5 = (TextView) findViewById(R.id.uiHighFive);


            highScoreOne = getSharedPreferences("highScoreOne", Context.MODE_PRIVATE);
            highScoreTwo = getSharedPreferences("highScoreTwo", Context.MODE_PRIVATE);
            highScoreThree = getSharedPreferences("highScoreThree", Context.MODE_PRIVATE);
            highScoreFour = getSharedPreferences("highScoreFour", Context.MODE_PRIVATE);
            highScoreFive = getSharedPreferences("highScoreFive", Context.MODE_PRIVATE);

            highScore1 = highScoreOne.getString("highScoreOne", "0 Null");
            highScore2 = highScoreTwo.getString("highScoreTwo", "0 Null");
            highScore3 = highScoreThree.getString("highScoreThree", "0 Null");
            highScore4 = highScoreFour.getString("highScoreFour", "0 Null");
            highScore5 = highScoreFive.getString("highScoreFive", "0 Null");

            textHighScore1.setText("" + highScore1);
            textHighScore2.setText("" + highScore2);
            textHighScore3.setText("" + highScore3);
            textHighScore4.setText("" + highScore4);
            textHighScore5.setText("" + highScore5);
        }
}
