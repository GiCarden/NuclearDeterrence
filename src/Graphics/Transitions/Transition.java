package Graphics.Transitions;

import java.awt.*;

// Transition Class
public class Transition {

    private float alpha;
    private Color color;
    private int x;
    private int y;
    private int w;
    private int h;

    // Transition
    public Transition(int x, int y, int w, int h, float alpha, Color color) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.alpha = alpha;
        this.color = color;
    }

    // Update
    public void update(float a) {

        alpha += a;

        // Keep alpha in range between 0.0 and 1.0
        if(alpha < 0) alpha = 0;

        if(alpha > 1) alpha = 1;
    }

    // Get X
    public int getX() { return this.x; }

    // Set X
    public void setX(int x) { this.x = x; }

    // Get Y
    public int getY() { return this.y; }

    // Set Y
    public void setY(int y) { this.y = y; }

    // Get W
    public int getW() { return this.w; }

    // Get H
    public int getH() { return this.h; }

    // Set Alpha
    public void setAlpha(float alpha) { this.alpha = alpha; }

    // Get Alpha
    public float getAlpha() { return this.alpha; }

    // Get Color
    public Color getColor() { return this.color; }

} // End of Class.
