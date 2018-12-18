package Game;

import Util.ScreenManager;
import java.awt.*;

/**
 *  Code created by David Brackeen
 *  Copyright (c) 2003, David Brackeen
 */
public abstract class GameCore {

    protected static final int FONT_SIZE = 24;

    // Possible Display Modes
    private static final DisplayMode POSSIBLE_MODES[] = {
            //new DisplayMode(1440, 900, 32, 0),
            //new DisplayMode(1440, 900, 24, 0),
            //new DisplayMode(1280, 800, 32, 0),
            //new DisplayMode(1280, 800, 24, 0),
            //new DisplayMode(1280, 720, 32, 0),
            //new DisplayMode(1280, 720, 24, 0),
            new DisplayMode(1024, 768, 32, 0),
            new DisplayMode(1024, 768, 24, 0),
            new DisplayMode( 800, 600, 32, 0),
            new DisplayMode( 800, 600, 24, 0)
    };

    private boolean isRunning;
    protected ScreenManager screen;

    // Signals the game loop that it's time to quit
    public void stop() { isRunning = false; }

    // Calls init() and gameLoop()
    public void run() {

        try {

            init();
            gameLoop();
        } finally {

            screen.restoreScreen();
            lazilyExit();
        }
    }

    // Lazily Exit
    public void lazilyExit() {

        /*
            Exits the VM from a daemon thread. The daemon thread waits
            2 seconds then calls System.exit(0). Since the VM should
            exit when only daemon threads are running, this makes sure
            System.exit(0) is only called if necessary. It's necessary
            if the Java Sound system is running.
         */

        Thread thread = new Thread(() -> {

            // first, wait for the VM exit on its own.
            try {

                Thread.sleep(2000);
            }
            catch (InterruptedException ex) { }
            // system is still running, so force an exit
            System.exit(0);
        });

        thread.setDaemon(true);
        thread.start();
    }

    // Sets full screen mode and initiates and objects
    public void init() {

        screen = new ScreenManager();

        DisplayMode displayMode = screen.findFirstCompatibleMode(POSSIBLE_MODES);

        screen.setFullScreen(displayMode);

        Window window = screen.getFullScreenWindow();
        window.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        window.setBackground(Color.blue);
        window.setForeground(Color.white);

        isRunning = true;
    }

    // Runs through the game loop until stop() is called
    public void gameLoop() {

        long startTime = System.currentTimeMillis();
        long currTime = startTime;

        while (isRunning) {

            long elapsedTime = System.currentTimeMillis() - currTime;

            currTime += elapsedTime;

            // Update
            update(elapsedTime);

            // Draw the screen
            Graphics2D g = screen.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            draw(g);
            g.dispose();
            screen.update();

            // don't take a nap! run as fast as possible
            /*try {
                Thread.sleep(20);
            }
            catch (InterruptedException ex) { }*/
        }
    }

    // Updates the state of the game/animation based on the amount of elapsed time that has passed
    public void update(long elapsedTime) { }

    // Draws to the screen. Subclasses must override this method
    public abstract void draw(Graphics2D g);

} // End of Class.