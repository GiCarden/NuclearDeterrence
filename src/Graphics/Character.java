package Graphics;

import java.awt.*;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Code modified from David Brackeen
 *  Copyright (c) 2003, David Brackeen
 *
 *  A Character is a Sprite that can die. It has one Animation: dying
 */
public abstract class Character extends Sprite {

    // Amount of time to go from STATE_DYING to STATE_DEAD.
    private static final int DIE_TIME = 3000;
    public  static final int STATE_NORMAL = 0;
    public  static final int STATE_DYING = 1;
    public  static final int STATE_DEAD = 2;

    private Animation normal;
    private Animation dying;

    // State of the Character
    private int state;
    private long stateTime;

    // Creates a new character with the specified Animations
    public Character(String name, Animation anim, Animation dying, Object collision,
                     int offset, int width, int height, int type) {

        super(name, anim, collision, offset, width, height, type);
        this.normal = anim;
        this.dying = dying;
        state = STATE_NORMAL;
    }

    // Gets the state of this character. The state is either STATE_NORMAL, STATE_DYING, or STATE_DEAD
    public int getState() { return state; }

    // Sets the state of this character to STATE_NORMAL, STATE_DYING, or STATE_DEAD
    public void setState(int state) {

        if(this.state != state) {

            this.state = state;
            stateTime  = 0;
        }
    }

    // Checks if this character is alive
    public boolean isAlive() { return state == STATE_NORMAL; }

    // Character is Dying
    public boolean isDying() { return state == STATE_DYING; }

    // Get Animation Image
    public Image getAnimImage() { return getAnim().getImage();  }

    // Set Animation
    public void setAnim(Animation anim) { this.anim = anim; }

    // Reset Animation to Normal
    public void resetAnim() { setAnim(normal); }

    // Updates the animation for this character
    public void update(long elapsedTime) {

        // Select the correct Animation
        Animation newAnim = getAnim();

        // Set Animation to Dying
        if(state == STATE_DYING) newAnim = dying;

        // Blank Animation stops any other animation from updating in the background.
        if(state == STATE_DEAD) newAnim = null;

        // Update the Animation
        if(getAnim() != newAnim) {

            setAnim(newAnim);

            if(getAnim() != null) getAnim().start();
        }
        else { if(getAnim() != null) getAnim().update(elapsedTime); }

        // Update to "dead" state
        stateTime += elapsedTime;

        if(state == STATE_DYING && stateTime >= DIE_TIME) { setState(STATE_DEAD); }
    }

} // End of Class.