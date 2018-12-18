package Game;

import java.awt.*;
import Graphics.Animation;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import Graphics.Transitions.FadeRect;
import Input.*;
import Sound.Sound;
import Sound.SoundManager;
import State.GameState;
import State.ResourceManager;
import javax.sound.sampled.AudioFormat;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Create a splash animation using an image sequence and timers.
 *  There is a 2 second delay timer before the sequence starts to ensure all resources are loaded.
 *
 *  Then the audio starts and sets a second delay timer to initialize the drawing of the animation.
 *  When the animation begins there is a fade transition using a filled white rectangle to simulate an explosion flash.
 *
 *  A third timer is used to end the animation drawing by setting the animation to null.
 *  This allows drawing a blank frame at the end for a set amount of time to allow a smooth transition between states.
 */
public class SplashGameState implements GameState {

    private Animation anim;
    private Animation nukeAnim;
    private int imageWidth;
    private int imageHeight;
    private GameAction exit;
    private static Timer timer;
    private static Timer startAnimTimer;
    private static Timer endAnimTimer;
    private static Timer endSplashTimer;
    private static boolean animTimerEnabled;
    private static boolean playSplash;
    private static boolean endSplash;
    private FadeRect fadeTransition;
    private static boolean fadeComplete;
    private long totalElapsedTime;
    private boolean done;
    private Sound sound;
    private boolean soundIsPlaying;
    private SoundManager soundManager;

    // Splash Game State
    public SplashGameState(AudioFormat PLAYBACK_FORMAT) {

        exit = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
        this.anim = null;
        this.soundIsPlaying = false;

        this.playSplash = false;
        this.timer = new Timer();
        this.startAnimTimer = new Timer();
        this.endAnimTimer = new Timer();
        this.endSplashTimer = new Timer();
        this.animTimerEnabled = false;
        this.fadeComplete = false;
        this.endSplash = false;
        this.fadeTransition = new FadeRect(0, 0, GameManager.getScreenWidth(),
                                                   GameManager.getScreenHeight(), 1f, Color.WHITE);
        this.soundManager = new SoundManager(PLAYBACK_FORMAT);
    }

    // Start
    public void start(InputManager inputManager) {

        // Set Buttons to Skip Splash Screen
        inputManager.mapToKey  (exit, KeyEvent.VK_SPACE);
        inputManager.mapToKey  (exit, KeyEvent.VK_ENTER);
        inputManager.mapToKey  (exit, KeyEvent.VK_ESCAPE);

        // Set a Mouse Button to Skip Splash Screen
        inputManager.mapToMouse(exit, InputManager.MOUSE_BUTTON_1);

        totalElapsedTime = 0;
        done = false;

        // Set a timer to delay the start to make sure resources are loaded
        timer.cancel();
        timer = new Timer();
        TimerTask action = new TimerTask() { public void run() { playSplash = true; } };
        timer.schedule(action, 2000);

        // Set a timer to end the splash state if user does not press a key
        endSplashTimer.cancel();
        endSplashTimer = new Timer();
        TimerTask ensSplashAction = new TimerTask() { public void run() { endSplash = true; } };
        endSplashTimer.schedule(ensSplashAction, 20000);
    }

    // Update
    public void update(long elapsedTime) {

        // If playing splash animation then update animation
        if(playSplash) playSplash(elapsedTime);

        // If time has reached end of Animation then Stop
        if(endSplash || exit.isPressed()) {

            soundManager.setPaused(true);
            done = true;
            soundIsPlaying = false;

            // Clear objects from memory
            anim = null;
            nukeAnim = null;
        }
    }

    // Draw
    public void draw(Graphics2D g, int screenWidth, int screenHeight) {

        // Draw Black over the Screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // If Animation is not Empty then Draw
        if(anim != null) {

            // Draw the animation
            g.drawImage(anim.getImage(), (screenWidth/2)-(imageWidth/2), (screenHeight)-(imageHeight), null);

            // Draw the Flash
            if(!fadeComplete) fadeTransition.draw(g);
        }
    }

    // Play Splash
    private void playSplash(long elapsedTime) {

        // If playing splash animation then update animation, else wait for start delay to end.
        if(playSplash) {

            // If Sound is not Playing then Play
            if(!soundIsPlaying) {

                soundIsPlaying = true;
                soundManager.play(sound);
            }

            // Create a Flash for the Explosion with a white rectangle fade transition
            if(!animTimerEnabled) {

                animTimerEnabled = true;

                // Set a timer to delay the start of the animation
                startAnimTimer.cancel();
                startAnimTimer = new Timer();

                // After timer ends set animation
                TimerTask animAction = new TimerTask() { public void run() { anim = nukeAnim; } };

                // Start Timer with delay amount to flash on cue with the explosion in the audio
                startAnimTimer.schedule(animAction, 10500);

                // Create a timer to null the animation and draw a black frame as a pause at the end.
                // Set a timer to delay the end of the animation
                endAnimTimer.cancel();
                endAnimTimer = new Timer();

                // After timer ends set animation to null
                TimerTask endAnimAction = new TimerTask() {public void run() { anim = null; }};

                // Start Timer with Delay amount
                endAnimTimer.schedule(endAnimAction, 15450);
            } else if(anim != null) {

                fadeTransition.update(-0.008f);
                anim.update(elapsedTime);
            }
        }

        // After the flash has faded out then set to complete to avoid drawing
        if(fadeTransition.getAlpha() == 0) fadeComplete = true;
    }

    // Load Resources
    public void loadResources(ResourceManager resourceManager, int screenWidth, int screenHeight) {

        // Load Animation Images
        Image[] images = new Image[122];

        for(int i = 0; i < 122; i++) {

            String location = "Splash/Splash_Screen_" + (i) + ".png";
            images[i] = resourceManager.loadImage(location);
        }

        this.imageWidth = images[0].getWidth(null);
        this.imageHeight= images[0].getHeight(null);

        // Create Animation
        nukeAnim = createAnim(images);

        // Load Sound FX
        String sound = "Music/Splash.wav";
        this.sound = resourceManager.loadSound(sound);
    }

    // Create Animation
    private Animation createAnim(Image[] images) {

        /*
            Create the animations by placing images
            in a sequence followed by a time each image is
            displayed. -addFrame(image, time);
         */

        Animation anim = new Animation();
        for (int i = 0; i < images.length; i++) anim.addFrame(images[i], 60);
        return anim;
    }

    // Check for State Change
    public String checkForStateChange() { return done?"Intro":null; }

    // Get Name
    public String getName() { return "Splash"; }

    // Stop
    public void stop() { soundManager.close(); }

    // Close Sound Managers
    public void closeSoundManagers() { }

} // End of Class.