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
    private SharedPreferences difficulty;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        difficulty = getSharedPreferences("difficulty", MODE_PRIVATE);

        SharedPreferences.Editor prefEditor = difficulty.edit();
        prefEditor.putString("difficulty", "Easy");
        prefEditor.apply();

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
                toggleDif();
            }
        });
    }
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
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context,difficulty.getString("difficulty",""), Toast.LENGTH_LONG);
            toast.show();

    }

}



