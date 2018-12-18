package Collision;

import Graphics.Shapes.Circle;
import java.awt.*;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Dual Circles is designed as a collision model. The circles follow a sprite and circle 1 is centered over the sprite while circle 2 has an offset from the center.
 */
public class DualCircles {

    public Circle circle1;
    public Circle circle2;

    // Offset's are for Circle 2 in order to move and rotate in sync with Circle 1's center
    private int offsetR;
    private int offsetX;

    // Dual Circles
    public DualCircles(int r1, int r2, int offsetR, int offsetX) {

        this.offsetR = offsetR;
        this.offsetX = offsetX;

        // new Circle (float x, float y, int r)
        this.circle1 = new Circle(0, 0, r1);
        this.circle2 = new Circle(0, 0, r2);
    }

    // Draw
    public void draw(Graphics2D g, double cosA, double sinA) {

        // Draw Circle 1
        circle1.draw(g, cosA, sinA);

        // Draw Circle 2
        circle2.draw(g, cosA, sinA);
    }

    // Get OffSetR
    public int getOffsetR() { return offsetR; }

    // Get OffSetX
    public int getOffsetX() { return offsetX; }

} // End of Class.