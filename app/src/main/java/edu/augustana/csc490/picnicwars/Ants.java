package edu.augustana.csc490.picnicwars;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by michaelmadden11 on 4/13/2015.
    mhanson git test
 */
public class Ants {

    public int antColor;
    public int xCoordinate;
    public int yCoordinate;
    public int health;
    public double time;

    public Ants(int x, int y, int color, int health, double time)
    {
        xCoordinate = x;
        yCoordinate = y;
        antColor = color;
        this.time = time;
        this.health = health;
    }
}
