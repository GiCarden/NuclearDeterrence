package Graphics.Sprites;

import Graphics.Character;
import Math.Calculate;
import Collision.DualCircles;
import Graphics.Animation;
import Math.Lookup;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  A Helicopter is a Sprite containing seven images and an animation.
 *  Images control the sprite's rotation and direction.
 *  The animation is for when the Helicopter is dying and the static images will not be displayed.
 */
public class Helicopter extends Character {

    // Images
    private Image image;
    private final Image fwImg;
    private final Image bwImg;
    private final Image ltImg;
    private final Image rtImg;
    private final Image hoverImg;
    private Image originalImage;

    // Rotation
    private int angle;
    private double cosA;
    private double sinA;
    private int rotateSpeed;

    // Bullets
    private int bulletReloadTime;
    private int bulletAmmo;
    private boolean bulletAmmoIsEmpty;
    private Timer refillBulletAmmo;

    // Rockets
    private int rocketReloadTime;
    private int rocketAmmo;
    private boolean rocketAmmoIsEmpty;
    private Timer refillRocketAmmo;
    private int rockets;

    // Helicopter Images
    public Helicopter(String name, Image[] images, Animation blades, Animation dying, Object collision,
                      int type, int offset) {

        super(name, blades, dying, collision, offset, images[1].getWidth(null),
                images[1].getHeight(null), type);

        this.image = images[0];
        this.hoverImg = images[0];
        this.fwImg = images[1];
        this.bwImg = images[2];
        this.ltImg = images[3];
        this.rtImg = images[4];
        this.originalImage = images[0];

        this.angle = 0;

        cosA = Lookup.cos[angle];
        sinA = Lookup.sin[angle];

        // Set Cos and Sin for Sprite Class
        super.setCosA(cosA);
        super.setSinA(sinA);

        this.rotateSpeed = 5;

        this.bulletAmmo = 2;
        this.bulletReloadTime = 250;
        this.bulletAmmoIsEmpty = false;
        this.refillBulletAmmo = new Timer();

        this.rockets = 8;
        this.rocketAmmo = 2;
        this.rocketReloadTime = 1000;
        this.rocketAmmoIsEmpty = false;
        this.refillRocketAmmo = new Timer();
    }

    // Update
    public void update(long elapsedTime) { super.update(elapsedTime); }

    // Draw
    public void draw(GraphicsConfiguration gc, Graphics2D g, int offsetX, int offsetY, boolean colBoundVisible) {

        // DRAW SPRITE
        // Adjust Position with Map Offset
        int spriteX = Math.round(getX()) + offsetX;
        int SpriteY = Math.round(getY()) + offsetY;

        if(super.isAlive() && anim != null) {

            // Draw Body Image
            g.drawImage(image, spriteX, SpriteY, null);

            // Draw the Rotor Blades
            g.drawImage(super.getAnimImage(), spriteX, SpriteY, null);
        }

        // Draw Animation if Dying!
        if(super.isDying() && anim != null) {

            // Rotate animation image
            Image newAnim = getRotatedImage(gc, super.getAnimImage(), angle);

            // Draw Death Animation
            g.drawImage(newAnim, spriteX, SpriteY, null);
        }

        // DRAW COLLISION MODEL
        // Draw Collision Boundary if Visible
        if(colBoundVisible) {

            // Get Collision Model
            DualCircles collision = (DualCircles) getCollisionPoly();

            // Set the X/Y of Circle 1
            collision.circle1.setX(spriteX + getOffset());
            collision.circle1.setY(SpriteY + getOffset());

            // Set the X/Y of Circle 2
            int r2 = collision.circle2.getR();

            collision.circle2.setX((int)((collision.circle1.getX()+collision.getOffsetX())-
                                                                    (collision.getOffsetR()+r2) * getBodyCosA()));
            collision.circle2.setY((int)((collision.circle1.getY())-(collision.getOffsetR()+r2) * getBodySinA()));

            // Draw the Circles
            collision.draw(g, cosA, sinA);
        }
    }

    // MOVEMENT
    // Get Body Angle
    public int getAngle() { return this.angle; }

    // Get Body CosA
    private double getBodyCosA() { return this.cosA; }

    // Get Body SinA
    private double getBodySinA() { return this.sinA; }

    // Hover
    public void hover(GraphicsConfiguration gc) {

        // Change to Hover Image
        setImage(hoverImg);

        // Rotate to Sprites Main Angle
        rotateImage(gc, 0);
    }

    // Move Forward
    public void moveForward(GraphicsConfiguration gc) {

        // Change to Forward Image
        setImage(fwImg);

        // Rotate to Sprites Main Angle
        rotateImage(gc, 0);

        super.moveForward();
    }

    // Move Backward
    public void moveBackward(GraphicsConfiguration gc) {

        // Change to Forward Image
        setImage(bwImg);

        // Rotate to Sprites Main Angle
        rotateImage(gc, 0);

        super.moveBackward();
    }

    // Strafe Left
    public void strafeLeft(GraphicsConfiguration gc) {

        // Change to Strafe Left Image
        setImage(ltImg);

        // Rotate to Sprites Main Angle
        rotateImage(gc, 0);

        // Calculate Left Angle
        int newAngle = Calculate.angle(angle, -90);

        double newCosA  = Lookup.cos[newAngle];

        double newSinA  = Lookup.sin[newAngle];

        super.moveForward(newCosA, newSinA);
    }

