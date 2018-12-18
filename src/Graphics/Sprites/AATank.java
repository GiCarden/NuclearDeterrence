package Graphics.Sprites;

import Math.Calculate;
import Graphics.Character;
import Graphics.Shapes.Circle;
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
 */
public class AATank  extends Character {

    // Images
    private final Image fwImg;
    private final Image bwImg;
    private final Image stoppedImg;

    private Image bodyImage;
    private Image turretImage;
    private Image originalBodyImg;
    private Image originalTurretImage;

    // Angle of Body Image
    private int bodyAngle;
    private double bodyCosA;
    private double bodySinA;

    // Angle of Turret
    private int turretAngle;
    private double turretCosA;
    private double turretSinA;

    // Turret Rotating determines when to play the SFX
    private boolean turretRotating;

    // Rockets
    private int rocketReloadTime;
    private int rocketAmmo;
    private boolean rocketAmmoIsEmpty;
    private Timer refillRocketAmmo;

    // Turret Rotate Speed affects rocket Images
    private static final int turretRotateSpeed = 5;

    // Helicopter Images
    public AATank(String name, Image[] images, Animation dying, Object collision, int type, int offset) {

        super(name, null, dying, collision, offset, images[1].getWidth(null),
                images[1].getHeight(null), type);

        setAnim(null);

        this.stoppedImg = images[1];
        this.fwImg = images[2];
        this.bwImg = images[3];
        this.originalBodyImg = images[1];
        this.bodyImage = images[1];
        this.originalTurretImage = images[0];
        this.turretImage = images[0];

        this.bodyAngle = 0;
        this.turretAngle = 0;
        bodyCosA = Lookup.cos[bodyAngle];
        bodySinA = Lookup.sin[bodyAngle];
        turretCosA = Lookup.cos[turretAngle];
        turretSinA = Lookup.sin[turretAngle];

        this.turretRotating = false;

        // Set Cos and Sin for Sprite Class
        super.setCosA(bodyCosA);
        super.setSinA(bodySinA);

        this.rocketAmmo = 2;
        this.rocketAmmoIsEmpty = false;
        this.rocketReloadTime = 1000;
        this.refillRocketAmmo = new Timer();
    }

    // Update
    public void update(long elapsedTime) { super.update(elapsedTime); }

    // Draw
    public void draw(GraphicsConfiguration gc, Graphics2D g, int offsetX, int offsetY, boolean colBoundVisible) {

        // DRAW SPRITE
        // Adjust Sprite Position with Map Offset
        int spriteX = Math.round(getX()) + offsetX;
        int SpriteY = Math.round(getY()) + offsetY;

        // Draw Static Images if Alive
        if(super.isAlive()) {

            // Draw Body Image
            g.drawImage(bodyImage, spriteX, SpriteY, null);

            // Draw Rotor Image
            g.drawImage(turretImage, spriteX, SpriteY, null);
        }

        // Draw Animation if Dying!
        if(super.isDying() && (super.anim != null)) {

            // Rotate animation image
            Image newAnim = getRotatedImage(gc, super.getAnimImage(), bodyAngle);

            // Draw Death Animation
            g.drawImage(newAnim, spriteX, SpriteY, null);
        }

        // DRAW COLLISION MODEL
        // Draw Collision Boundary if Visible
        if(colBoundVisible) {

            // Get Collision Model
            Circle collision = (Circle) getCollisionPoly();

            // Set the X/Y of Circle 1
            collision.setX(spriteX + getWidth()/2);
            collision.setY(SpriteY + getHeight()/2 + getOffset());

            // Draw the Circles
            collision.draw(g, bodyCosA, bodySinA);
        }
    }


    // MOVEMENT
    // Get Angle
    public int getTurretAngle() { return this.turretAngle; }

    // Get Body CosA
    private double getBodyCosA() { return this.bodyCosA; }

    // Get Body SinA
    private double getBodySinA() { return this.bodySinA; }

    // Get Turret CosA
    public double getTurretCosA() { return this.turretCosA; }

    // Get Turret SinA
    public double getTurretSinA() { return this.turretSinA; }

    // Stopped
    public void stopped(GraphicsConfiguration gc) {

        // Change to Hover Image
        setBodyImage(stoppedImg);

        // Rotate to Sprites Main Angle
        rotateBody(gc, 0);
    }

    // Move Forward //
    public void moveForward(GraphicsConfiguration gc) {

        // Change to Forward Image
        setBodyImage(fwImg);

        // Rotate to Sprites Main Angle
        rotateBody(gc, 0);

        super.moveForward();
    }

