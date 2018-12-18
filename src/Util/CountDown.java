package Util;

/**
 *  Code created by: Brett Bearden, and Giovanni Cardenas.
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class CountDown {

    private long  startTime;
    private long  duration;
    private long  clockTime;

    public CountDown(long duration) {

        this.duration  = duration;

        this.startTime = -1;
    }


    public void update() {

        long now  = System.currentTimeMillis();

        clockTime = now - startTime;

        if(clockTime >= duration) clockTime = duration;
    }

    public void setStartTime() { this.startTime = System.currentTimeMillis(); }

    public long getStartTime(){ return this.startTime; }

    public long getClockTime(){ return this.clockTime; }

    public long getDuration(){ return this.duration; }

    public boolean countComplete() {  return this.clockTime >= this.duration; }

} //End of Class.
