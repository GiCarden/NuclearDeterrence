package Game;

import Graphics.Shapes.RoundRect;
import Graphics.Transitions.FadeRect;
import Graphics.Transitions.FadeRoundRect;
import Input.GameAction;
import Input.InputManager;
import Sound.Sound;
import Sound.SoundManager;
import Graphics.Shapes.ARect;
import State.GameState;
import State.GameStateManager;
import State.ResourceManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.Timer;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class MenuGameState implements GameState {

    // Background Image
    private static Image image;
    private static Image options_UH60;
    private static int imageOffsetX;
    private static int imageOffsetY;

    // Screen Center
    private static int centerX;
    private static int centerY;

    // Fonts
    private Font autoFont;
    private Font stencilFont;
    private Color color;

    // Button Index's
    private static int creditsBN   = 5;
    private static int optionsBN   = 4;
    private static int newGameBN   = 3;
    private static int exitGameBN  = 2;
    private static int objectiveBN = 1;
    private static int gameBN      = 0;
    private int currentBtnSelection;
    private static boolean btnBoundaryVisible;

    // Pop Up Page with Yes/No
    private static boolean noSelected;
    private static boolean yesSelected;

    // Main Button Rectangles
    private static ARect noBNRect;
    private static ARect yesBNRect;
    private static ARect objBNRect;
    private static ARect exitBNRect;
    private static ARect closeBNRect;
    private static ARect newGameBNRect;
    private static ARect optionsBNRect;
    private static ARect creditsBNRect;

    // Options Button Rectangles
    private static RoundRect nameBNRect;
    private static RoundRect fwBNRect;
    private static RoundRect bwBNRect;
    private static RoundRect srBNRect;
    private static RoundRect slBNRect;
    private static RoundRect bulletBNRect;
    private static RoundRect rocketBNRect;
    private static ARect defaultBNRect;
    private static ARect saveKeysBNRect;
    private static ARect saveNameBNRect;

    // Main Page Index's
    private static int creditsPg = 5;
    private static int optionsPg = 4;
    private static int newGamePg = 3;
    private static int exitGamePg = 2;
    private static int objectivePg = 1;
    private static int mainPg = 0;
    private int currentPgSelection;

    // Options Page Index's
    private static int fwBtn = 0;
    private static int bwBtn = 1;
    private static int srBtn = 2;
    private static int slBtn = 3;
    private static int bulletBtn = 4;
    private static int rocketBtn = 5;
    private static int nameBtn = 6;
    private static int saveKeysBtn = 7;
    private static int saveNameBtn = 8;
    private int optionsBtnSelection;

    // Pop Up Page
    private RoundRect popUpBd;
    private FadeRoundRect popUpBG;

    // Objective
    private String objective_1;
    private String objective_2;
    private Image objective;

    // States
    private boolean done;
    private String state;

    // Input Action
    private InputManager inputManager;
    private GameAction up;
    private GameAction down;
    private GameAction right;
    private GameAction left;
    private GameAction enter;
    private GameAction click;
    private GameAction showPolys;

    // Mouse
    private static int mouseOldX;
    private static int mouseOldY;
    private static boolean mouseOverBTN;

    // Screen Transition
    private FadeRect fadeIn;
    private FadeRect fadeOut;
    private static float fadeOutSpeed = 0.02f;
    private static boolean fadingIn;
    private static boolean fadingOut;

    // Audio
    private Sound[] radioSFX;
    private SoundManager soundManager;

    // SFX Timer
    private Random rand;
    private static Timer timer;

    // Remove Splash and Intro States
    private static boolean statesRemoved;

    // Intro Game State ---------------------------------------------------------------------------------------------//
    public MenuGameState(SoundManager soundManager) {

        this.state = "";
        this.up = new GameAction("up", GameAction.DETECT_INITAL_PRESS_ONLY);
        this.down = new GameAction("dn", GameAction.DETECT_INITAL_PRESS_ONLY);
        this.right = new GameAction("right", GameAction.DETECT_INITAL_PRESS_ONLY);
        this.left = new GameAction("left", GameAction.DETECT_INITAL_PRESS_ONLY);
        this.enter = new GameAction("enter", GameAction.DETECT_INITAL_PRESS_ONLY);
        this.click = new GameAction("click", GameAction.DETECT_INITAL_PRESS_ONLY);
        this.showPolys = new GameAction("show polys", GameAction.DETECT_INITAL_PRESS_ONLY);

        this.btnBoundaryVisible = false;

        this.fadeIn = new FadeRect(0, 0, GameManager.getScreenWidth(),
                GameManager.getScreenHeight(), 1, Color.BLACK);

        this.fadeOut = new FadeRect(0, 0, GameManager.getScreenWidth(),
                GameManager.getScreenHeight(), 0, Color.BLACK);

        // SFX Timer
        this.timer = new Timer();
        this.rand = new Random();

        this.soundManager = soundManager;

        this.statesRemoved = false;
    }

    // Start
    public void start(InputManager inputManager) {

        // Remove Splash State and Intro State to clear memory
        if(!statesRemoved) {

            Iterator i = GameStateManager.getStates();

            while(i.hasNext()) {

                GameState gameState = (GameState)i.next();
                if(gameState.getName().equals("Splash")) i.remove();
                if(gameState.getName().equals("Intro"))  i.remove();
            }
            statesRemoved = true;
        }

        this.inputManager = inputManager;

        // Set Buttons to Skip Splash Screen
        inputManager.mapToKey(up, KeyEvent.VK_UP);
        inputManager.mapToKey(down, KeyEvent.VK_DOWN);
        inputManager.mapToKey(right, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(left, KeyEvent.VK_LEFT);
        inputManager.mapToKey(enter, KeyEvent.VK_ENTER);
        inputManager.mapToKey(showPolys, KeyEvent.VK_P);

        inputManager.setCursor(inputManager.DEFAULT_CURSOR);
        inputManager.mapToMouse(click, inputManager.MOUSE_BUTTON_1);

        this.currentBtnSelection = gameBN;
        this.optionsBtnSelection = 0;
        this.noSelected = true;
        this.yesSelected = false;
        setPgSelection(mainPg);

        this.mouseOldX = inputManager.getMouseX();
        this.mouseOldY = inputManager.getMouseY();
        this.mouseOverBTN = false;

        // State
        this.done = false;

        // Transitions
        this.fadingIn = true;
        this.fadingOut = false;
        this.fadeIn.setAlpha(1);
        this.fadeOut.setAlpha(0);
    }

    // Update
    public void update(long elapsedTime) {

        // Update the menu fading transition
        if(fadingIn) {

            fadeIn.update(-0.02f);

            // Check if fade is complete
            if(fadeIn.getAlpha() == 0) fadingIn = false; else return;
        } else if(fadingOut) {

            fadeOut.update(fadeOutSpeed);

            // Check if fade is complete
            if(fadeOut.getAlpha() == 1) done = true; else return;
        }

        // Check Menu Input if no transition is in process and based on which page is selected
        if(currentPgEquals(mainPg))checkMenuInput();
        if(currentPgEquals(objectivePg)) checkSingleBtnInput();
        if(currentPgEquals(exitGamePg))checkDoubleBtnInput();
        if(currentPgEquals(newGamePg)) checkDoubleBtnInput();
        if(currentPgEquals(optionsPg)) checkOptionsInput();
        if(currentPgEquals(creditsPg)) checkSingleBtnInput();

        // Play Radio Chatter
        if(!GameManager.radioSFXIsPlaying()) {

            playSFX();

            GameManager.setRadioSFXIsPlaying(true);

            // Set a timer to enable net radio sfx to play
            timer.cancel();
            timer = new Timer();

            // After timer ends set sfx playing to false
            TimerTask action = new TimerTask() { public void run() { GameManager.setRadioSFXIsPlaying(false); } };

            // Start Timer with Delay amount
            timer.schedule(action, 12000);
        }

        // Show Button Poly's
        if(showPolys.isPressed()) setBtnBoundaryVisible();
    }

    // Check Menu Input
    private void checkMenuInput() {

        // Get Mouse Location
        int mouseX = inputManager.getMouseX();
        int mouseY = inputManager.getMouseY();

        // Check Input
        if(up.isPressed()) {

            if(currentBtnEquals(gameBN)) setBtnSelection(exitGameBN);
            else if(currentBtnEquals(newGameBN)) setBtnSelection(creditsBN);
            else currentBtnSelection --;

            // Only allow selections of new game if a game has started
            if(!GameManager.gameHasStarted() && currentBtnEquals(newGameBN)) currentBtnSelection += 2;
        } else if(down.isPressed()) {

            if(currentBtnEquals(exitGameBN))setBtnSelection(gameBN);
            else if(currentBtnEquals(creditsBN)) setBtnSelection(newGameBN);
            else currentBtnSelection ++;

            // Only allow selections of new game if a game has started
            if(!GameManager.gameHasStarted() && currentBtnEquals(newGameBN)) currentBtnSelection += 1;
        } else if(right.isPressed() || left.isPressed()) {

            if(currentBtnEquals(gameBN) && GameManager.gameHasStarted()) setBtnSelection(newGameBN);
            else if(currentBtnEquals(objectiveBN)) setBtnSelection(optionsBN);
            else if(currentBtnEquals(creditsBN)) setBtnSelection(exitGameBN);
            else if(currentBtnEquals(optionsBN)) setBtnSelection(objectiveBN);
            else if(currentBtnEquals(newGameBN)) setBtnSelection(gameBN);
            else if(currentBtnEquals(exitGameBN)) setBtnSelection(creditsBN);
        } else if(mouseOldX != mouseX || (mouseOldY != mouseY)) {

            // Check if the Mouse Interacted with the buttons
            mouseOldX = mouseX;
            mouseOldY = mouseY;

            // Check if a Rectangle contains the mouse
            if(GameManager.getGameRect().contains(mouseX, mouseY)) {

                setBtnSelection(gameBN);
                setMouseOverBtn(true);
            } else if(objBNRect.contains(mouseX, mouseY)) {

                setBtnSelection(objectiveBN);
                setMouseOverBtn(true);
            } else if(exitBNRect.contains(mouseX, mouseY)) {

                setBtnSelection(exitGameBN);
                setMouseOverBtn(true);
            } else if(newGameBNRect.contains(mouseX, mouseY) && GameManager.gameHasStarted()) {

                setBtnSelection(newGameBN);
                setMouseOverBtn(true);
            } else if(optionsBNRect.contains(mouseX, mouseY)) {

                setBtnSelection(optionsBN);
                setMouseOverBtn(true);

            } else if(creditsBNRect.contains(mouseX, mouseY)) {

                setBtnSelection(creditsBN);
                setMouseOverBtn(true);
            }
            else setMouseOverBtn(false);
        }

        // Check Button Selection
        if(enter.isPressed() || (click.isPressed() && mouseOverBTN)) {

            if(currentBtnEquals(objectiveBN)) setPgSelection(objectivePg);
            else if(currentBtnEquals(optionsBN)) setPgSelection(optionsPg);
            else if(currentBtnEquals(creditsBN)) setPgSelection(creditsPg);
            else if(currentBtnEquals(exitGameBN)) {

                noSelected  = true;
                yesSelected = false;
                setPgSelection(exitGamePg);
            } else if(currentBtnEquals(newGameBN)) {

                noSelected  = true;
                yesSelected = false;
                setPgSelection(newGamePg);
            } else if(currentBtnEquals(gameBN)) {

                state = "Main";
                timer.cancel();
                fadingOut = true;
                inputManager.setCursor(inputManager.INVISIBLE_CURSOR);
            }
        }
    }

    // Check Single Button Input
    private void checkSingleBtnInput() {

        // Get Mouse Location
        int mouseX = inputManager.getMouseX();
        int mouseY = inputManager.getMouseY();

        // Set Button Location
        int offsetX = centerX - 25;
        int offsetY = centerY + (popUpBd.getH()/2) - 40;

        closeBNRect.setX(offsetX);
        closeBNRect.setY(offsetY);

        // Check if the button contains the mouse
        if(closeBNRect.contains(mouseX, mouseY)) setMouseOverBtn(true); else setMouseOverBtn(false);

        // Check Button Selection
        if(enter.isPressed() || (click.isPressed() && mouseOverBTN)) setPgSelection(mainPg);
    }

    // Check Single Button Input
    private void checkDoubleBtnInput() {

        // Get Mouse Location
        int mouseX = inputManager.getMouseX();
        int mouseY = inputManager.getMouseY();

        // Check Button Selection
        if(right.isPressed() || left.isPressed()) {

            if(yesSelected) {

                noSelected = true;
                yesSelected = false;
            } else {

                noSelected = false;
                yesSelected = true;
            }
        } else if(mouseOldX != mouseX || (mouseOldY != mouseY)) {

            // Check if the Mouse Interacted with the buttons
            mouseOldX = mouseX;
            mouseOldY = mouseY;

            if(yesBNRect.contains(mouseX, mouseY)) {

                noSelected = false;
                yesSelected = true;
                setMouseOverBtn(true);
            } else if(noBNRect.contains(mouseX, mouseY)) {

                noSelected = true;
                yesSelected = false;
                setMouseOverBtn(true);
            }
            else setMouseOverBtn(false);
        }

        if(enter.isPressed() || (click.isPressed() && mouseOverBTN)) {

            if(yesSelected && currentPgEquals(exitGamePg)) {

                fadeOutSpeed = 0.009f;
                state = GameStateManager.EXIT_GAME;
                fadingOut = true;
                inputManager.setCursor(inputManager.INVISIBLE_CURSOR);
            } else if(yesSelected && currentPgEquals(newGamePg)) {

                state = "Main";
                timer.cancel();
                fadingOut = true;
                GameManager.setNewGame(true);
                inputManager.setCursor(inputManager.INVISIBLE_CURSOR);
            }
            else if(noSelected) setPgSelection(mainPg);
        }
    }

    // Check Options Input
    private void checkOptionsInput() {

        // Get Mouse Location
        int mouseX = inputManager.getMouseX();
        int mouseY = inputManager.getMouseY();

        // Check Input
        if(up.isPressed()) {

            if(currentOpBtnEquals(fwBtn)) setOpBtnSelection(nameBtn); else optionsBtnSelection --;
        } else if(down.isPressed()) {

            if(currentOpBtnEquals(nameBtn)) setOpBtnSelection(fwBtn); else optionsBtnSelection ++;
        } else if(mouseOldX != mouseX || (mouseOldY != mouseY)) {

            // Check if the Mouse Interacted with the buttons
            mouseOldX = mouseX;
            mouseOldY = mouseY;

            // Check if a Rectangle contains the mouse
            if(fwBNRect.contains(mouseX, mouseY)) {

                setOpBtnSelection(fwBtn);
                setMouseOverBtn(true);
            }
            else if(bwBNRect.contains(mouseX, mouseY)) {

                setOpBtnSelection(bwBtn);
                setMouseOverBtn(true);
            }
            else if(srBNRect.contains(mouseX, mouseY)) {

                setOpBtnSelection(srBtn);
                setMouseOverBtn(true);
            }
            else if(slBNRect.contains(mouseX, mouseY)) {

                setOpBtnSelection(slBtn);
                setMouseOverBtn(true);
            }
            else if(bulletBNRect.contains(mouseX, mouseY)) {

                setOpBtnSelection(bulletBtn);
                setMouseOverBtn(true);
            }
            else if(rocketBNRect.contains(mouseX, mouseY)) {

                setOpBtnSelection(rocketBtn);
                setMouseOverBtn(true);
            }
            else if(nameBNRect.contains(mouseX, mouseY)) {

                setOpBtnSelection(nameBtn);
                setMouseOverBtn(true);
            }
        }

        // Set Button Location
        int offsetX = centerX - 25;
        int offsetY = centerY + (popUpBd.getH()/2) - 100;

        closeBNRect.setX(offsetX);
        closeBNRect.setY(offsetY);

        // Check if the button contains the mouse
        if(closeBNRect.contains(mouseX, mouseY)) setMouseOverBtn(true); else setMouseOverBtn(false);

        // Check Button Selection
        if(enter.isPressed() || (click.isPressed() && mouseOverBTN)) setPgSelection(mainPg);
    }

    // Draw
    public void draw(Graphics2D g, int screenWidth, int screenHeight) {

        // Draw Game Menu
        drawMenu(g);

        // Draw Pop Up Pages
        if(!currentPgEquals(mainPg)) popUpBG.draw(g);
        if(!currentPgEquals(mainPg)) popUpBd.draw(g);

        // Draw Objective Page
        if(currentPgEquals(objectivePg)) drawObjective(g);

        // Draw Exit Game Page
        if(currentPgEquals(exitGamePg)) drawExit(g);

        // Draw New Game Page
        if(currentPgEquals(newGamePg)) drawNewGame(g);

        // Draw Options Page
        if(currentPgEquals(optionsPg)) drawOptions(g);

        // Draw Credits Page
        if(currentPgEquals(creditsPg)) drawCredits(g, screenWidth, screenHeight);

        // Draw Fade In
        if(fadingIn) fadeIn.draw(g);

        // Draw Fade Out
        if(fadingOut) fadeOut.draw(g);
    }

    // Draw Menu
    private void drawMenu(Graphics2D g) {

        // Set Font for Menu Selections
        g.setFont(autoFont);
        g.setColor(color);

        // Draw Background Image
        g.drawImage(image, imageOffsetX, imageOffsetY,null);

        // If Menu Item is selected change color
        // Draw Left Screen Buttons
        if(currentBtnEquals(gameBN)) g.setColor(Color.WHITE);
        else g.setColor(color);
        g.drawString(GameManager.getGameBN(), GameManager.getGameRect().getX(),
                     GameManager.getGameRect().getY() + GameManager.getGameRect().getH());


        if(currentBtnEquals(objectiveBN)) g.setColor(Color.WHITE);
        else g.setColor(color);
        g.drawString("Objective", objBNRect.getX(), objBNRect.getY() + objBNRect.getH());


        if(currentBtnEquals(exitGameBN)) g.setColor(Color.WHITE);
        else g.setColor(color);
        g.drawString("Exit Game", exitBNRect.getX(), exitBNRect.getY() + exitBNRect.getH());


        // Draw Right Screen Buttons
        if(GameManager.gameHasStarted()) {

            // If a game has started then enable New Game button else draw as dark gray
            if(currentBtnEquals(newGameBN)) g.setColor(Color.WHITE);
            else g.setColor(color);
            g.drawString("New Game", newGameBNRect.getX(), newGameBNRect.getY() + newGameBNRect.getH());
        } else {

            g.setColor(Color.DARK_GRAY);
            g.drawString("New Game", newGameBNRect.getX(), newGameBNRect.getY() + newGameBNRect.getH());
        }


        if(currentBtnEquals(optionsBN)) g.setColor(Color.WHITE);
        else g.setColor(color);
        g.drawString("Options", optionsBNRect.getX(), optionsBNRect.getY() + optionsBNRect.getH());


        if(currentBtnEquals(creditsBN)) g.setColor(Color.WHITE);
        else g.setColor(color);
        g.drawString("Credits", creditsBNRect.getX(), creditsBNRect.getY() + creditsBNRect.getH());

        // Draw Button Rectangle Boundaries
        if(btnBoundaryVisible) {

            g.setColor(Color.RED);

            GameManager.getGameRect().draw(g);
            objBNRect.draw(g);
            exitBNRect.draw(g);
            newGameBNRect.draw(g);
            optionsBNRect.draw(g);
            creditsBNRect.draw(g);
        }
    }

    // Draw Objective
    private void drawObjective(Graphics2D g) {

        g.setFont(stencilFont);
        g.setColor(Color.WHITE);

        // Draw Objective
        drawMultiLine(g, stencilFont, Color.WHITE, objective_1, popUpBd.getX() + 10, popUpBd.getY() + 25);
        drawMultiLine(g, stencilFont, Color.WHITE, objective_2, popUpBd.getX() + 10, popUpBd.getY() + 80);
        g.drawImage(objective, popUpBd.getX(), popUpBd.getY() + 115, null);

        // Draw Close Button
        if(mouseOverBTN) g.setColor(color);

        Font font = new Font("Stencil STD", Font.PLAIN, 20);
        g.setFont(font);
        g.drawString("Close", closeBNRect.getX(), closeBNRect.getY() + closeBNRect.getH());

        // Draw Button Rectangle Boundaries
        if(btnBoundaryVisible) closeBNRect.draw(g);
    }

    // Draw Exit
    private void drawExit(Graphics2D g) {

        // Draw Heading
        Font font = new Font("Stencil STD", Font.PLAIN, 40);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Are you sure you want to quit?", popUpBd.getX() + 20, popUpBd.getY() + 200);
        g.drawLine(popUpBd.getX() + 20, popUpBd.getY() + 205, popUpBd.getX() + 780, popUpBd.getY() + 205);

        // Draw Buttons
        font = new Font("Stencil STD", Font.PLAIN, 30);
        g.setFont(font);

        if(yesSelected) g.setColor(color);
        else            g.setColor(Color.WHITE);
                        g.drawString("Yes", yesBNRect.getX(), yesBNRect.getY() + yesBNRect.getH());
                        g.setColor(Color.WHITE);

        if(noSelected)  g.setColor(color);
        else            g.setColor(Color.WHITE);
                        g.drawString("No", noBNRect.getX(), noBNRect.getY() + noBNRect.getH());

        // Draw Button Rectangle Boundaries
        if(btnBoundaryVisible) {

            noBNRect. draw(g);
            yesBNRect.draw(g);
        }
    }

    // Draw New Game
    private void drawNewGame(Graphics2D g) {

        // Draw Heading
        Font font = new Font("Stencil STD", Font.PLAIN, 28);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Are you sure you want to start a new game?", popUpBd.getX() + 30, popUpBd.getY() + 200);
        g.drawLine(popUpBd.getX() + 20, popUpBd.getY() + 205, popUpBd.getX() + 780, popUpBd.getY() + 205);

        // Draw Buttons
        font = new Font("Stencil STD", Font.PLAIN, 30);
        g.setFont(font);

        if(yesSelected) g.setColor(color);
        else            g.setColor(Color.WHITE);
        g.drawString("Yes", yesBNRect.getX(), yesBNRect.getY() + yesBNRect.getH());
        g.setColor(Color.WHITE);

        if(noSelected)  g.setColor(color);
        else            g.setColor(Color.WHITE);
        g.drawString("No", noBNRect.getX(), noBNRect.getY() + noBNRect.getH());

        // Draw Button Rectangle Boundaries
        if(btnBoundaryVisible) {

            noBNRect. draw(g);
            yesBNRect.draw(g);
        }
    }

    // Draw Options
    private void drawOptions(Graphics2D g) {

        // Draw Headings
        Font font = new Font("Stencil STD", Font.PLAIN, 20);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("Map game keys: A-Z, Space Bar", centerX-175, popUpBd.getY() + 70);
        g.drawLine(popUpBd.getX() +  50, popUpBd.getY() + 75, popUpBd.getX() +  750, popUpBd.getY() + 75);
        g.drawLine(popUpBd.getX() +  50, popUpBd.getY() + 309, popUpBd.getX() +  750, popUpBd.getY() + 309);
        g.drawImage(options_UH60, popUpBd.getX() +  80, popUpBd.getY() + 80, null);

        g.drawString("Change Player Name", centerX-100, popUpBd.getY() + 360);
        g.drawLine(popUpBd.getX() +  50, popUpBd.getY() + 440, popUpBd.getX() +  750, popUpBd.getY() + 440);

        // Draw Key Map Text Plus Fill Rectangles
        g.setFont(stencilFont);
        g.drawString("Move Forward", centerX + 90, popUpBd.getY() + 100);
        g.fillRoundRect(fwBNRect.getX(), fwBNRect.getY(), fwBNRect.getW(), fwBNRect.getH(), 10, 10);

        g.drawString("Move Backward", centerX + 90, popUpBd.getY() + 135);
        g.fillRoundRect(bwBNRect.getX(), bwBNRect.getY(), bwBNRect.getW(), bwBNRect.getH(), 10, 10);

        g.drawString("Strafe Left",   centerX + 90, popUpBd.getY() + 170);
        g.fillRoundRect(slBNRect.getX(), slBNRect.getY(), slBNRect.getW(), slBNRect.getH(), 10, 10);

        g.drawString("Strafe Right",  centerX + 90, popUpBd.getY() + 205);
        g.fillRoundRect(srBNRect.getX(), srBNRect.getY(), srBNRect.getW(), srBNRect.getH(), 10, 10);

        g.drawString("Shoot Bullet",   centerX + 90, popUpBd.getY() + 240);
        g.fillRoundRect(bulletBNRect.getX(), bulletBNRect.getY(), bulletBNRect.getW(), bulletBNRect.getH(), 10, 10);

        g.drawString("Shoot Rocket",  centerX + 90, popUpBd.getY() + 275);
        g.fillRoundRect(rocketBNRect.getX(), rocketBNRect.getY(), rocketBNRect.getW(), rocketBNRect.getH(), 10, 10);

        // Name Rectangle
        g.fillRoundRect(centerX-25, popUpBd.getY() + 380, 100, 20, 10, 10);

        // Draw Kep Map Borders
        if(currentOpBtnEquals(nameBtn))nameBNRect.draw(g);
        if(currentOpBtnEquals(fwBtn)) fwBNRect.draw(g);
        if(currentOpBtnEquals(bwBtn)) bwBNRect.draw(g);
        if(currentOpBtnEquals(srBtn)) srBNRect.draw(g);
        if(currentOpBtnEquals(slBtn)) slBNRect.draw(g);
        if(currentOpBtnEquals(bulletBtn)) bulletBNRect.draw(g);
        if(currentOpBtnEquals(rocketBtn)) rocketBNRect.draw(g);

        // Draw Mapped Keys
        font = new Font("Stencil STD", Font.PLAIN, 14);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString("W",centerX+55,popUpBd.getY() + 100);
        g.drawString("S",centerX+55,popUpBd.getY() + 135);
        g.drawString("A",centerX+55,popUpBd.getY() + 170);
        g.drawString("D",centerX+55,popUpBd.getY() + 205);
        g.drawString("SP", centerX+55,popUpBd.getY() + 240);
        g.drawString("R", centerX+55,popUpBd.getY() + 275);

        // Draw Close Button
        font = new Font("Stencil STD", Font.PLAIN, 20);
        g.setFont(font);
        if(mouseOverBTN) g.setColor(color);else g.setColor(Color.WHITE);

        g.drawString("Close", closeBNRect.getX(), closeBNRect.getY() + closeBNRect.getH());

        // Draw Button Rectangle Boundaries
        if(btnBoundaryVisible) {

            g.setColor(Color.RED);
            nameBNRect.draw(g);
            closeBNRect.draw(g);
            defaultBNRect.draw(g);
            saveKeysBNRect.draw(g);
        }
    }

    // Draw Credits
    private void drawCredits(Graphics2D g, int screenWidth, int screenHeight) {

        Font font = new Font("Stencil STD", Font.PLAIN, 25);
        g.setFont(font);
        g.setColor(Color.WHITE);

        g.drawString("Project Contributors", (screenWidth/2) - 165, (screenHeight/2) - 203);
        g.drawLine((screenWidth/2) - 200, (screenHeight/2) - 200, (screenWidth/2) + 200, (screenHeight/2) - 200);

        String brett  = "Brett Bearden";
        String giovanni = "Giovanni Cardenas";

        g.drawString(brett,    (screenWidth/2) - 95,  (screenHeight/2) -  70);
        g.drawString(giovanni, (screenWidth/2) - 125, (screenHeight/2) -  30);

        // Draw Close Button
        if(mouseOverBTN) g.setColor(color); else g.setColor(Color.WHITE);

        g.setFont(stencilFont);
        g.drawString("Close", closeBNRect.getX(), closeBNRect.getY() + closeBNRect.getH());

        // Draw Button Rectangle Boundaries
        if(btnBoundaryVisible) {

            g.setColor(Color.RED);
            closeBNRect.draw(g);
        }
    }

    // Draw Multi Line Text
    private void drawMultiLine(Graphics2D g, Font font, Color color, String text, int x, int y) {

        // Set Color
        g.setColor(color);

        // Set Font
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics();

        if(metrics.stringWidth(text) < 800) g.drawString(text, x, y);
        else {

            String[] words       = text.split(" ");
            String   currentLine = words[0];

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

    // Play Radio Chatter SFX
    private void playSFX() {

        // Randomly select a radio chatter to play
        int index = rand.nextInt(26);

        soundManager.play(radioSFX[index]);
    }

    // Load Resources
    public void loadResources(ResourceManager resourceManager, int screenWidth, int screenHeight) {

        // Center of Screen X/Y
        this.centerX = (screenWidth/2);
        this.centerY = (screenHeight/2);

        // Pop Up Page
        popUpBd = new RoundRect (centerX - 400, centerY - 300, 799, 599, 10, 10, Color.WHITE);
        popUpBG = new FadeRoundRect(centerX - 400, centerY - 300, 799, 599, 10, 10, 0.9f, Color.DARK_GRAY);

        // Instantiate Button Rectangles: ARect(x, y, w, h, color)
        ARect gameRect = new ARect(centerX - 228, centerY + 145, 86, 16, Color.RED);
        GameManager.setGameRect(gameRect);

        // Main Menu Rectangles
        this.objBNRect = new ARect(centerX - 225, centerY + 166, 80, 16, Color.RED);
        this.closeBNRect = new ARect(centerX, centerY,67, 16, Color.RED);
        this.exitBNRect = new ARect(centerX - 226, centerY + 187, 85, 16, Color.RED);
        this.newGameBNRect = new ARect(centerX + 110, centerY + 145, 85, 16, Color.RED);
        this.optionsBNRect = new ARect(centerX + 122, centerY + 166, 65, 16, Color.RED);
        this.creditsBNRect = new ARect(centerX + 122, centerY + 187, 65, 16, Color.RED);

        // Options Page Rectangles
        this.defaultBNRect = new ARect(centerX-200, centerY-20, 67, 16, Color.RED);
        this.saveKeysBNRect = new ARect(centerX+200, centerY-20, 67, 16, Color.RED);
        this.saveNameBNRect = new ARect(centerX-100, centerY-50, 67, 16, Color.RED);
        this.nameBNRect = new RoundRect(centerX - 25, popUpBd.getY() +  380, 100, 20, 10, 10, Color.RED);
        this.fwBNRect = new RoundRect(centerX + 50, popUpBd.getY() +   83, 35, 20, 10, 10, Color.RED);
        this.bwBNRect = new RoundRect(centerX + 50, popUpBd.getY() +  118, 35, 20, 10, 10, Color.RED);
        this.srBNRect = new RoundRect(centerX + 50, popUpBd.getY() +  153, 35, 20, 10, 10, Color.RED);
        this.slBNRect = new RoundRect(centerX + 50, popUpBd.getY() +  188, 35, 20, 10, 10, Color.RED);
        this.bulletBNRect = new RoundRect(centerX + 50, popUpBd.getY() +  223, 35, 20, 10, 10, Color.RED);
        this.rocketBNRect = new RoundRect(centerX + 50, popUpBd.getY() +  258, 35, 20, 10, 10, Color.RED);

        // Calculate Yes/No offsets
        int centerPopUpX = popUpBd.getX() + (popUpBd.getW()/2);
        int centerPopUpY = popUpBd.getY() + (popUpBd.getH()/2);

        int yesOffsetX = centerPopUpX - 75;
        int yesOffsetY = centerPopUpY - 50;

        int noOffsetX = centerPopUpX + 25;
        int noOffsetY = centerPopUpY - 50;

        this.yesBNRect = new ARect(yesOffsetX, yesOffsetY,58,23, Color.RED);
        this.noBNRect = new ARect(noOffsetX, noOffsetY,42,23, Color.RED);

        // Objective Dialogue
        objective_1 = "At 0700 cross China's border via the Yellow Sea heading towards 35°03'N 118°21\'E. ";

        objective_2 = "Eliminate hostile enemies in route to Linyi's Nuclear Silo and destroy the silo before " +
                      "a nuclear missile is launched.";

        // Load Images
        image = resourceManager.loadImage("Menu/GameMenu.png");
        options_UH60 = resourceManager.loadImage("Menu/Options_UH60.png");
        objective = resourceManager.loadImage("Menu/Objective.png");

        // Create Background Image offset based on Center of Game Resolution and Center of Image
        this.imageOffsetX = (screenWidth /2) - 720;
        this.imageOffsetY = (screenHeight/2) - 450;

        // Set Menu Font
        this.color = new Color(6, 141, 11);
        this.autoFont = new Font("Auto Mission", Font.PLAIN, 18);
        this.stencilFont = new Font("Stencil STD", Font.PLAIN, 18);

        // Load Radio Chatter SFX
        String[] sounds = { "SFX/Radio/Radio_Chatter_0.wav",  "SFX/Radio/Radio_Chatter_1.wav",
                            "SFX/Radio/Radio_Chatter_2.wav",  "SFX/Radio/Radio_Chatter_3.wav",
                            "SFX/Radio/Radio_Chatter_4.wav",  "SFX/Radio/Radio_Chatter_5.wav",
                            "SFX/Radio/Radio_Chatter_6.wav",  "SFX/Radio/Radio_Chatter_7.wav",
                            "SFX/Radio/Radio_Chatter_8.wav",  "SFX/Radio/Radio_Chatter_9.wav",
                            "SFX/Radio/Radio_Chatter_10.wav", "SFX/Radio/Radio_Chatter_11.wav",
                            "SFX/Radio/Radio_Chatter_12.wav", "SFX/Radio/Radio_Chatter_13.wav",
                            "SFX/Radio/Radio_Chatter_14.wav", "SFX/Radio/Radio_Chatter_15.wav",
                            "SFX/Radio/Radio_Chatter_16.wav", "SFX/Radio/Radio_Chatter_17.wav",
                            "SFX/Radio/Radio_Chatter_18.wav", "SFX/Radio/Radio_Chatter_19.wav",
                            "SFX/Radio/Radio_Chatter_20.wav", "SFX/Radio/Radio_Chatter_21.wav",
                            "SFX/Radio/Radio_Chatter_22.wav", "SFX/Radio/Radio_Chatter_23.wav",
                            "SFX/Radio/Radio_Chatter_24.wav", "SFX/Radio/Radio_Chatter_25.wav" };

        this.radioSFX = new Sound[sounds.length];

        for(int i = 0; i < sounds.length; i++) this.radioSFX[i] = resourceManager.loadSound(sounds[i]);
    }

    // Check for State Change
    public String checkForStateChange() { return done?state:null; }

    // Get Name
    public String getName() { return "Menu"; }

    // Current Button Selection Equals
    private boolean currentBtnEquals(int x) { return this.currentBtnSelection == x; }

    // Set Current Button Selection
    private void setBtnSelection(int x) { this.currentBtnSelection = x; }

    // Current Page Selection Equals
    private boolean currentPgEquals(int x) { return this.currentPgSelection == x; }

    // Set Current Page Selection
    private void setPgSelection(int x) { this.currentPgSelection = x; }

    // Current Options Button Selection Equals
    private boolean currentOpBtnEquals(int x) { return this.optionsBtnSelection == x; }

    // Set Current Options Button Selection
    private void setOpBtnSelection(int x) { this.optionsBtnSelection = x; }

    // Set Button Boundary Visible
    private void setBtnBoundaryVisible() {

        if(btnBoundaryVisible) btnBoundaryVisible = false; else btnBoundaryVisible = true;
    }

    // Set Mouse Over Button
    private void setMouseOverBtn(boolean state) {

        this.mouseOverBTN = state;

        if(state) inputManager.setCursor(inputManager.HAND_CURSOR);
        else inputManager.setCursor(inputManager.DEFAULT_CURSOR);
    }

    // Stop
    public void stop() { }

    // Close Sound Managers
    public void closeSoundManagers() { }

} // End of Class.