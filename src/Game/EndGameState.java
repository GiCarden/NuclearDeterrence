package Game;

import Graphics.Transitions.FadeRect;
import Input.GameAction;
import Input.InputManager;
import State.GameState;
import State.ResourceManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class EndGameState implements GameState {

    private boolean done;
    private boolean gameWon;

    private Image winImg;
    private Image loseImg;

    private Timer timer;
    private int duration;

    // Transitions
    private FadeRect fadeIn;
    private FadeRect fadeOut;
    private static boolean fadingIn;
    private static boolean fadingOut;

    private GameAction close;

    // End Game State
    public EndGameState() {

        close = new GameAction("close", GameAction.DETECT_INITAL_PRESS_ONLY);

        this.fadeIn = new FadeRect(0,0, GameManager.getScreenWidth(),
                GameManager.getScreenHeight(),1, Color.BLACK);
        this.fadeOut = new FadeRect(0,0, GameManager.getScreenWidth(),
                GameManager.getScreenHeight(),0, Color.BLACK);

        this.timer = new Timer();
    }

    // Start
    public void start(InputManager inputManager) {

        // Set Buttons to Skip Splash Screen
        inputManager.mapToKey(close, KeyEvent.VK_SPACE);
        inputManager.mapToKey(close, KeyEvent.VK_ENTER);
        inputManager.mapToKey(close, KeyEvent.VK_ESCAPE);

        // Set a Mouse Button to Skip Splash Screen
        inputManager.mapToMouse(close, InputManager.MOUSE_BUTTON_1);

        this.done = false;

        this.gameWon = GameManager.gameWon();

        // Transitions
        this.fadingIn = true;
        this.fadingOut = false;
        this.fadeIn.setAlpha(1);
        this.fadeOut.setAlpha(0);

        if(gameWon) duration = 42500; else duration = 53500;

        // Start Music
        GameManager.playGameOverMusic();

        // Setup Timer to fade out
        timer.cancel();
        timer = new Timer();

        // After timer ends fade out to game menu
        TimerTask action = new TimerTask() { public void run() { fadingOut = true; } };

        // Start Timer with Delay amount
        timer.schedule(action, duration);

        // Reset Menu Play Game Button
        GameManager.resetGameBN();

        GameManager.setNewGame(true);
    }

    // Update
    public void update(long elapsedTime) {

        if(fadingIn) {

            fadeIn.update(-0.007f);

            // Check if fade is complete
            if(fadeIn.getAlpha() == 0) fadingIn = false;

            return;
        } else if(fadingOut) {

            fadeOut.update(0.007f);

            // Check if fade is complete
            if(fadeOut.getAlpha() == 1) {

                GameManager.pauseGameOverMusic();
                done = true;
            }
            return;
        }

        // If user presses close then start fade
        if(close.isPressed()) fadingOut = true;
    }

    // Draw
    public void draw(Graphics2D g, int screenWidth, int screenHeight) {

        // Draw Black Over the Screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Dialogue Screen Offset
        int x = (screenWidth/2) - 400;
        int y = screenHeight - 600;

        if(gameWon) g.drawImage(winImg, x,0,null);
        else g.drawImage(loseImg, x, y,null);

        // Draw Fade In
        if(fadingIn) fadeIn.draw(g);

        // Draw Fade Out
        if(fadingOut) fadeOut.draw(g);
    }

    // Load Resources
    public void loadResources(ResourceManager resourceManager, int screenWidth, int screenHeight) {

        // Load Images
        this.winImg = resourceManager.loadImage("Win.png");
        this.loseImg = resourceManager.loadImage("Lose.png");
    }

    // Check for State Change
    public String checkForStateChange() { return done?"Menu":null; }

    // Get Name
    public String getName() { return "EndGame"; }

    // Stop
    public void stop() { }

    // Close Sound Managers
    public void closeSoundManagers() { }

} // End of Class.
