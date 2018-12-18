package Graphics.Shapes;

import java.awt.*;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class RoundRect {

    private int x;
    private int y;
    private int w;
    private int h;
    private int aW;
    private int aH;
    private Color color;

    // ARect
    public RoundRect(int x, int y, int w, int h, int aW, int aH, Color color) {

        this.x = x;
        this.y = y;
        this.w = w;
        this. h = h;
        this.aW = aW;
        this.aH = aH;
        this.color = color;
    }

    // Draw
    public void draw(Graphics2D g) {

        g.setColor(color);
        g.drawRoundRect(x, y, w, h, aW, aH);
    }

    // Fill
    public void fill(Graphics2D g) {

        g.setColor(color);
        g.fillRect(x, y, w, h);
    }

    // Get X
    public int getX() { return x; }

    // Get Y
    public int getY() { return y; }

    // Get W
    public int getW() { return this.w; }

    // Get H
    public int getH() { return this.h; }

    // Set Color
    public void setColor(Color color) { this.color = color; }

    // ARect Contains an Object
    public boolean contains(int x2, int y2) {

        // Return True if an object's location is inside the rectangle
        return (y2 > y && x2 > x && y2 < y + h && x2 < x + w);
    }

} // End of Class.