    // Strafe Right
    public void strafeRight(GraphicsConfiguration gc) {

        // Change to Forward Image
        setImage(rtImg);

        // Rotate to Sprites Main Angle
        rotateImage(gc, 0);

        // Calculate Right Angle
        int newAngle = Calculate.angle(angle, 90);

        double newCosA  = Lookup.cos[newAngle];

        double newSinA  = Lookup.sin[newAngle];

        super.moveForward(newCosA, newSinA);
    }

    // Rotate Main Image
    public void rotate(GraphicsConfiguration gc, int direction) { rotateImage(gc, this.rotateSpeed * direction); }

    // Turn Towards Sprite
    public void turnTowards(GraphicsConfiguration gc, float x1, float y1, float x2, float y2) {

        // Rotate this Sprite towards another Sprite
        double d = Calculate.turnDistance(x1, y1, x2, y2, cosA, sinA);

        // The values in (d < -30) and (d > 30) sets a smaller range
        // to turn towards and reduces the amount of rotations needed.
        if(d < -30)rotateImage(gc, -1 * rotateSpeed);
        if(d > 30) rotateImage(gc,  1 * rotateSpeed);
    }

    // IMAGE
    // Get this Sprite's Image Width
    public int getWidth() { return this.image.getWidth(null); }

    // Get this Sprite's Image Height
    public int getHeight() { return this.image.getHeight(null); }

    // Set Body Image
   public void setImage(Image image){ this.originalImage  = image; }

    // Rotate this Sprite's Image
    public void rotateImage(GraphicsConfiguration gc, int degree) {

        // Rotate changes the first image (main body) of this sprite
        // Calculate the new Angle Change
        angle = Calculate.angle(angle, degree);

        // Lookup the new Angle
        cosA = Lookup.cos[angle];
        sinA = Lookup.sin[angle];

        // Set Sprite CosA and SinA
        super.setCosA(cosA);
        super.setSinA(sinA);

        // If the Angle is 0 then just copy the original image
        if(angle == 0) image = originalImage;
        else
        {
            // Any angle other than 0 requires a new image to be generated by
            // Calculating the rotation transform from the original image.
            image = getRotatedImage(gc, originalImage, angle);
        }
    }

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

    // Reset Image
    public void resetImage(GraphicsConfiguration gc, int degree) {

        // Reset Angle to 0
        angle = 0;

        // Lookup the new Angle
        cosA = Lookup.cos[angle];
        sinA = Lookup.sin[angle];

        // Set Sprite CosA and SinA
        super.setCosA(cosA);
        super.setSinA(sinA);

        // Rotate Image to New Angle
        rotateImage(gc, degree);
    }


    // PROJECTILES
    // Is in Range
    public boolean isInRange(float x1, float y1, float x2, float y2, int r1, int r2) {

        // Calculate Angle between Sprite's
        double d = Calculate.turnDistance(x1, y1, x2, y2, cosA, sinA);

        // If the Sprite is Aimed in a range at the other Sprite then Check Distance
        if(d >= -45 && d < 45) {

            d = Calculate.distanceTo(x1, y1, x2, y2);

            // If the distance is in range then return true
            return d < (r1 + r2) * (r1 + r2);
        }

        // Not in Range Return False
        return false;
    }

    // Decrease Bullet Ammunition
    public void decreaseBulletAmmo() {

        bulletAmmo -= 2;

        if(bulletAmmo <= 0) {

            bulletAmmo = 0;
            bulletAmmoIsEmpty = true;
        }

        // This will cancel the current task. If there is no active task, nothing happens.
        this.refillBulletAmmo.cancel();
        this.refillBulletAmmo = new Timer();

        // After timer ends Refill Ammunition
        TimerTask action = new TimerTask() { public void run() { refillBulletAmmo(); } };

        // Start Timer with Delay amount
        this.refillBulletAmmo.schedule(action, bulletReloadTime);
    }

    // Bullet Ammunition is Empty
    public boolean bulletAmmoIsEmpty() { return bulletAmmoIsEmpty; }

    // Refill Bullet Ammunition
    private void refillBulletAmmo() {

        bulletAmmo = 2;
        bulletAmmoIsEmpty = false;
    }

    // Decrease Rocket Ammunition
    public void decreaseRocketAmmo() {

        rocketAmmo -= 2;

        if(rocketAmmo <= 0) {

            rocketAmmo = 0;
            rocketAmmoIsEmpty = true;
        }

        // This will cancel the current task. If there is no active task, nothing happens.
        this.refillRocketAmmo.cancel();
        this.refillRocketAmmo = new Timer();

        // After timer ends Refill Ammunition
        TimerTask action = new TimerTask() { public void run() { refillRocketAmmo(); } };

        // Start Timer with Delay amount
        this.refillRocketAmmo.schedule(action, rocketReloadTime);
    }

    // Rocket Ammunition is Empty
    public boolean rocketAmmoIsEmpty() { return rocketAmmoIsEmpty; }

    // Refill Rocket Ammunition
    private void refillRocketAmmo() {

        rocketAmmo = 2;
        rocketAmmoIsEmpty = false;
    }

    // Set Bullet Reload Time
    public void setBulletReloadTime(int time) { this.bulletReloadTime = time; }

    // Set Bullet Reload Time
    public void setRocketReloadTime(int time) { this.rocketReloadTime = time; }

    // Decrease Rockets
    public void decreaseRockets() { this.rockets -= 1; }

    // Get Number of Rockets
    public int getNumRockets() { return rockets; }

    // Set Rockets
    public void setRockets(int x) { this.rockets = x; }

} // End of Class.