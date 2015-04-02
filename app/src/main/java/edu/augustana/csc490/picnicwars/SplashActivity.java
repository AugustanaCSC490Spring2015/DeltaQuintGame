package edu.augustana.csc490.picnicwars;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

/**
 * Created by Reed on 4/1/2015.
 */
public class SplashActivity extends Activity {
    final int PREF_MODE = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferenceSettings;
        final SharedPreferences.Editor preferenceEditor;

        preferenceSettings = getPreferences(PREF_MODE);
        preferenceEditor = preferenceSettings.edit();
        preferenceEditor.putBoolean("current",false);
        preferenceEditor.commit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Switch difficultySwitch = (Switch) findViewById(R.id.difficultySwitch);
        difficultySwitch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                toggleSwitch();
            }
        });

        Button next = (Button) findViewById(R.id.buttonStartGame);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    public void toggleSwitch() {
        SharedPreferences preferenceSettings;
        final SharedPreferences.Editor preferenceEditor;
        preferenceSettings = getPreferences(PREF_MODE);
        preferenceEditor = preferenceSettings.edit();

        boolean tempStatus = preferenceSettings.getBoolean("Current",true);
        preferenceEditor.remove("current");
        preferenceEditor.putBoolean("current",!tempStatus);
        preferenceEditor.commit();
    }
}
