package Graphics.Sprites;

import Game.MainGameState;
import Graphics.Sprite;
import Collision.Collision;
import Game.GameManager;
import Graphics.Animation;
import Graphics.Shapes.Circle;
import Sound.Sound;
import Sound.SoundManager;
import Util.CountDown;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class Silo {

    // Amount of time to go from STATE_DYING to STATE_DEAD.
    private static final int DIE_TIME = 5000;
    public  static final int STATE_NORMAL = 0;
    public  static final int STATE_DYING = 1;
    public  static final int STATE_DEAD = 2;
    public  static final int STATE_LAUNCHING = 3;

    // Location
    private float x;
    private float y;
    private int launch_Y;

    // State of the Silo
    private int state;
    private long stateTime;
    private int health;

    // Images
    private Image openDoorsImg;
    private Image explodedImg;
    private int width;
    private int height;

    // Animations
    private Animation anim;
    private Animation launch;
    private Animation explosion;
    private Animation launchExp;
    private Animation openDoors;

    // Collision
    private Circle collision;

    private boolean isOnScreen;
    private boolean launched;

    // Count Down
    private Timer sequenceTimer;
    private boolean counting;
    private boolean countComplete;
    private Font font;
    private SimpleDateFormat df;
    private CountDown countDown;

    // Silo
    public Silo(Image[] images, Image[] launch, Image[] explosion, Image[] launchExp, Image[] openDoors) {

        this.openDoorsImg = images[0];
        this.explodedImg = images[1];
        this.width = openDoorsImg.getWidth(null);
        this.height = openDoorsImg.getHeight(null);
        this.launch = createAnim(launch);
        this.explosion = createDyingAnim(explosion);
        this.launchExp = createAnim(launchExp);
        this.openDoors = createDoorsAnim(openDoors);
        this.anim = this.openDoors;
        this.launch_Y = 0;
        this.health = 1000;
        this.launched = false;
        this.isOnScreen = false;
        this.font = new Font("Stencil STD", Font.PLAIN, 40);
        this.df = new SimpleDateFormat("mm:ss:SSS");
        this.countDown = new CountDown(85000);
        this.counting = false;
        this.countComplete = false;
        this.sequenceTimer = new Timer();
        this.collision = new Circle(0, 0, 30);
        this.state = STATE_NORMAL;
    }

    // Update
    public void update(long elapsedTime, Sprite sprite, SoundManager soundManager, Sound launchingSFX, Sound dyingSFX){

        // Select the correct Animation
        Animation newAnim = getAnim();

        // Update Count Down
        if(counting && countDown.getStartTime() < 0) countDown.setStartTime(); else if(counting) countDown.update();

        // Set Animation
        if(state != STATE_DYING && state != STATE_DEAD) {

            if(counting && countDown.countComplete() && state != STATE_LAUNCHING) {

                setState(STATE_LAUNCHING);

                launch.start();

                soundManager.play(launchingSFX);

                GameManager.shake.shake(25);

                // Check Collision with Silo Rocket
                if(!MainGameState.godMode_Enabled) {

                    Helicopter player = (Helicopter)sprite;
                    Collision.isCollision(player, this, soundManager, dyingSFX);
                }
            }
        }

        if(state == STATE_LAUNCHING) newAnim = launchExp;

        if(state == STATE_DYING)     newAnim = explosion;

        // Update the Animation
        if(anim != newAnim) {

            anim = newAnim;
            if(anim != null) anim.start();
            if(state == STATE_DYING) soundManager.play(dyingSFX);
        } else {

            if(anim != null) anim.update(elapsedTime);
        }

        // Update Launch Animation
        if(launch != null && state == STATE_LAUNCHING) {

            launch.update(elapsedTime);
            if(launch.getCurrentFrame() == 12) launch_Y += 4;
            launched = true;
        }

        // Update to "dead" state
        stateTime += elapsedTime;
        if((state == STATE_DYING || launched) && (state != STATE_DEAD) && (stateTime >= DIE_TIME)) setState(STATE_DEAD);
    }

    // Draw
    public void draw(Graphics2D g, int offsetX, int offsetY, boolean colBoundVisible) {

        // Adjust Position with Map Offset
        int siloX = Math.round(getX()) + offsetX;
        int SiloY = Math.round(getY()) + offsetY;

        // Draw Silo Plus Count Down and the Rocket
        if(isAlive()) {

            // Draw Static Silo Image or Animation
            if(state == STATE_NORMAL) {

                if(counting) g.drawImage(openDoorsImg, siloX, SiloY, null);
                else if(anim != null) g.drawImage(anim.getImage(), siloX, SiloY, null);
            }

            // Draw if the Rocket is Launching
            if(state == STATE_LAUNCHING) {

                g.drawImage(openDoorsImg, siloX, SiloY, null);

                if(anim != null) g.drawImage(anim.getImage(),siloX - 92,SiloY - 115,null);
                if(launch != null) g.drawImage(launch.getImage(), siloX,SiloY - launch_Y, null);
            }
        }

        // Draw Dying Silo
        if(isDying()) {

            g.drawImage(explodedImg, siloX, SiloY,null);
            if(anim != null) g.drawImage(anim.getImage(), siloX - 158, SiloY - 115, null);
        }

        // Draw Dead Silo
        if(isDead()) {

            // If rocket was launched then draw empty silo
            if(launched) { if(launch != null) g.drawImage(anim.getImage(), siloX - 92, SiloY - 115, null); }
            else {

                // Else draw exploded silo
                g.drawImage(explodedImg, siloX, SiloY, null);
            }
        }

        // Draw Collision Model
        if(colBoundVisible) collision.draw(g,
                siloX + (openDoorsImg.getWidth(null)/2) -1,
                SiloY + (openDoorsImg.getHeight(null)/2) + 32);
    }

    // Draw
    public void drawCountDown(Graphics2D g, int screenWidth) {

        // Draw CountDown
        if(counting) {

            g.setFont(font);
            g.setColor(Color.WHITE);
            g.drawString(df.format(countDown.getDuration() - countDown.getClockTime()), screenWidth - 220, 40);
        }
    }

    // Init Sequence
    public void initSequence() {

        // Delay initializing count down sequence
        sequenceTimer.cancel();

        sequenceTimer = new Timer();

        // After timer ends set new paragraph
        TimerTask action = new TimerTask() {

            public void run() {

                anim     = null;
                counting = true;
            }
        };

        // Start Timer with Delay amount
        sequenceTimer.schedule(action, 3200);
    }

    // Gets the state of this character. The state is either STATE_NORMAL, STATE_DYING, or STATE_DEAD
    public int getState() { return state; }

    // Sets the state of the silo to STATE_NORMAL, STATE_DYING, or STATE_DEAD
    public void setState(int state) {

        if(this.state != state) {

            this.state = state;
            stateTime  = 0;
        }
    }

    // Checks if this Silo is alive
    public boolean isAlive() { return state == STATE_NORMAL || state == STATE_LAUNCHING; }

    // Silo is Dying
    public boolean isDying() { return state == STATE_DYING; }

    // Silo is Launching
    public boolean isLaunching() { return state == STATE_LAUNCHING; }

    // Checks if this Silo is dead
    public boolean isDead() { return state == STATE_DEAD; }

    // Get Animation
    public Animation getAnim() { return this.anim; }

    // Reset Animation to Normal
    public void reset() {

        this.launch_Y = 0;
        this.health = 1000;
        this.countDown = new CountDown(85000);
        this.launched = false;
        this.isOnScreen = false;
        this.counting = false;
        this.countComplete = false;
        this.state = STATE_NORMAL;
        this.anim = openDoors;
        this.anim.start();
    }

    // Create Doors Animation
    private Animation createDoorsAnim(Image[] images) {

        Animation anim = new Animation();

        anim.addFrame(images[0], 2000);

        for(int i = 1; i < images.length - 1; i++) anim.addFrame(images[i], 60);

        anim.addFrame(images[24], 1000);

        return anim;
    }

    // Create Animation
    private Animation createAnim(Image[] images) {

        Animation anim = new Animation();

        for(int i = 0; i < images.length - 1; i++) anim.addFrame(images[i], 60);

        anim.addFrame(images[images.length-1], 10000);

        return anim;
    }

    // Create Animation
    private Animation createDyingAnim(Image[] images) {

        Animation anim = new Animation();

        for(int i = 0; i < images.length - 1; i++) anim.addFrame(images[i], 100);

        anim.addFrame(images[images.length-1], 10000);

        return anim;
    }

    // Set X
    public void setX(int x) { this.x = x; }

    // Set Y
    public void setY(int y) { this.y = y; }

    // Get X
    public float getX() { return this.x; }

    // Get R
    public int getR() { return this.collision.getR(); }

    // Get Y
    public float getY() { return this.y; }

    // Get Is On Screen
    public boolean isOnScreen() { return this.isOnScreen; }

    // Set Is On Screen
    public void setIsOnScreen(boolean state) { this.isOnScreen = state; }

    // Is Counting
    public boolean isCounting() { return counting; }

    // Get Launched
    public boolean hasLaunched() { return this.launched; }

    // Get Height
    public int getHeight() { return height; }

    // Get Width
    public int getWidth() { return width; }

    // Get Health
    public int getHealth() { return this.health; }

    // Decrease Health
    public void decreaseHealth(int x) {

        this.health -= x;

        if(this.health <= 0 && state != STATE_DYING) {

            setState(STATE_DYING);
            GameManager.shake.shake(50);
        }
    }

} // End of Class.
