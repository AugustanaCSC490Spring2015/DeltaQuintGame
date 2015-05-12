package edu.augustana.csc490.picnicwars;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by michaelmadden11 on 4/13/2015.
    mhanson git test
 */
public class Bug {

    public int antColor;
    public int xCoordinate;
    public int yCoordinate;
    public int health;
    public double time;
    public double speed;
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
