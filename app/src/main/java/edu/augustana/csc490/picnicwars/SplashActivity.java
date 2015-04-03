package edu.augustana.csc490.picnicwars;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Reed on 4/1/2015.
 */
public class SplashActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        SharedPreferences settings = getSharedPreferences("Difficulty", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("Difficlty", "Hard");
        prefEditor.commit();

        CheckBox hardDif = (CheckBox) findViewById(R.id.hardCheckBox);
        Button next = (Button) findViewById(R.id.buttonStartGame);

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        hardDif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleDifficulty();
            }
        });

    }

        public void toggleDifficulty() {
            SharedPreferences sharedPref = getSharedPreferences("Difficulty", MODE_PRIVATE);
            String tempDif = sharedPref.getString("Difficulty", "Easy");
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.remove("Difficulty");
            if (tempDif.equals("Hard")) {
                prefEditor.putString("Difficulty","Easy");
            } else {
                prefEditor.putString("Difficulty","Hard");
            }

            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context,"Test",Toast.LENGTH_SHORT);
            toast.show();
        }

}
