package Graphics;

import java.awt.*;

/**
 *  Code created by:
 *  Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class Billboard {

    private Image image;
    private int x;
    private int y;
    private int w;
    private int h;

    private boolean isOnScreen;

    // Billboard
    public Billboard(Image image) {

        this.image = image;
        this.isOnScreen = false;
        this.w = image.getWidth(null);
        this.h = image.getHeight(null);
    }

    // Draw
    public void draw(Graphics2D g, int offsetX, int offsetY) {

        int imageX = Math.round(x) + offsetX;
        int imageY = Math.round(y) + offsetY;

        g.drawImage(image, imageX, imageY, null);
    }

    // Set X
    public void setX(int x) { this.x = x; }

    // Set Y
    public void setY(int y) { this.y = y; }

    // Get X
    public int getX() { return this.x; }

    // Get Y
    public int getY() { return this.y; }

    // Get W
    public int getWidth() { return this.w; }

    // Get H
    public int getHeight() { return this.h; }

    // Set Is On Screen
    public void setOnScreen(boolean state) { this.isOnScreen = state; }

    // Is On Screen
    public boolean isOnScreen() { return isOnScreen; }

} // End of Class.