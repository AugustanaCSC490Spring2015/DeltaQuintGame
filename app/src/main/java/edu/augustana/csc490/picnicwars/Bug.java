package edu.augustana.csc490.picnicwars;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by michaelmadden11 on 4/13/2015.
    mhanson git test
 */
public class Bug {

    public int antColor; //Determines the ants color
    public int xCoordinate; //Keeps track of the bug's x coordinate
    public int yCoordinate;//Keeps track of the bug's y coordinate
    public int health; //Keeps track of the bugs health. 1 is the standard for the bugs, 3 is for beetles.
    public double time; //Determines when to release the bug
    public double speed; // Bug's speed
    public int bugType; //1 for ant; 2 for butterfly; 3 for beetle

    public Bug(int x, int y, int color, int health, double time, int bugType, double speed)
    {
        xCoordinate = x;
        yCoordinate = y;
        antColor = color;
        this.time = time;
        this.health = health;
        this.bugType = bugType;
        this.speed = speed;
    }
}
