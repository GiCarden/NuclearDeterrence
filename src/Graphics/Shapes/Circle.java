package Graphics.Shapes;

import Math.Calculate;
import java.awt.*;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class Circle {

    private float x;
    private float y;
    private int r;

    // Circle
    public Circle(float x, float y, int r) {

        this.x = x;
        this.y = y;
        this.r = r;
    }

    // Draw
    public void draw(Graphics2D g, double cosA, double sinA) {

        g.setColor(Color.RED);
        g.drawOval((int)x-r, (int)y-r, 2*r, 2*r);

        // Directional Line
        g.drawLine((int)x, (int)y, (int)(x+r*cosA), (int)(y+r*sinA));
    }

    // Draw
    public void draw(Graphics2D g, float x, float y) {

        g.setColor(Color.RED);
        g.drawOval((int)x-r, (int)y-r, 2*r, 2*r);
    }

    // Has Collided with Circle
    public boolean hasCollidedWith(float x1, float y1, float x2, float y2, int r2) {

        double d = Calculate.distanceTo(x1, y1, x2, y2);
        return d < (r + r2) * (r + r2);
    }

    // Get X
    public float getX() { return x; }

    // Get Y
    public float getY() { return y; }

    // Set X
    public void setX(float x) { this.x = x; }

    // Set Y
    public void setY(float y) { this.y = y; }

    // Get R
    public int getR() { return r; }

} // End of Class.