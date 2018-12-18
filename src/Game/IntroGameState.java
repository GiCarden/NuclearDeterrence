package Game;

import Graphics.Transitions.FadeRect;
import Input.GameAction;
import Input.InputManager;
import Sound.Sound;
import Sound.SoundManager;
import State.GameState;
import State.ResourceManager;
import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Intro types a message on the screen by displaying one character at a time.
 *  Every character that displays on the screen randomly selects a typing sound effect to play.
 *
 *  The message is divided into paragraphs and there is a delay timer to transition between paragraphs which will
 *  play an enter key sound effect.
 *
 *  When the message is complete the intro state fades out.
 *  The user has the option to skip the intro which will begin the fading.
 */
public class IntroGameState implements GameState {

    private Font font;
    private int ch;
    private static Timer endTimer;
    private static Timer nextCharTimer;
    private static Timer fadeTimer;
    private static Timer dialogueTimer;
    private static Timer introCompleteTimer;
    private static boolean nextCharEnabled;
    private static boolean timerEnabled;
    private static boolean endTimerEnabled;
    private static boolean fadeTimerEnabled;
    private static boolean introComplete;
    private FadeRect fadeTransition;
    private int paragraph;
    private String dialogue_1;
    private String dialogue_2;
    private String dialogue_3;
    private String master_paragraph1;
    private String master_paragraph2;
    private String master_paragraph3;
    private boolean done;
    private GameAction exit;
    private long totalElapsedTime;
    private Sound music;
    private SoundManager soundManager;
    private static boolean fading;
    private static boolean fadeComplete;

