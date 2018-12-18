package Graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
 *     Code created by David Brackeen
 *     Copyright (c) 2003, David Brackeen
 *
 *     The Animation class manages a series of images (frames)
 *     and the amount of time to display each frame.
 */
public class Animation {

    public ArrayList frames;
    private int currFrameIndex;
    private long animTime;
    private long totalDuration;

    // Create a new, empty Animation
    public Animation() { this(new ArrayList(), 0); }

    // Animation //
    private Animation(ArrayList frames, long totalDuration) {

        this.frames = frames;
        this.totalDuration = totalDuration;
        start();
    }

    // Adds an image to the animation with the specified duration (time to display the image)
    public synchronized void addFrame(Image image, long duration) {

        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }

    // Starts this animation over from the beginning
    public synchronized void start() {

        animTime = 0;
        currFrameIndex = 0;
    }

    // Updates this animation's current image (frame), if necessary
    public synchronized void update(long elapsedTime) {

        if(frames.size() > 1) {

            animTime += elapsedTime;

            if(animTime >= totalDuration) {

                animTime = animTime % totalDuration;
                currFrameIndex = 0;
            }

            while(animTime > getFrame(currFrameIndex).endTime) { currFrameIndex++; }
        }
    }

    // Gets this Animation's current image. Returns null if this animation has no images
    public synchronized Image getImage() {

        if(frames.size() == 0) { return null; } else { return getFrame(currFrameIndex).image; }
    }

    // Gets this Animation's current image. Returns null if this animation has no images
    public synchronized Image getImage(int index) {

        if(frames.size() == 0) { return null; } else { return getFrame(index).image; }
    }

    // Get Current Frame Index
    public int getCurrentFrame() { return this.currFrameIndex; }

    // Get Frame //
    public AnimFrame getFrame(int i) { return (AnimFrame)frames.get(i); }

    // Private Anim Frame Class
    private class AnimFrame {

        Image image;
        long endTime;

        // Anim Frame //
        public AnimFrame(Image image, long endTime) {

            this.image = image;
            this.endTime = endTime;
        }
    } // End of Inner Class.

} // End of Class.