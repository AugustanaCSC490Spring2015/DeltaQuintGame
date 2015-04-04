package edu.augustana.csc490.picnicwars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

/**
 * Created by Reed on 4/1/2015.
 * splash screen for game, allows user to choose a different difficulty, offers instructions
 * and allows to start a new game
 *  source for picnic blanket background: http://cliparts.co/cliparts/rTn/rkM/rTnrkMpgc.png
 */
public class SplashActivity extends Activity {
    private SharedPreferences difficulty;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        CheckBox hardDif = (CheckBox) findViewById(R.id.hardCheckBox);

        difficulty = getSharedPreferences("difficulty", MODE_PRIVATE);

        String tempDif = difficulty.getString("difficulty","");

        if (tempDif.equals("Hard")) {
            hardDif.setChecked(true);
        }

        SharedPreferences.Editor prefEditor = difficulty.edit();

        if (!tempDif.equals("Hard")) {
            prefEditor.putString("difficulty", "Easy");
            prefEditor.apply();
        }

        Button next = (Button) findViewById(R.id.buttonStartGame);

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        hardDif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleDif();
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

}



