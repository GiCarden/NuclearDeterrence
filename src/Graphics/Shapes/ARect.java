package Graphics.Shapes;

import java.awt.*;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class ARect {

    private int x;
    private int y;
    private int w;
    private int h;
    private Color color;

    // ARect
    public ARect(int x, int y, int w, int h, Color color) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;
    }

    // Draw
    public void draw(Graphics2D g) {

        g.setColor(color);
        g.drawRect(x, y, w, h);
    }

    // Fill
    public void fill(Graphics2D g) {

        g.setColor(color);
        g.fillRect(x, y, w, h);
    }

    // Set X
    public void setX(int x) { this.x = x; }

    // Set y
    public void setY(int y) { this.y = y; }

    // Set W
    public void setW(int w) { this.w = w; }

    // Set H
    public void setH(int h) { this.h = h; }

    // Get X
    public int getX() { return this.x; }

    // Get y
    public int getY() { return this.y; }

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
