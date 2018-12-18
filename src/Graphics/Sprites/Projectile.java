package Graphics.Sprites;

import Graphics.Animation;
import Math.Calculate;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class Projectile {

    // Amount of time to go before Projectile dies.
    private static final int NORMAL_TIME  = 3200;
    private int DIE_TIME;
    public  static final int STATE_NORMAL = 0;
    public  static final int STATE_DYING  = 1;
    public  static final int STATE_DEAD   = 2;
    Image image;

    // Animation
    private Animation anim;
    private Animation dyingAnim;

    // State of the Projectile
    private int state;
    private long stateTime;

    // Name is Player or Enemy
    private boolean ally;

    private String type;

    // Position
    private float x;
    private float y;

    // Radius for Collision
    private int r;

    // Velocity
    private float dx;
    private float dy;

    // Image size
    private int w;
    private int h;

    // Projectile is on Screen
    private boolean isOnScreen;

    // Speed and Distance
    private float maxSpeed;

    // Angle
    private final int angle;
    private final double cosA;
    private final double sinA;

    // Projectile //
    public Projectile(float x, float y, double cosA, double sinA, float maxSpeed, Image image, int w, int h, int r,
                                           int angle, Animation dying, boolean ally, int dieTime, String type) {

        this.x = x;
        this.y = y;
        this.r = r;
        this.w = w;
        this.h = h;
        this.ally = ally;
        this.type = type;
        this.maxSpeed = maxSpeed;
        this.image = image;
        this.anim = null;
        this.dyingAnim = dying;
        this.state = STATE_NORMAL;
        this.DIE_TIME = dieTime;
        this.angle = angle;
        this.cosA = cosA;
        this.sinA = sinA;
        this.isOnScreen = true;
    }

    // Update
    public void update(long elapsedTime) {

        // Updates this Projectile's position based on the velocity.
        x += dx * elapsedTime;
        y += dy * elapsedTime;

        // If Projectile is Dying then update Animation
        Animation newAnim = anim;

        // Set Animation to Dying
        if(state == STATE_DYING) newAnim = dyingAnim;

        // Blank Animation stops any other animation from updating in the background.
        if(state == STATE_DEAD) newAnim = null;

        // Update the Animation
        if(anim != newAnim) {

            anim = newAnim;

            if(anim != null) anim.start();
        } else {

            if(anim != null) anim.update(elapsedTime);
        }

        // Update to "dead" state
        stateTime += elapsedTime;

        if(state == STATE_DYING && stateTime >= DIE_TIME) { setState(STATE_DEAD); }

        if(state == STATE_NORMAL && stateTime >= NORMAL_TIME) { setState(STATE_DEAD); }
    }

    // Draw
    public void draw(GraphicsConfiguration gc, Graphics2D g, int x, int y) {

        if(state == STATE_NORMAL) g.drawImage(image, x, y, null);

        if(state == STATE_DYING && anim != null) {

            // Rotate animation image
            Image newAnim = getRotatedImage(gc, anim.getImage(), angle);

            // Draw Death Animation
            g.drawImage(newAnim, x, y, null);
        }
    }

    // Checks if this character is alive
    public boolean isAlive() { return state == STATE_NORMAL; }

    // Character is Dying
    public boolean isDying() { return state == STATE_DYING; }

    // Is Ally Projectile
    public boolean isAlly() { return this.ally; }

    // Get Max Speed
    public float getMaxSpeed() { return maxSpeed; }

    // Get X
    public float getX() { return x; }

    // Get Y
    public float getY() { return y; }

    // Get Type
    public String getType() { return this.type; }

    // Move this Sprite Forward
    public void moveForward() { dx = (float)(getMaxSpeed() * cosA); dy = (float)(getMaxSpeed() * sinA); }

    // Stop Movement
    public void stopMovement() { dx = 0; dy = 0; }

    // Sets the state of this Projectile to STATE_DYING, or STATE_DEAD
    public void setState(int state) { if(this.state != state) { this.state = state; stateTime  = 0; } }

    // Projectile is Dead
    public boolean isDead() { return state == STATE_DEAD; }

    // Sets this Sprite's current X position
    public void setX(float x) { this.x = x; }

    // Sets this Sprite's current Y position
    public void setY(float y) { this.y = y; }

    // Get this Sprite's Velocity X
    public float getVelocityX() { return dx; }

    // Get this Sprite's Velocity Y
    public float getVelocityY() { return dy; }

    // Get Width
    public int getWidth() { return this.w; }

    // Get Height
    public int getHeight() { return this.h; }

    // Get Rotated Image
    private Image getRotatedImage(GraphicsConfiguration gc, Image image, int angle) {

        // Set up the transform
        AffineTransform transform = new AffineTransform();
        transform.translate(image.getWidth(null) / 2.0, image.getHeight(null) / 2.0 );

        transform.rotate(Math.toRadians(angle));

        // Put origin back to upper left corner
        transform.translate(-image.getWidth(null) / 2.0, -image.getHeight(null) / 2.0);

        // Create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.BITMASK);

        // Draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        AffineTransform origTransform = g.getTransform();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, transform, null);
        g.setTransform(origTransform);
        g.dispose();

        return newImage;
    }

    // Is on Screen
    public boolean isOnScreen() { return this.isOnScreen; }

    // Set Is on Screen
    public void setIsOnScreen(boolean onScreen) { this.isOnScreen = onScreen; }

    // Projectile Has Collided with Circle
    public boolean hasCollidedWith(int x1, int y1, int x2, int y2, int r2) {

        // Take the distance between the projectile and the collision circle
        double d = Calculate.distanceTo(x1, y1, x2, y2);

        // Check for negative distance with the projectile Radius and the Collision Circle Radius
        return d < Calculate.distanceTo(r, r2);
    }

} // End of Class.