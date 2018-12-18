package Collision;

import Graphics.Billboard;
import Graphics.Sprites.Projectile;
import Graphics.Sprite;
import Graphics.Sprites.Silo;

import java.awt.*;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Screen Contains draws a Rectangle around the full screen based on the set game resolution.
 *  The Rectangle moves to center over a Sprite's location and will not move out of screen boundary.
 *
 *  A method called Contains allows checking if a Sprite or a Projectile is inside the Rectangle and returning a
 *  boolean if it is on or off screen.
 */
public class ScreenContains {

    // Position
    private float x;
    private float y;

    // Rectangle Size
    private int width;
    private int height;

    // Game Map Size
    private int mapWidth;
    private int mapHeight;

    // Screen Resolution
    private int screenWidth;
    private int screenHeight;

    // Screen Contains ----------------------------------------------------------------------------------------------//
    public ScreenContains(int width, int height, int screenWidth, int screenHeight, int mapWidth, int mapHeight) {

        this.x = 0;
        this.y = 0;

        this.width = width;
        this.height = height;

        // Map Size is based on 64 Bit Tile Size
        this.mapWidth = mapWidth * 64;
        this.mapHeight = mapHeight * 64;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    // Draw
    public void draw(Graphics2D g, int offsetX, int offsetY) {

        // Adjust Position with Map Offset
        int rectX = Math.round(getX()) + offsetX;
        int rectY = Math.round(getY()) + offsetY;

        g.setColor(Color.RED);
        g.drawRect(rectX, rectY, width, height);
    }

    // Gets current X position
    public float getX() { return x; }

    // Gets current Y position
    public float getY() { return y; }

    // Sets current X position
    private void setX(float x) { this.x = x; }

    // Sets current Y position
    private void setY(float y) { this.y = y; }

    // Set Rectangle Position Centered from Sprite
    public void setRectPosition(Sprite sprite) {

        // Update X and Y Separately then check Collision against Map Boundary.

        // Get Sprite's X Position
        float spriteX = sprite.getX();

        // Set Rect X based on Player's X plus an offset
        setX(spriteX - (screenWidth / 2) + 64);

        // Adjust the Rectangle to the left edge of the screen
        if(x < 0) setX(0);

        // Adjust the Rectangle to the right edge of the screen
        if(x > mapWidth - screenWidth) setX(mapWidth - screenWidth);

        // Get Sprite Y Position
        float spriteY = sprite.getY();

        // Set Rect Y based on Sprite's Y plus an offset
        setY(spriteY - (screenHeight / 2) + 64);

        // Adjust the Rectangle to the top edge of the screen
        if(y < 0) setY(0);

        // Adjust the Rectangle to the bottom edge of the screen
        if(y > mapHeight - screenHeight) setY(mapHeight - screenHeight);
    }

    // Sprite Is on Screen
    public boolean contains(Sprite sprite) {

        // Grab the sprite's location
        float spriteX = sprite.getX() + (sprite.getWidth());
        float spriteY = sprite.getY() + (sprite.getHeight());

        int spriteH = sprite.getHeight();
        int spriteW = sprite.getWidth();

        // Return True if the sprite's location is inside the Rectangle
        return (spriteY > y - spriteH &&
                spriteX > x - spriteW &&
                spriteY < y + height + spriteH &&
                spriteX < x + width + spriteW);
    }

    // Projectile Is on Screen
    public boolean contains(Projectile projectile) {

        // Grab the projectile's location
        float projectileX = projectile.getX();
        float projectileY = projectile.getY();

        int projectileH = projectile.getHeight();
        int projectileW = projectile.getWidth();

        // Return True if the projectile's location is inside the Rectangle
        return (projectileY > y - projectileH &&
                projectileX > x - projectileW &&
                projectileY < y + height + projectileH &&
                projectileX < x + width + projectileW);
    }

    // Billboard Is on Screen
    public boolean contains(Billboard billboard) {

        // Grab the sprite's location
        float billboardX = billboard.getX();
        float billboardY = billboard.getY();

        int billboardH = billboard.getHeight();
        int billboardW = billboard.getWidth();

        // Return True if the billboard's location is inside the Rectangle
        return (billboardY > (y - billboardH) &&
                billboardX > (x - billboardW) &&
                billboardY < (y + height + billboardH) &&
                billboardX < (x + width + billboardW));
    }

    // Silo Is on Screen
    public boolean contains(Silo silo) {

        // Grab the sprite's location
        float siloX = silo.getX();
        float siloY = silo.getY();

        int siloH = silo.getHeight();
        int siloW = silo.getWidth();

        // Return True if the billboard's location is inside the Rectangle
        return (siloY > (y - siloH) &&
                siloX > (x - siloW) &&
                siloY < (y + height + siloH) &&
                siloX < (x + width + siloW));
    }

} // End of Class.