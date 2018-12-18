package Graphics.Shapes;

import Graphics.Sprites.Helicopter;
import Math.Calculate;
import java.awt.*;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class Arrow extends PolygonModel {

    // Arrow
    public Arrow(int x, int y, int angle) { super(x, y, angle); }

    // Get X Struct
    public int[][] getXStruct() {

        int[][] xStructure = { { 0,  20, 20, 32, 20, 20 }, };

        return xStructure;
    }

    // Get Y Struct
    public int[][] getYStruct() {

        int[][] yStructure = { { 0,  0, 3, 0, -3, 0 }, };

        return yStructure;
    }

    // Draw
    public void draw(Graphics g, Color color) { super.draw(g, getXStruct(), getYStruct(), color); }

    // Turn Towards Point
    public void turnArrowTowards(Helicopter player, float x2, float y2) {

        float x1 = player.getX() + (player.getWidth()/2);
        float y1 = player.getY() + (player.getHeight()/2);

        // Rotate the arrow towards the next objective
        double d = Calculate.turnDistance(x1, y1, x2, y2, super.getCosA(), super.getSinA());

        // The values in (d < -30) and (d > 30) sets a smaller range
        // to turn towards and reduces the amount of rotations needed.
        if(d < -30) rotate(1);
        if(d > 30)  rotate(-1);
    }

} // End of Class.