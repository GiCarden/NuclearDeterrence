package Graphics.Transforms;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 * Copyright (c) 2016, Nuclear Deterrence
 */
public class Shake {

    private static final int neg = 0;
    private static final int pos = 1;
    private int amount = 0;
    private int direction = pos;
    private int offset;
    private boolean shaking = false;

    // Shake
    public void shake(int amount) {

        if(amount > 0) {

            this.amount  = amount;
            this.shaking = true;
        }
    }

    // Update
    public void update() {

        if(directionEquals(pos)) {

            offset = amount;
            direction = neg;
        } else {

            offset = amount * -1;
            direction = pos;
        }

        if(amount > 0) amount --; else shaking = false;
    }

    // Direction Equals //
    private boolean directionEquals(int x) { return this.direction == x; }

    // Get Shaking //
    public boolean shaking() { return this.shaking; }

    // Get Offset //
    public int getOffset() { return this.offset; }

} // End of Class.
