package Game;

import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.logging.*;
import javax.sound.sampled.AudioFormat;
import Sound.Sound;
import Graphics.Transforms.Shake;
import Graphics.Shapes.ARect;
import Sound.SoundManager;
import Input.InputManager;
import State.GameState;
import State.ResourceManager;
import State.GameStateManager;
import Util.TimeSmoothie;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Game Manager manages all parts of the game
 */
public class GameManager extends GameCore {

    static final Logger log = Logger.getLogger("Nuclear Deterrence");

    public static void main(String[] args) { new GameManager().run(); }

    // Game Resolution
    private static int screenWidth;
    private static int screenHeight;

    // Game Menu States
    private static String gameBN;
    private static ARect gameBNRect;
    private static boolean newGame;
    private static boolean gameStarted;

    // Global Variables
    public static Shake shake;
    private static boolean gameWon;
    private static boolean radioSfxIsPlaying;
    private static Sound win;
    private static Sound lose;
    private static SoundManager gameOverSoundManager;

    // Uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT = new AudioFormat(44100,16,1,true,false);

    // Sound Managers
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private InputManager inputManager;
    private GameStateManager gameStateManager;
    private TimeSmoothie timeSmoothie = new TimeSmoothie();

    // Init
    public void init() {

        // Game Menu States
        this.gameBN = "Play Game";
        this.gameStarted = false;

        // Shake controls the screen shaking during ground explosions
        this.shake = new Shake();

        log.setLevel(Level.INFO);

        // Init Sound Managers
        log.info("Init sound manager...");
        soundManager = new SoundManager(PLAYBACK_FORMAT,8);

        // Init Game Core
        log.info("Init gamecore...");
        super.init();

        // Init Input Manager
        log.info("Init input manager...");
        inputManager = new InputManager(screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        // Init Resource Manager
        log.info("Init resource manager..");
        resourceManager = new GameResourceManager(screen.getFullScreenWindow().getGraphicsConfiguration(),soundManager);

        // Screen Resolution
        this.screenWidth = screen.getWidth();
        this.screenHeight = screen.getHeight();

        // Init End Game Sound Manager
        gameOverSoundManager = new SoundManager(PLAYBACK_FORMAT);
        win = resourceManager.loadSound("Music/Win.wav");
        lose = resourceManager.loadSound("Music/Lose.wav");

        // Set Global Variables
        radioSfxIsPlaying = false;

        // Init Game States
        log.info("Init game states..");
        gameStateManager = new GameStateManager(inputManager, resourceManager.loadImage("Loading.png"));
        gameStateManager.addState(new SplashGameState(PLAYBACK_FORMAT));
        gameStateManager.addState(new IntroGameState(PLAYBACK_FORMAT));
        gameStateManager.addState(new MenuGameState(soundManager));
        gameStateManager.addState(new MainGameState(soundManager));
        gameStateManager.addState(new EndGameState());

        // Load resources (in separate thread)
        new Thread(() -> {

            log.info("Loading resources...");
            gameStateManager.loadAllResources(resourceManager, screenWidth, screenHeight);

            log.info("Setting to Splash state...");
            gameStateManager.setState("Splash");
        }).start();
    }

    // Update
    public void update(long elapsedTime) {

        if(gameStateManager.isDone()) stop();
        else {

            elapsedTime = timeSmoothie.getTime(elapsedTime);
            gameStateManager.update(elapsedTime);
        }
    }

    // Draw
    public void draw(Graphics2D g) { gameStateManager.draw(g, screenWidth, screenHeight); }

    // Close any resources used by the GameManager
    public void stop() {

        log.info("Stopping game.");
        super.stop();

        log.info("Closing sound managers.");
        soundManager.close();

        // Close End Game Music Sound Manager
        gameOverSoundManager.close();

        // Close Other Game States Looping Sound Managers
        Iterator i = GameStateManager.getStates();

        while(i.hasNext()) {

            GameState gameState = (GameState)i.next();
            if(gameState.getName().equals("MainGame")) gameState.closeSoundManagers();
        }
    }

    // GLOBAL VARIABLES
    // Get Screen Width
    public static int getScreenWidth() { return screenWidth; }

    // Get Screen Height
    public static int getScreenHeight() { return screenHeight; }

    // Get Game BN State to Display in Menu "Play Game" or "Resume Game"
    public static String getGameBN() { return gameBN; }

    // Reset Game Button
    public static void resetGameBN() {

        gameBN = "Play Game";
        gameStarted = false;

        setGameRectX(+12);
        setGameRectW(-27);
    }

    // Set Game BN State to Display in Menu "Play Game" or "Resume Game"
    public static void setGameBN(String newState) { gameBN = newState; }

    // Set Game Rectangle
    public static void setGameRect(ARect aRect) { gameBNRect = aRect; }

    // Set Game Rectangle X
    public static void setGameRectX(int x) { gameBNRect.setX(gameBNRect.getX() + x); }

    // Set Game Rectangle W
    public static void setGameRectW(int w) { gameBNRect.setW(gameBNRect.getW() + w); }

    // Get Game Rectangle
    public static ARect getGameRect() { return gameBNRect; }

    // Get Game Started
    public static boolean gameHasStarted() { return gameStarted; }

    // Set Game Started
    public static void setGameStarted(boolean state) { gameStarted = state; }

    // Get New Game
    public static boolean newGame() { return newGame; }

    // Get New Game
    public static void setNewGame(boolean state) { newGame = state; }

    // Get Radio SFX Is Playing
    public static boolean radioSFXIsPlaying() { return radioSfxIsPlaying; }

    // Set Radio SFX Is Playing
    public static void setRadioSFXIsPlaying(boolean state) { radioSfxIsPlaying = state; }

    // Set Game Won
    public static void setGameWon(boolean state) { gameWon = state; }

    // Get Game Won
    public static boolean gameWon() { return gameWon; }

    // Play Game Over Music
    public static void playGameOverMusic() {

        if(gameOverSoundManager.isPaused()) gameOverSoundManager.setPaused(false);

        if(gameWon) gameOverSoundManager.play(win, null, false);
        else gameOverSoundManager.play(lose, null, false);
    }

    // Pause Game Over Music
    public static void pauseGameOverMusic() { gameOverSoundManager.setPaused(true); }

} // End of Class.