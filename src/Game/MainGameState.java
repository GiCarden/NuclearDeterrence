package Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import Collision.Collision;
import Collision.ScreenContains;
import Graphics.*;
import Graphics.Sprites.Projectile;
import Graphics.Character;
import Graphics.Transitions.FadeRect;
import Sound.*;
import Input.*;
import State.GameState;
import State.ResourceManager;
import Graphics.Sprites.*;
import TileMap.*;

import javax.sound.sampled.AudioFormat;

/**
 *
 */
public class MainGameState implements GameState {

    // Game Managers
    private SoundManager soundManager;
    private GameResourceManager resourceManager;

    // Tile Maps
    private TileMap roadMap;
    private TileMap billBoardMap;
    private TileMap waterMap;
    private TileMap LScapeMap;
    private TileMap spriteMap;
    private TileMapRenderer renderer;

    // On Screen Rectangle is used to determine if a
    // Sprite is contained on the Screen.
    private ScreenContains rect;

    // Screen Width and Height
    private static int screenWidth;
    private static int screenHeight;

    // In Game Overlay
    private GameOverlay gameOverlay;

    // Transitions
    private FadeRect fadeIn;
    private FadeRect fadeOut;
    private static float fadeOutSpeed;
    private static boolean fadingIn;
    private static boolean fadingOut;

    // Toggle The Sprite's Collision Boundary Visibility
    private boolean collisionBound = false;

    // Game State
    private boolean done;
    private String state;

    /**
        ZONE CONTROL
        Zone_1X/Y...keeps track of zone center location for objective tracking.
        Zone_1_Border...limits the player to that zone Y value.
        Zone_1...is used to tell what zone an enemy sprite is in.
        Zone_1_NumEnemies...equals the amount of enemies a zone has.
        Zone 3 has two objectives
     */
    private static final int zone_1X = 2864;
    private static final int zone_1Y = 8181;
    private static final int zone_2X = 509;
    private static final int zone_2Y = 6307;
    private static final int zone_3X = 1994;
    private static final int zone_3Y = 3626;
    private static final int zone_4X = 2264;
    private static final int zone_4Y = 80;
    private static final int zone_1_Border = 7367;
    private static final int zone_2_Border = 5448;
    private static final int zone_3_Border = 2900;
    private static final int zone_1 = 1;
    private static final int zone_2 = 2;
    private static final int zone_3 = 3;
    private static final int zone_4 = 4;
    private static int zone_1_NumEnemies = 0;
    private static int zone_2_NumEnemies = 0;
    private static int zone_3_NumEnemies = 0;
    private int currentZone = 1;

    // Input
    private GameAction exit;
    private GameAction godMode;
    private GameAction strafeLeft;
    private GameAction strafeRight;
    private GameAction rotateLeft;
    private GameAction rotateRight;
    private GameAction shootBullet;
    private GameAction shootRocket;
    private GameAction moveForward;
    private GameAction moveBackward;
    private GameAction colBoundVisible;

    public static boolean godMode_Enabled;

    // Sound Manager
    private SoundManager alarmSoundManager;
    private SoundManager uhLoopingSoundManager;
    private SoundManager ohLoopingSoundManager;
    private SoundManager aaLoopingSoundManager;
    private SoundManager aaTankLoopingSoundManager;

    // Sounds
    private Sound uhSFX;
    private Sound ohSFX;
    private Sound aaSFX;
    private Sound doorSFX;
    private Sound alarmSFX;
    private Sound launchSFX;
    private Sound explosionSFX;
    private Sound dyingSFX;
    private Sound rocketSFX;
    private Sound uhBulletSFX;
    private Sound ohBulletSFX;

    // Keep Track of SFX that play once
    private boolean alarmSFXIsPlaying;

    // Keep track if the sounds have been played for the first time
    // then the thread is paused and un-paused.
    private boolean uhSFXInit;
    private boolean ohSFXInit;
    private boolean aaSFXInit;
    private boolean aaTankSFXInit;

    // Uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT = new AudioFormat(44100,16,1,
            true,false);


    // Main Game State
    public MainGameState(SoundManager soundManager) {

        godMode = new GameAction("godMode", GameAction.DETECT_INITAL_PRESS_ONLY);
        strafeLeft = new GameAction("moveLeft", GameAction.NORMAL);
        strafeRight = new GameAction("moveRight", GameAction.NORMAL);
        rotateLeft = new GameAction("rotateLeft", GameAction.NORMAL);
        rotateRight = new GameAction("rotateRight", GameAction.NORMAL);
        shootBullet = new GameAction("bullet", GameAction.NORMAL);
        shootRocket = new GameAction("rocket", GameAction.NORMAL);
        moveForward = new GameAction("moveUp", GameAction.NORMAL);
        moveBackward = new GameAction("moveDown", GameAction.NORMAL);
        colBoundVisible = new GameAction("colBoundVisible", GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit", GameAction.DETECT_INITAL_PRESS_ONLY);

        renderer = new TileMapRenderer();

        this.fadeIn = new FadeRect(0, 0, GameManager.getScreenWidth(),
                GameManager.getScreenHeight(), 1, Color.BLACK);
        this.fadeOut = new FadeRect(0, 0, GameManager.getScreenWidth(),
                GameManager.getScreenHeight(), 0, Color.BLACK);

        this.soundManager = soundManager;
    }

    // Load Resources
    public void loadResources(ResourceManager resManager, int screenWidth, int screenHeight) {

        resourceManager = (GameResourceManager)resManager;

        resourceManager.loadResources(resManager);

        // Temporary List to Load Billboard Maps
        TileMap campMap;

        // Load first map
        roadMap = resourceManager.loadNextMap("Resources/Maps/roadMap",
                       resourceManager.getRoads(), resourceManager);
        billBoardMap = resourceManager.loadNextMap("Resources/Maps/billBoardMap",
                       resourceManager.getBillBoards(), resourceManager);
        campMap = resourceManager.loadNextMap("Resources/Maps/campMap",
                       resourceManager.getCamp(), resourceManager);
        waterMap = resourceManager.loadNextMap("Resources/Maps/WaterMap",
                       resourceManager.getWater(), resourceManager);
        LScapeMap = resourceManager.loadNextMap("Resources/Maps/LScapeMap",
                       resourceManager.getLScape(), resourceManager);
        spriteMap = resourceManager.loadNextMap("Resources/Maps/spriteMap",
                       resourceManager.getSprites(), resourceManager);

        // Combine Billboards into one list
        Iterator i = campMap.getBillBoards();

        while(i.hasNext()) {

            Billboard billboards = (Billboard)i.next();

            // Add to Map Sprite List
            billBoardMap.addBillboard(billboards);
        }

        // On Screen Rectangle
        rect = new ScreenContains(screenWidth-1,screenHeight-1, screenWidth, screenHeight,
                waterMap.getWidth(), waterMap.getHeight());

        // Set the Rectangle centered over the Player
        rect.setRectPosition(spriteMap.getPlayer());

        // Set Screen Width and Height
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        // Load Game Overlay
        String[] files = { "Player_Stats.png", "Direction.png" };
        gameOverlay = new GameOverlay(files, screenWidth, screenHeight);
        gameOverlay.arrow.rotate(-90);
        gameOverlay.setName("Brian Murphy");

        // Load sounds
        uhSFX = resourceManager.loadSound("SFX/Helicopters/UH60/UH60_Movement.wav");
        ohSFX = resourceManager.loadSound("SFX/Helicopters/OH58D/OH58D_Movement.wav");
        aaSFX = resourceManager.loadSound("SFX/AATurret.wav");
        launchSFX = resourceManager.loadSound("SFX/Launch.wav");
        explosionSFX = resourceManager.loadSound("SFX/Explosion.wav");
        alarmSFX = resourceManager.loadSound("Music/NuclearAlarm.wav");
        doorSFX = resourceManager.loadSound("SFX/SiloDoor.wav");
        dyingSFX = resourceManager.loadSound("SFX/Explosion.wav");
        rocketSFX = resourceManager.loadSound("SFX/Rocket.wav");
        uhBulletSFX = resourceManager.loadSound("SFX/Helicopters/UH60/UH60_Bullet.wav");
        ohBulletSFX = resourceManager.loadSound("SFX/Helicopters/OH58D/OH58D_Bullet.wav");

        // Initialize Looping Sound Managers
        alarmSoundManager = new SoundManager(PLAYBACK_FORMAT);
        uhLoopingSoundManager = new SoundManager(PLAYBACK_FORMAT);
        ohLoopingSoundManager = new SoundManager(PLAYBACK_FORMAT);
        aaLoopingSoundManager = new SoundManager(PLAYBACK_FORMAT);
        aaTankLoopingSoundManager = new SoundManager(PLAYBACK_FORMAT);

        // Only allow looping sounds to be initialized once
        uhSFXInit = false;
        ohSFXInit = false;
        aaSFXInit = false;
        aaTankSFXInit = false;

        // Allow only one Sound for multiple objects of same type
        alarmSFXIsPlaying = false;
    }

    // Start
    public void start(InputManager inputManager) {

        // Map Key Inputs
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(godMode, KeyEvent.VK_G);
        inputManager.mapToKey(rotateLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(rotateRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(strafeLeft, KeyEvent.VK_A);
        inputManager.mapToKey(strafeRight, KeyEvent.VK_D);
        inputManager.mapToKey(shootBullet, KeyEvent.VK_SPACE);
        inputManager.mapToKey(shootRocket, KeyEvent.VK_R);
        inputManager.mapToKey(moveForward, KeyEvent.VK_UP);
        inputManager.mapToKey(moveBackward, KeyEvent.VK_DOWN);
        inputManager.mapToKey(moveForward, KeyEvent.VK_W);
        inputManager.mapToKey(moveBackward, KeyEvent.VK_S);
        inputManager.mapToKey(colBoundVisible, KeyEvent.VK_P);

        // Map Mouse Inputs
        inputManager.mapToMouse(rotateLeft, inputManager.MOUSE_MOVE_LEFT);
        inputManager.mapToMouse(rotateRight, inputManager.MOUSE_MOVE_RIGHT);
        inputManager.mapToMouse(shootBullet, inputManager.MOUSE_BUTTON_1);
        inputManager.mapToMouse(shootRocket, inputManager.MOUSE_BUTTON_3);

        inputManager.setCursor(inputManager.INVISIBLE_CURSOR);

        godMode_Enabled = false;

        done = false;

        // Transitions
        this.fadingIn = true;
        this.fadingOut = false;
        fadeOutSpeed = 0.02f;
        this.fadeIn.setAlpha(1);
        this.fadeOut.setAlpha(0);

        // Set Global variables for the game menu
        if(!GameManager.gameHasStarted()) GameManager.setGameStarted(true);

        // Change the Game Menu button for "Play Game"
        if(GameManager.getGameBN().equals("Play Game")) {

            GameManager.setGameBN("Resume Game");

            // Reset Game Menu button "Play Game" Offset for "Resume Game"
            GameManager.setGameRectX(-12);
            GameManager.setGameRectW(27);
        }

        // If New Game then Reset Level
        if(GameManager.newGame()) {

            resetGame();
            GameManager.setNewGame(false);
        }

        // Game Menu Radio SFX is controlled by a timer
        // Set the radio back to not playing
        GameManager.setRadioSFXIsPlaying(false);

        // Play Alarm if paused
        if(alarmSFXIsPlaying) alarmSoundManager.setPaused(false);

        // UnPause Player SFX
        if(!uhSFXInit) {

            uhLoopingSoundManager.play(uhSFX, null, true);
            uhSFXInit = true;
        }
        else playUhSFX();
    }

    // Stop
    public void stop() {

        // Pause Player SFX Manager
        pauseUhSFX();

        // If Alarm is playing, pause
        alarmSoundManager.setPaused(true);

        // Pause Enemy SFX Managers
        pauseOhSFX();
        pauseAASFX();
        pauseAATankSFX();
    }

    // Draw
    public void draw(Graphics2D g, int screenWidth, int screenHeight) {

        // Draw the Map and Sprites
        renderer.draw(resourceManager.gc, g, spriteMap, roadMap, billBoardMap, LScapeMap, waterMap,
                                            screenWidth, screenHeight, collisionBound, rect, resourceManager);

        // Draw the Game Overlay
        gameOverlay.draw(g, spriteMap.getPlayer());

        // Draw Fading Transitions
        if(fadingIn) fadeIn.draw(g);
        if(fadingOut) fadeOut.draw(g);
    }

    // Check Input
    private void checkInput() {

        // Get the Map Player Sprite
        Helicopter player = (Helicopter)spriteMap.getPlayer();

        // If Exit then go to Game Menu
        if(exit.isPressed()) {

            state = "Menu";
            fadingOut = true;
            return;
        }

        // Toggle God Mode
        if(godMode.isPressed()) {

            if(godMode_Enabled) godMode_Enabled = false; else godMode_Enabled = true;
        }

        // As long as the player is alive calculate movement
        if(player.isAlive()) {

            // Rotate
            if(rotateLeft.isPressed()) player.rotate(resourceManager.gc,-1);
            else if(rotateRight.isPressed()) player.rotate(resourceManager.gc,1);

            // Move
            if(moveForward.isPressed()) player.moveForward(resourceManager.gc);
            else if(moveBackward.isPressed()) player.moveBackward(resourceManager.gc);
            else if(strafeLeft.isPressed()) player.strafeLeft(resourceManager.gc);
            else if(strafeRight.isPressed()) player.strafeRight(resourceManager.gc);
            else {

                player.stopMovement();
                player.hover(resourceManager.gc);
            }

            // Player Shoots Bullet
            if (shootBullet.isPressed() && !player.bulletAmmoIsEmpty())
                resourceManager.addProjectile(player, resourceManager.addBullet(),0, soundManager, uhBulletSFX);

            // Player Shoots Rocket
            if (shootRocket.isPressed() && !player.rocketAmmoIsEmpty())
                resourceManager.addProjectile(player, resourceManager.addRocket(),0, soundManager, rocketSFX);

        }

        // Enable/Disable Collision Boundary Visibility (P Key)
        if(colBoundVisible.isPressed()) { setColBoundVisible(); }
    }

    // Updates Animation, position, and velocity of all Sprites in the current map.
    public void update(long elapsedTime) {

        // Get the Player from the Map
        Helicopter player = (Helicopter) spriteMap.getPlayer();

        // Update Transitions
        if (fadingIn) {

            fadeIn.update(-0.02f);

            // Check if fade is complete
            if (fadeIn.getAlpha() == 0) fadingIn = false;

            player.anim.update(elapsedTime);

            return;

        } else if (fadingOut) {

            fadeOut.update(fadeOutSpeed);

            // Check if fade is complete
            if (fadeOut.getAlpha() == 1) { done = true; }

            player.anim.update(elapsedTime);

            return;
        }

        // Update Main State
        // If Player is dead start map over
        if (player.getState() == Character.STATE_DEAD) { resetGame(); return; }

        // Get Keyboard/Mouse Input
        checkInput();

        // Update Player Position and Check Collision
        updatePlayer(player, elapsedTime);

        // Update Final Position and Apply Elapsed Time
        player.update(elapsedTime);

        // Update On Screen Rectangle Position
        rect.setRectPosition(player);

        // Turn Arrow towards objective
        if (currentZoneEquals(zone_1)) gameOverlay.arrow.turnArrowTowards(player, zone_1X, zone_1Y);
        else if (currentZoneEquals(zone_2)) gameOverlay.arrow.turnArrowTowards(player, zone_2X, zone_2Y);
        else if (currentZoneEquals(zone_3)) gameOverlay.arrow.turnArrowTowards(player, zone_3X, zone_3Y);
        else if (currentZoneEquals(zone_4)) gameOverlay.arrow.turnArrowTowards(player, zone_4X, zone_4Y);

        // Update All Other Sprite's
        // Keep track of how many enemies are on screen to update sound managers
        // If there is at least 1 enemy of a specific type then make sure a sound is playing
        int num_oh = 0;
        int num_aa = 0;
        int num_ag = 0;

        Iterator i = spriteMap.getSprites();

        // Get each Sprite from the List
        while (i.hasNext()) {

            Character sprite = (Character) i.next();

            // Set Sprite is on screen by calculating the Y value with the top of the screen
            sprite.setIsOnScreen(rect.contains(sprite));

            // If the Sprite is on screen then update Ai and Check Collision
            if (sprite.isOnScreen()) {

                // If Character is Alive do Normal Update
                if (sprite.isAlive()) {

                    // Update Ai
                    updateAi(sprite);

                    // Update Character's Position and Check Collision
                    updateCharacter(sprite, elapsedTime);

                    if (resourceManager.isType(sprite, resourceManager.OH)) num_oh++;

                    if (resourceManager.isType(sprite, resourceManager.AA)) {

                        AATank aaTank = (AATank) sprite;
                        if(aaTank.isTurretRotating()) num_aa++;
                    }

                    if (resourceManager.isType(sprite, resourceManager.AG)) {

                        AAGun aaGun = (AAGun) sprite;
                        if(aaGun.isTurretRotating()) num_ag++;
                    }
                }

                // Update Final Position and Animation by applying Elapsed Time
                sprite.update(elapsedTime);
            }

            // If Sprite is dead remove it from the list
            if (sprite.getState() == sprite.STATE_DEAD) i.remove();
        }

        // Play at least one sound for each type of enemy on the screen
        if (num_oh > 0 && !ohSFXInit) {

            ohLoopingSoundManager.play(ohSFX,null,true);
            ohSFXInit = true;
        } else if (num_oh > 0 && ohSFXInit) playOhSFX();
        else pauseOhSFX();

        if(num_aa > 0 && !aaSFXInit) {

            aaLoopingSoundManager.play(aaSFX,null,true);
            aaSFXInit = true;
        } else if (num_aa > 0 && aaSFXInit) playAASFX();
        else pauseAASFX();

        if (num_ag > 0 && !aaTankSFXInit) {

            aaTankLoopingSoundManager.play(aaSFX,null,true);
            aaTankSFXInit = true;
        } else if (num_ag > 0 && aaTankSFXInit) playAATankSFX();
        else pauseAATankSFX();

        // Update Player Projectiles
        if (resourceManager.playerHasProjectiles()) {

            i = resourceManager.getPlayerProjectiles();

            // Update Bullets
            updateProjectiles(i, elapsedTime);
        }

        // Update Enemies Projectiles
        if (resourceManager.enemyHasProjectiles()) {

            i = resourceManager.getEnemyProjectiles();

            // Update Bullets
            updateProjectiles(i, elapsedTime);
        }

        // Update Zone
        updateZone();

        // Update Billboard On Screen
        i = billBoardMap.getBillBoards();

        while (i.hasNext()) {

            Billboard billboard = (Billboard) i.next();
            if (rect.contains(billboard)) billboard.setOnScreen(true);
            else billboard.setOnScreen(false);
        }

        // Update Silo
        if (currentZoneEquals(zone_4)) {

            if (!spriteMap.silo.isOnScreen()) {

                spriteMap.silo.setIsOnScreen(rect.contains(spriteMap.silo));

                if (spriteMap.silo.isOnScreen()) {

                    spriteMap.silo.initSequence();
                    soundManager.play(doorSFX);
                }
            } else {

                // Check Player Projectiles collision with the Silo
                if (spriteMap.silo.getHealth() > 0 && !spriteMap.silo.hasLaunched() &&
                        resourceManager.playerHasProjectiles()) {

                    i = resourceManager.getPlayerProjectiles();

                    while (i.hasNext()) {

                        Projectile projectile = (Projectile) i.next();

                        if (Collision.isCollision(projectile, spriteMap.silo))
                            projectile.setState(projectile.STATE_DYING);
                    }
                }

                // Check when to play the Alarm SFX
                if(spriteMap.silo.isCounting()) {

                    if(!alarmSFXIsPlaying) {

                        alarmSoundManager.play(alarmSFX);
                        alarmSFXIsPlaying = true;
                    }else if(alarmSoundManager.isPaused()) alarmSoundManager.setPaused(false);
                }

                // Check if Silo is dead and end game
                if (spriteMap.silo.isDead() && !fadingOut) {

                    state = "EndGame";
                    fadeOutSpeed = 0.007f;
                    fadingOut = true;

                    if (spriteMap.silo.hasLaunched()) GameManager.setGameWon(false);
                    else GameManager.setGameWon(true);
                }

                // Silo Update
                spriteMap.silo.update(elapsedTime, player, soundManager, launchSFX, dyingSFX);
            }

            // Update Shaking
            if (GameManager.shake.shaking()) GameManager.shake.update();
        }
    }

    // Updates the Map Characters, and Check Collisions
    private void updatePlayer(Character character, long elapsedTime) {

        // Check if the Player is on the Map and set New X/Y Location
        // Change X
        float dx = character.getVelocityX();
        float oldX = character.getX();
        float newX = oldX + dx * elapsedTime;

        // If the Player goes off of the Map Horizontally then set Velocity X to Zero, else set new X
        if (newX > (waterMap.getWidth()-3)*64 || newX < -50) character.setVelocityX(0);
        else character.setX(newX);

        // Change Y
        float dy = character.getVelocityY();
        float oldY = character.getY();
        float newY = oldY + dy * elapsedTime;

        // If the Player goes off of the Map Vertically then set Velocity Y to Zero
        // else if the player leaves the allowed zone then set Velocity Y to Zero
        // else allow the player to change Y position
        if(newY > (waterMap.getHeight()-3)*64  || newY < -50) character.setVelocityY(0);
        else if((currentZoneEquals(zone_1) && newY < zone_1_Border) ||
                (currentZoneEquals(zone_2) && newY < zone_2_Border) ||
                (currentZoneEquals(zone_3) && newY < zone_3_Border)) character.setVelocityY(0);
        else character.setY(newY);

        // Check Collision
        Collision.characterCollision(character, elapsedTime, spriteMap, soundManager, dyingSFX);
    }

    // Updates the Map Characters, and Check Collisions
    private void updateCharacter(Character character, long elapsedTime) {

        // Check if the Character is on the Map and set New X/Y Location
        mapContains(character, elapsedTime);

        // Check Character Collision
        if(character.isOnScreen())
            Collision.characterCollision(character, elapsedTime, spriteMap, soundManager, dyingSFX);
    }

    // Update Projectiles
    private void updateProjectiles(Iterator i, long elapsedTime) {

        while(i.hasNext()) {

            Projectile projectile = (Projectile)i.next();

            // Only update Position if the projectile is not dying
            if(projectile.isAlive()) {

                // Change X
                float dx = projectile.getVelocityX();
                float oldX = projectile.getX();
                float newX = oldX + dx * elapsedTime;

                projectile.setX(newX);

                // Change Y
                float dy = projectile.getVelocityY();
                float oldY = projectile.getY();
                float newY = oldY + dy * elapsedTime;

                projectile.setY(newY);
            }
            else projectile.stopMovement();

            // Set Projectile is on screen by calculating the X/Y value with the screen Width/Height
            projectile.setIsOnScreen(rect.contains(projectile));

            // If the Projectile is on screen then Check Collision
            if(!projectile.isDying() && projectile.isOnScreen()) {

                // Check Projectile Collision
                if(Collision.projectileCollision(projectile, spriteMap, soundManager, dyingSFX))
                    projectile.setState(projectile.STATE_DYING);
            }

            // Update Projectile's position even if it is not on screen
            if(!projectile.isDead()) projectile.update(elapsedTime);

            // If Projectile reached Max Life then remove from the List
            if(projectile.isDead()) i.remove();
        }
    }

    // Update Ai
    private void updateAi(Character character) {

        // Get the Player from the Map
        Helicopter player = (Helicopter)spriteMap.getPlayer();

        // Get screen location of the Player
        float playerX = player.getX() + player.getWidth()  / 2;
        float playerY = player.getY() + player.getHeight() / 2;

        if(character.isAlive()) {

            // If character is a Helicopter
            if(character instanceof Helicopter) {

                Helicopter helicopter = (Helicopter)character;

                // If Player is Alive
                if(player.isAlive()) {

                    // Get screen location of the Player
                    float helicopterX = helicopter.getX() + helicopter.getWidth()   / 2;
                    float helicopterY = helicopter.getY() + helicopter.getHeight()  / 2;

                    // Turn towards the player
                    helicopter.turnTowards(resourceManager.gc, playerX, playerY, helicopterX, helicopterY);

                    // Move towards the player
                    helicopter.moveForward(resourceManager.gc);

                    // If in range of the player then attack by firing a bullet
                    if(helicopter.isInRange(helicopterX, helicopterY, playerX, playerY, screenWidth/2, 68)) {

                        if(!helicopter.bulletAmmoIsEmpty())
                            resourceManager.addProjectile(helicopter, resourceManager.addBullet(), 0, soundManager,
                                                                                                        ohBulletSFX);
                    }

                    // If in range of the player then attack by firing a rocket
                    if(helicopter.isInRange(helicopterX, helicopterY, playerX, playerY, (screenWidth/2)+100, 68)) {

                        if(!helicopter.rocketAmmoIsEmpty())
                            resourceManager.addProjectile(helicopter, resourceManager.addRocket(), 0, soundManager,
                                                                                                         rocketSFX);
                    }
                }
            }
        }

        // If Character is a AATank
        if(character instanceof AATank) {

            AATank aaTank = (AATank)character;

            float aaTankX = aaTank.getX() + aaTank.getWidth() / 2;
            float aaTankY = aaTank.getY() + aaTank.getHeight() / 2;

            // Turn towards the player
            aaTank.turnTurretTowards(resourceManager.gc, playerX, playerY, aaTankX, aaTankY);

            // If in range of the player then attack by firing a rocket
            if(aaTank.isInRange(aaTankX, aaTankY, playerX, playerY,screenWidth/2 - 100,68)) {

                if(!aaTank.rocketAmmoIsEmpty())
                    resourceManager.addProjectile(aaTank, resourceManager.addRocket(),0, soundManager, rocketSFX);
            }
        }

        // If Character is a AAGun
        if(character instanceof AAGun) {

            AAGun aaGun = (AAGun)character;

            float aaGunX = aaGun.getX() + aaGun.getWidth() / 2;
            float aaGunY = aaGun.getY() + aaGun.getHeight() / 2;

            // Turn towards the player
            aaGun.turnTowards(resourceManager.gc, playerX, playerY, aaGunX, aaGunY);

            // If in range of the player then attack by firing a bullet
            if(aaGun.isInRange(aaGunX, aaGunY, playerX, playerY, screenWidth/2 - 100, 68)) {

                if(!aaGun.ammoIsEmpty()) {

                    resourceManager.addProjectile(aaGun, resourceManager.addBullet(),8, soundManager, uhBulletSFX);
                    resourceManager.addProjectile(aaGun, resourceManager.addBullet(),-8, soundManager, uhBulletSFX);
                }
            }
        }
    }

    // Update Zone
    private void updateZone() {

        Sprite player = spriteMap.getPlayer();

        if(currentZoneEquals(zone_1)) {

            if(zone_1_Cleared()) {

                player.setHealth(100);
                currentZone ++;
            }
        } else if(currentZoneEquals(zone_2)) {

            if(zone_2_Cleared()) {

                player.setHealth(100);
                currentZone ++;
            }
        } else if(currentZoneEquals(zone_3)) {

            if(zone_3_Cleared()) {

                player.setHealth(100);
                currentZone ++;
            }
        }
    }

    // Character Is on the Map
    public void mapContains(Character character, long elapsedTime) {

        // Check if the Sprite's location is on the map and if false then set Velocity to Zero.
        // Change X
        float dx = character.getVelocityX();
        float oldX = character.getX();
        float newX = oldX + dx * elapsedTime;

        // If the Sprite goes off of the Map Horizontally then set Velocity X to Zero, else set new X
        if (newX > (waterMap.getWidth()-3)*64 || newX < -50) character.setVelocityX(0);
        else character.setX(newX);

        // Change Y
        float dy = character.getVelocityY();
        float oldY = character.getY();
        float newY = oldY + dy * elapsedTime;

        // If the Sprite goes off of the Map Vertically then set Velocity Y to Zero, else set new Y
        if (newY > (waterMap.getHeight()-3)*64  || newY < -50) character.setVelocityY(0);
        else character.setY(newY);
    }

    // Set Collision Boundary Visibility
    private void setColBoundVisible() {

        if(collisionBound) this.collisionBound = false; else this.collisionBound = true;
    }

    // Get Name
    public String getName() { return "Main"; }

    // Check for State Change
    public String checkForStateChange() { return done?state:null; }

    // Reset Game
    public void resetGame() {

        // Reset Zone Enemies
        zone_1_NumEnemies = 0;
        zone_2_NumEnemies = 0;
        zone_3_NumEnemies = 0;

        // RESET PROJECTILES
        // Remove Map Sprite Projectiles
        Iterator i = resourceManager.getEnemyProjectiles();

        while(i.hasNext()) {

            i = resourceManager.getEnemyProjectiles();
            Projectile projectile = (Projectile)i.next();
            resourceManager.removeEnemyProjectile(projectile);
        }

        // Remove Player Projectiles
        i = resourceManager.getPlayerProjectiles();

        while(i.hasNext()) {

            i = resourceManager.getPlayerProjectiles();
            Projectile projectile = (Projectile)i.next();
            resourceManager.removePlayerProjectile(projectile);
        }

        // RESET PLAYER
        Helicopter player = (Helicopter)spriteMap.getPlayer();

        // Reset Player State to Normal
        player.setState(Character.STATE_NORMAL);

        // Reset Player Animation
        player.resetAnim();

        // Reset Player to Starting Angle
        player.resetImage(resourceManager.gc,-90);

        // Reset Player's Health
        player.setHealth(100);

        //Reset Rockets
        player.setRockets(8);

        // RELOAD MAPS
        spriteMap = resourceManager.reloadMap("Resources/Maps/spriteMap", resourceManager.getSprites(), resourceManager);

        // Reset game over
        GameManager.setGameWon(false);

        // Reset Silo
        spriteMap.getSilo().reset();

        // Update Zone
        currentZone = 1;

        // Reset Sound
        alarmSFXIsPlaying = false;

        // Reset Billboards to show as on screen to allow initial draw of U.S. Base
        i = spriteMap.getBillBoards();

        while(i.hasNext()) {

            Billboard billBoard = (Billboard)i.next();
            billBoard.setOnScreen(true);
        }
    }

    // Get Zone_1
    public static int getZone_1() { return zone_1; }

    // Get Zone_2
    public static int getZone_2() { return zone_2; }

    // Get Zone_2
    public static int getZone_3() { return zone_3; }

    // Set Zone_1_NumEnemies
    public static void setZone_1_NumEnemies(int x) { zone_1_NumEnemies += x; }

    // Set Zone_2_NumEnemies
    public static void setZone_2_NumEnemies(int x) { zone_2_NumEnemies += x; }

    // Set Zone_3_NumEnemies
    public static void setZone_3_NumEnemies(int x) { zone_3_NumEnemies += x; }

    // Zone 1 Cleared
    private boolean zone_1_Cleared() { return zone_1_NumEnemies == 0; }

    // Zone 2 Cleared
    private boolean zone_2_Cleared() { return zone_2_NumEnemies == 0; }

    // Zone 3 Cleared
    private boolean zone_3_Cleared() { return zone_3_NumEnemies == 0; }

    // Current Zone Equals
    private boolean currentZoneEquals(int zone) { return currentZone == zone; }


    // SOUND FX
    // Play
    public void playUhSFX() { uhLoopingSoundManager.setPaused(false); }
    public void playOhSFX() { ohLoopingSoundManager.setPaused(false); }
    public void playAASFX() { aaLoopingSoundManager.setPaused(false); }
    public void playAATankSFX() { aaTankLoopingSoundManager.setPaused(false); }

    // Pause
    public void pauseUhSFX() { uhLoopingSoundManager.setPaused(true); }
    public void pauseOhSFX() { ohLoopingSoundManager.setPaused(true); }
    public void pauseAASFX() { aaLoopingSoundManager.setPaused(true); }
    public void pauseAATankSFX() { aaTankLoopingSoundManager.setPaused(true); }

    // Close Sound Managers
    public void closeSoundManagers() {

        alarmSoundManager.close();
        uhLoopingSoundManager.close();
        ohLoopingSoundManager.close();
        aaLoopingSoundManager.close();
        aaTankLoopingSoundManager.close();
    }

} // End of Class.