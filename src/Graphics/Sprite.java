package Graphics;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Sprites store an X/Y Location and can have velocity. There is also an animation for dying.
 *
 *  Sprites also have an Object that contains a Collision Model.
 */
public abstract class Sprite {

    // Position
    private float x;
    private float y;

    // Velocity
    private float dx;
    private float dy;

    // Sprite Angle
    private double cosA;
    private double sinA;

    // Sprite Size
    private int width;
    private int height;

    // Animation
    public  Animation anim;
    private Animation orig_Anim;

    // Max Speed of Sprite
    private float maxSpeed;

    // Info
    private String  name;
    private boolean isOnScreen;
    private int     zone;

    // Keep track of Sprite's Health
    private int health;

    // Hold a Collision Object that can be a Circles or a Polygon
    // and hold an offset value for placement.
    private Object collisionPoly;
    private int    offset;

    // Type of Sprite
    private final int type;

    // Sprite
    public Sprite(String name, Animation anim, Object collision, int offset, int width, int height, int type) {

        this.anim = anim;
        this.orig_Anim = anim;
        this.width = width;
        this.height = height;
        this.maxSpeed = 0.3f;
        this.name = name;
        this.isOnScreen = false;
        this.health = 100;
        this.collisionPoly = collision;
        this.offset = offset;
        this.type = type;
    }

    // Update this Sprite
    public void update(long elapsedTime) {

        // Updates this Sprite's Animation and its position based on the velocity.
        x += dx * elapsedTime;

        y += dy * elapsedTime;

        // Update Animation
        if(anim != null) anim.update(elapsedTime);
    }

    // POSITION
    // Gets this Sprite's current X position
    public float getX() { return x; }

    // Gets this Sprite's current Y position
    public float getY() { return y; }

    // Sets this Sprite's current X position
    public void setX(float x) { this.x = x; }

    // Sets this Sprite's current Y position
    public void setY(float y) { this.y = y; }

    // Set this Sprite's Velocity X
    public void setVelocityX(float dx) { this.dx = dx; }

    // Set this Sprite's Velocity Y
    public void setVelocityY(float dy) { this.dy = dy; }

    // Get this Sprite's Velocity X
    public float getVelocityX() { return dx; }

    // Get this Sprite's Velocity Y
    public float getVelocityY() { return dy; }

    // Set CosA
    public void setCosA(double cosA) { this.cosA = cosA; }

    // Set SinA
    public void setSinA(double sinA) { this.sinA = sinA; }

    // Get CosA
    public double getCosA() { return this.cosA; }

    // Get SinA
    public double getSinA() { return this.sinA; }

    // Get this Sprite's Max Speed
    public float getMaxSpeed() { return maxSpeed; }

    // Set this Sprite's Max Speed
    public void setMaxSpeed(float speed) { this.maxSpeed = speed; }

    // Move this Sprite Forward
    public void moveForward() {

        dx = (float)(getMaxSpeed() * cosA);

        dy = (float)(getMaxSpeed() * sinA);
    }

    // Move this Sprite Backward
    public void moveBackward() {

        dx = (float)(-1 * getMaxSpeed() * cosA);

        dy = (float)(-1 * getMaxSpeed() * sinA);
    }

    // Move this Sprite Forward
    public void moveForward(double cosA, double sinA) {

        dx = (float)(getMaxSpeed() * cosA);

        dy = (float)(getMaxSpeed() * sinA);
    }

    // Stop Sprite's Movement
    public void stopMovement() {

        setVelocityX(0);

        setVelocityY(0);
    }

    // ANIMATION
    // Set Animation
    public void setAnim(Animation anim) { this.anim = anim; }

    // Get Animation
    public Animation getAnim() { return this.anim; }

    // COLLISION
    // Get Collision Object
    public Object getCollisionPoly() { return collisionPoly; }

    // Get Offset
    public int getOffset() { return offset; }

    // INFO
    // Get Name
    public String getName() { return this.name; }

    // Is Enemy
    public boolean isEnemy() { return this.name.equals("enemy"); }

    // Is Player
    public boolean isPlayer() { return this.name.equals("player"); }

    // Sprite's Are Allies
    public boolean isAlly(Sprite sprite) { return this.name.equals(sprite.name); }

    // Is on Screen
    public boolean isOnScreen() { return this.isOnScreen; }

    // Set Is on Screen
    public void setIsOnScreen(boolean onScreen) { this.isOnScreen = onScreen; }

    // Decrease Health
    public void decreaseHealth(int health) { this.health -= health; }

    // Increase Health
    public void increaseHealth(int health) { this.health += health; }

    // Get Health
    public int getHealth() { return this.health; }

    // Set Health
    public void setHealth(int health) { this.health = health; }

    // Get this Sprite's Image Width
    public int getWidth() { return this.width; }

    // Get this Sprite's Image Height
    public int getHeight() { return this.height; }

    // Get Type
    public int getType() { return this.type; }

    // Get Zone
    public int getZone() { return this.zone; }

    // Set Zone
    public void setZone(int x) { this.zone = x; }

} // End of Class