    // Intro Game State
    public IntroGameState(AudioFormat PLAYBACK_FORMAT){

        exit = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);
        this.soundManager = new SoundManager(PLAYBACK_FORMAT);
        this.ch = 0;
        this.dialogueTimer = new Timer();
        this.endTimer = new Timer();
        this.nextCharTimer = new Timer();
        this.fadeTimer = new Timer();
        this.introCompleteTimer = new Timer();
        this.timerEnabled = false;
        this.endTimerEnabled = false;
        this.fadeTimerEnabled = false;
        this.introComplete = false;
        this.paragraph = 1;
        this.dialogue_1 = "";
        this.dialogue_2 = "";
        this.dialogue_3 = "";
        this.fading = false;
        this.fadeComplete = false;
        this.nextCharEnabled = false;
        fadeTransition = new FadeRect(0, 0, GameManager.getScreenWidth(),
                GameManager.getScreenHeight(),0, Color.BLACK);
    }

    // Start
    public void start(InputManager inputManager) {

        // Set Buttons to Skip Splash Screen
        inputManager.mapToKey(exit, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ENTER);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);

        // Set a Mouse Button to Skip Splash Screen
        inputManager.mapToMouse(exit, InputManager.MOUSE_BUTTON_1);

        totalElapsedTime = 0;

        done = false;

        // Create a timer to end the Intro if the user does not press a key
        introCompleteTimer.cancel();
        introCompleteTimer  = new Timer();

        TimerTask introAction = new TimerTask() {

            public void run() { introComplete = true; }
        };

        introCompleteTimer.schedule(introAction, 42500);

        soundManager.play(music);

        // Delay the typing to start with Music
        nextCharTimer.cancel();
        nextCharTimer = new Timer();
        TimerTask nextCharAction = new TimerTask() { public void run() { nextCharEnabled = true; } };
        nextCharTimer.schedule(nextCharAction, 500);
    }

    // Update
    public void update(long elapsedTime) {

        // Fade out the screen when intro is complete
        if(introComplete || fading) fadeOutScreen(); else getNextCharacter();

        // If user presses exit then start fade
        if(exit.isPressed() && !fading) fading = true;
    }

    // Draw
    public void draw(Graphics2D g, int screenWidth, int screenHeight) {

        // Draw Black Over the Screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Dialogue Screen Offset
        int x = (screenWidth/2) - 400;
        int y = (screenHeight/2) - 200;

        // Draw Paragraph 1
        drawMultiLine(g, dialogue_1, x, y);

        // Draw Paragraph 2
        if(paragraph > 1) drawMultiLine(g, dialogue_2, x, y + 100);

        // Draw Paragraph 3
        if(paragraph > 2) drawMultiLine(g, dialogue_3, x, y + 200);

        // Draw Fade
        if(fading) fadeTransition.draw(g);
    }

    // End State
    private void endState() {

        soundManager.setPaused(true);
        done = true;
    }

    // Fade Out Screen
    private void fadeOutScreen() {

        // Increase Alpha to Fade
        if(fading) { fadeTransition.update(0.009f); }
        else {

            if(!fadeTimerEnabled) {

                fadeTimerEnabled = true;

                // Set a timer to delay the fade after the Enter Key SFX plays
                // This will cancel the current task. If there is no active task, nothing happens.
                fadeTimer.cancel();
                fadeTimer = new Timer();

                // After timer ends set fade to true
                TimerTask fadeAction = new TimerTask() { public void run() { fading = true; } };

                // Start Timer with Delay amount
                fadeTimer.schedule(fadeAction, 10);
            }
        }

        // Check if fade is complete
        if(fadeTransition.getAlpha() == 1) fadeComplete = true;

        // When fading is complete end state
        if(fadeComplete) {

            if(!endTimerEnabled) {

                endTimerEnabled = true;

                // Set a timer to delay the fade after the Enter Key SFX plays
                // This will cancel the current task. If there is no active task, nothing happens.
                endTimer.cancel();
                endTimer = new Timer();

                // After timer ends set fade to true
                TimerTask fadeEndAction = new TimerTask() { public void run() { endState(); } };

                // Start Timer with Delay amount
                fadeTimer.schedule(fadeEndAction, 500);
            }
        }
    }

    // Get Next Character
    private void getNextCharacter() {

        // Update Characters in Dialogue
        if(nextCharEnabled && paragraph == 3) dialogue_3 += updateDialogue(master_paragraph3);
        if(nextCharEnabled && paragraph == 2) dialogue_2 += updateDialogue(master_paragraph2);
        if(nextCharEnabled && paragraph == 1) dialogue_1 += updateDialogue(master_paragraph1);

        if(nextCharEnabled) {

            nextCharEnabled = false;

            nextCharTimer.cancel();
            nextCharTimer = new Timer();
            TimerTask nextCharAction = new TimerTask() { public void run() { nextCharEnabled = true; } };
            nextCharTimer.schedule(nextCharAction, 125);
        }
    }

    // Update Dialogue
    private String updateDialogue(String dialogue) {

        // Get new character to add to the paragraph or increment to next paragraph

        String newChar = "";

        if(ch < dialogue.length()) {

            newChar += dialogue.charAt(ch);
            ch ++;
        } else {

            if(!timerEnabled && paragraph != 3) {

                // Set a timer to delay the next paragraph
                // This will cancel the current task. If there is no active task, nothing happens.
                dialogueTimer.cancel();
                dialogueTimer = new Timer();

                // After timer ends set new paragraph
                TimerTask action = new TimerTask() { public void run() { nextParagraph(); } };

                // Start Timer with Delay amount
                dialogueTimer.schedule(action, 1000);

                timerEnabled = true;
            }
        }

        return newChar;
    }

    // Next Paragraph
    private void nextParagraph(){

        ch = 0;
        paragraph++;
        timerEnabled = false;
    }

    // Draw Multi Line Text
    private void drawMultiLine(Graphics2D g, String text, int x, int y) {

        // Set Color
        g.setColor(Color.WHITE);

        // Set Font
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics();

        if(metrics.stringWidth(text) < 800) g.drawString(text, x, y);
        else {

            String[] words = text.split(" ");
            String currentLine = words[0];

            for(int i = 1; i < words.length; i++) {

                if(metrics.stringWidth(currentLine+words[i]) < 800) currentLine += " "+ words[i];
                else {

                    g.drawString(currentLine, x, y);
                    y += metrics.getHeight();
                    currentLine = words[i];
                }
            }

            if(currentLine.trim().length() > 0) g.drawString(currentLine, x, y);
        }
    }

    // Load Resources
    public void loadResources(ResourceManager resourceManager, int screenWidth, int screenHeight) {

        // Set Font
        font = new Font("Stencil Std", Font.PLAIN, 30);

        // Dialogue
        master_paragraph1 = "The U.S. economy collapsed causing a ripple effect across world markets.";

        master_paragraph2 = "China begins threatening Nuclear War if other Countries do not pay their debts.";

        master_paragraph3 = "Mission: A synchronized attack on China's Nuclear Silos by sneaking over the " +
                            "border and destroying the closest Silo.";

        // Load Music
        this.music = resourceManager.loadSound("Music/Win.wav");
    }

    // Check for State Change
    public String checkForStateChange() { return done?"Menu":null; }

    // Get Name
    public String getName() { return "Intro"; }

    // Stop
    public void stop() { soundManager.close(); }

    // Close Sound Managers
    public void closeSoundManagers() { }

} // End of Class.