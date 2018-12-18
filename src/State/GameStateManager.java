package State;

import java.awt.*;
import java.util.*;
import Input.InputManager;

/**
 *  Code created by David Brackeen
 *  Copyright (c) 2003, David Brackeen
 */
public class GameStateManager {

    public static final String EXIT_GAME = "_ExitGame";
    private static Map gameStates;
    private Image defaultImage;
    private GameState currentState;
    private InputManager inputManager;
    private boolean done;

    // Game State Manager
    public GameStateManager(InputManager inputManager, Image defaultImage) {

        this.inputManager = inputManager;
        this.defaultImage = defaultImage;
        gameStates = new HashMap();
    }

    // Add Game State
    public void addState(GameState state) { gameStates.put(state.getName(), state); }

    // Get States
    public static Iterator getStates() { return gameStates.values().iterator(); }

    // Load All Resources
    public void loadAllResources(ResourceManager resourceManager, int screenWidth, int screenHeight) {

        Iterator i = getStates();

        while (i.hasNext()) {

            GameState gameState = (GameState)i.next();
            gameState.loadResources(resourceManager, screenWidth, screenHeight);
        }
    }

    // Get State
    public GameState getState() { return this.currentState; }

    // Is Done
    public boolean isDone() { return done; }

    // Sets the current state (by name)
    public void setState(String name) {

        // Clean up old state
        if(currentState != null) currentState.stop();

        inputManager.clearAllMaps();

        if(name == EXIT_GAME) {

            done = true;
        } else {

            // Set new state
            currentState = (GameState)gameStates.get(name);
            if(currentState != null) currentState.start(inputManager);
        }
    }

    // Updates world, handles input
    public void update(long elapsedTime) {

        // If no state, pause a short time
        if(currentState == null) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) { }
        } else {

            String nextState =    currentState.checkForStateChange();

            if(nextState != null) {

                setState(nextState);
            } else {

                currentState.update(elapsedTime);
            }
        }
    }

    // Draw
    public void draw(Graphics2D g, int screenWidth, int screenHeight) {

        // Draw Black over the Screen
        g.setColor(Color.black);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Draw Game State or Default Image
        if(currentState != null) {

            currentState.draw(g, screenWidth, screenHeight);
        } else {

            g.drawImage(defaultImage, (screenWidth / 2) - (defaultImage.getWidth(null) / 2),
                    (screenHeight / 2) - (defaultImage.getHeight(null) / 2), null);
        }
    }

} // End of Class.