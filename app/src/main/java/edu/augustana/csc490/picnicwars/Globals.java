package edu.augustana.csc490.picnicwars;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

/**
 * Created by Reed on 4/1/2015.
 */
public class Globals extends Application {

    private boolean isHard;

    public void setData(boolean newDifficulty) {
        this.isHard = newDifficulty;
    }

    public boolean getData(){
        return isHard;
    }
}