    // Move Backward
    public void moveBackward(GraphicsConfiguration gc) {

        // Change to Forward Image
        setBodyImage(bwImg);

        // Rotate to Sprites Main Angle
        rotateBody(gc, 0);

        super.moveBackward();
    }

    // Rotate Main Image
    public void rotate(GraphicsConfiguration gc, int direction) { rotateBody(gc, 5 * direction); }

    // Turn Towards Sprite
    public void turnTowards(GraphicsConfiguration gc, float x1, float y1, float x2, float y2) {

        // Rotate this Sprite towards another Sprite
        double d = (x2 - x1) * bodySinA - (y2 - y1) * bodyCosA;

        // The values in (d < -30) and (d > 30) sets a smaller range
        // to turn towards and reduces the amount of rotations needed.
        if(d < -30) rotateBody(gc, -5);
        if(d > 30)  rotateBody(gc, 5);
    }

    // Turn Turret Towards
    public void turnTurretTowards(GraphicsConfiguration gc, float x1, float y1, float x2, float y2) {

        // Rotate this Sprite's Turret towards another Sprite
        double d = (x2 - x1) * turretSinA - (y2 - y1) * turretCosA;

        // The values in (d < -30) and (d > 30) sets a smaller range
        // to turn towards and reduces the amount of rotations needed.
        if(d < -30)     rotateTurret(gc, -turretRotateSpeed);
        else if(d > 30) rotateTurret(gc, turretRotateSpeed);

        // Turn on SFX only for certain range
        if(d < -75)     turretRotating = true;
        else if(d > 75) turretRotating = true;
        else turretRotating = false;
    }

    // Get Turret Rotating
    public boolean isTurretRotating() { return this.turretRotating; }


    // IMAGE
    // Get this Sprite's Image Width
    public int getWidth() { return this.bodyImage.getWidth(null); }

    // Get this Sprite's Image Height
    public int getHeight() { return this.bodyImage.getHeight(null); }

    // Set Body Image
    public void setBodyImage(Image image) {

        this.bodyImage       = image;
        this.originalBodyImg = image;
    }

    // Rotate this Sprite's Primary Image
    public void rotateBody(GraphicsConfiguration gc, int degree) {

        // Rotate changes the first image (main body) of this sprite
        // Calculate the new Angle Change
        bodyAngle += degree;

        // Adjust the Angle between 0 and 360
        if(bodyAngle >= 360) bodyAngle += -360;
        if(bodyAngle <    0) bodyAngle +=  360;

        // Lookup the new Angle
        bodyCosA = Lookup.cos[bodyAngle];
        bodySinA = Lookup.sin[bodyAngle];

        // Set Sprite CosA and SinA
        super.setCosA(bodyCosA);
        super.setSinA(bodySinA);

        // If the Angle is 0 then just copy the original image
        if(bodyAngle == 0) bodyImage = originalBodyImg;
        else {

            // Any angle other than 0 requires a new image to be generated by
            // Calculating the rotation transform from the original image.
            bodyImage = getRotatedImage(gc, originalBodyImg, bodyAngle);
        }
    }

    // Rotate this Sprite's Secondary Image
    public void rotateTurret(GraphicsConfiguration gc, int degree) {

        // Secondary Rotate changes the second image attached to the sprite
        // Calculate the new Angle Change
        turretAngle += degree;

        if(turretAngle >= 360) turretAngle -= 360;
        if(turretAngle < 0)    turretAngle += 360;

        // Lookup the new Angle
        turretCosA = Lookup.cos[turretAngle];
        turretSinA = Lookup.sin[turretAngle];

        // If the Angle is 0 then just copy the original image
        if(turretAngle == 0) turretImage = originalTurretImage;
        else {

            // Any angle other than 0 requires a new image to be generated by
            // Calculating the rotation transform from the original image.
            turretImage = getRotatedImage(gc, originalTurretImage, turretAngle);
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


    // PROJECTILES
    // Is in Range
    public boolean isInRange(float x1, float y1, float x2, float y2, int r1, int r2) {

        // Calculate Angle between Sprite's
        double d = Calculate.turnDistance(x1, y1, x2, y2, super.getCosA(), super.getSinA());

        // If the Sprite is Aimed in a range at the other Sprite then Check Distance
        if(d >= -400 && d < 400) {

            d = Calculate.distanceTo(x1, y1, x2, y2);

            // If the distance is in range then return true
            return d < (r1 + r2) * (r1 + r2);
        }

        // Not in Range Return False
        return false;
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
    public void setRocketReloadTime(int time) { this.rocketReloadTime = time; }

} // End of Class.
