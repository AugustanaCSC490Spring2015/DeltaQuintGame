package edu.augustana.csc490.picnicwars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Switch;

/**
 * Created by Reed on 4/1/2015.
 * splash screen for game, allows user to choose a different difficulty, offers instructions
 * and allows to start a new game
 *  source for picnic blanket background: http://cliparts.co/cliparts/rTn/rkM/rTnrkMpgc.png
 */
public class SplashActivity extends Activity {
    private SharedPreferences difficulty;
    private SharedPreferences gameMode;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        CheckBox hardDif = (CheckBox) findViewById(R.id.hardCheckBox);
        final Switch survivalMode = (Switch) findViewById(R.id.UISurvivalMode);

        difficulty = getSharedPreferences("difficulty", MODE_PRIVATE);
        gameMode = getSharedPreferences("mode", MODE_PRIVATE);

        String tempDif = difficulty.getString("difficulty","");
        String tempMode = gameMode.getString("mode","");

        if (tempDif.equals("Hard")) {
            hardDif.setChecked(true);
        }

        SharedPreferences.Editor prefEditor = difficulty.edit();

        if (!tempDif.equals("Hard")) {
            prefEditor.putString("difficulty", "Easy");
            prefEditor.apply();
        }

        if(tempMode.equals("Survival")){
            survivalMode.setChecked(true);
        }
        SharedPreferences.Editor modeEditor = gameMode.edit();

        if(!tempMode.equals("Survival")){
            modeEditor.putString("mode", "Classic");
            modeEditor.apply();
        }

        Button next = (Button) findViewById(R.id.buttonStartGame);
        ImageButton highScores = (ImageButton) findViewById(R.id.highScoreButton);

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        highScores.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), HighScores.class);
                startActivityForResult(myIntent, 0);
            }
        });

        hardDif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleDif();
            }
        });
        survivalMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleMode(survivalMode);
            }
        });
    }
        //called in the click listener for "Hard difficulty" checkbox
        //toggles the difficulty setting
        private void toggleDif() {
            difficulty = getSharedPreferences("difficulty",MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = difficulty.edit();
            String dif = difficulty.getString("difficulty","");
            if (dif.equals("Easy")) {
                prefEditor.remove("difficulty");
                prefEditor.putString("difficulty","Hard");
            } else {
                prefEditor.remove("difficulty");
                prefEditor.putString("difficulty","Easy");
            }
            prefEditor.apply();
    }
    //toggles the current game mode
       private void toggleMode(Switch survivalMode) {
           gameMode = getSharedPreferences("mode", MODE_PRIVATE);
           SharedPreferences.Editor modeEditor = gameMode.edit();
           String mode = gameMode.getString("mode","");
           if(survivalMode.isChecked()){
               modeEditor.remove("mode");
               modeEditor.putString("mode", "Survival");
           }else{
               modeEditor.remove("mode");
               modeEditor.putString("mode", "Classic");
           }
            modeEditor.apply();

       }

}



