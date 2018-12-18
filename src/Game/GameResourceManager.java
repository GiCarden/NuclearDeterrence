package Game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import Graphics.Shapes.Circle;
import Collision.DualCircles;
import Graphics.*;
import Graphics.Sprites.Projectile;
import Graphics.Sprites.*;
import Sound.Sound;
import Sound.SoundManager;
import State.ResourceManager;
import TileMap.*;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  The ResourceManager class loads and manages tile Images and "host" Sprites used in the game.
 *  This class also instantiates new projectiles.
 */
public class GameResourceManager extends ResourceManager {

    private ArrayList roadTiles;
    private ArrayList campTiles;
    private ArrayList billBoards;
    private ArrayList waterTiles;
    private ArrayList LScapeTiles;
    private int currentMap;

    // Determines which map to load
    private static final int camp = 5;
    private static final int sprites = 4;
    private static final int roads = 3;
    private static final int billBoard = 2;
    private static final int water = 1;
    private static final int LScape = 0;

    // Type of Sprites
    public static final int UH = 0;
    public static final int OH = 1;
    public static final int AA = 2;
    public static final int AG = 3;

    // Player Sprite
    private Sprite playerSprite;

    // Silo Sprite
    private Silo silo;

    // Projectiles
    private LinkedList enemyProjectiles;
    private LinkedList playerProjectiles;

    // Type
    public static int bullet = 0;
    public static int rocket = 1;

    // Bullet
    private Image bulletImg;
    private Image[] sparksImgs;
    private int bulletW;
    private int bulletH;

    // Rocket
    private Image rocketImg;
    private Image[] rocketExp;
    private int rocketW;
    private int rocketH;
    private Image OZRocketImg;
    private int ozRocketW;
    private int ozRocketH;

    // Creates a new ResourceManager with the specified GraphicsConfiguration
    public GameResourceManager(GraphicsConfiguration gc, SoundManager soundManager) {

        super(gc, soundManager);
        this.playerProjectiles = new LinkedList();
        this.enemyProjectiles = new LinkedList();
    }

    // Load Resources
    public void loadResources(ResourceManager resManager) {

        // LOAD PROJECTILES
        // Load Projectile Images
        this.bulletImg = loadImage("Projectiles/Bullet.png");
        this.rocketImg = loadImage("Projectiles/Rocket.png");
        this.OZRocketImg = loadImage("Projectiles/OZ_Rocket.png");

        // Load Sparks Animation Images
        this.sparksImgs = new Image[1];

        for(int i = 0; i < sparksImgs.length; i++) {

            String file = "Projectiles/Sparks/Sparks_" + i + ".png";
            this.sparksImgs[i] = loadImage(file);
        }

        // Rocket Explosion
        this.rocketExp = new Image[20];

        for(int i = 0; i < rocketExp.length; i++) {

            String file = "Projectiles/Explosion/Rocket_Exp__" + i + ".png";
            this.rocketExp[i] = loadImage(file);
        }

        // Store Projectile Width and Height
        this.bulletW = bulletImg.getWidth(null);
        this.bulletH = bulletImg.getHeight(null);
        this.rocketW = rocketImg.getWidth(null);
        this.rocketH = rocketImg.getHeight(null);
        this.ozRocketW = OZRocketImg.getWidth(null);
        this.ozRocketH = OZRocketImg.getHeight(null);

        // LOAD TILE IMAGES
        // LandScape Images
        loadTileImages(LScape);

        // Water Images
        loadTileImages(water);

        // Billboard Images
        loadTileImages(billBoard);

        // Road Images
        loadTileImages(roads);

        // Road Images
        loadTileImages(camp);

        // Load the Player
        loadPlayerSprite(resManager);

        // Load the silo
        silo = silo(resManager);
    }

    // Load Next Map
    public TileMap loadNextMap(String filePath, int mapIndex, ResourceManager resManager) {

        TileMap map = null;

        while(map == null) {

            currentMap++;

            try {

                map = loadMap(filePath + currentMap + ".txt", mapIndex, resManager);
            } catch (IOException ex) {

                if(currentMap == 1) {

                    // No maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }
        return map;
    }

    // Reload Map
    public TileMap reloadMap(String filePath, int mapIndex, ResourceManager resManager) {

        try {

            return loadMap(filePath + currentMap + ".txt", mapIndex, resManager);
        } catch (IOException ex) {

            ex.printStackTrace();
            return null;
        }
    }

    // Load Map
    private TileMap loadMap(String filename, int mapIndex, ResourceManager resManager) throws IOException {

        ArrayList lines = new ArrayList();

        int width  = 0;
        int height = 0;

        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(filename);

        if(url == null) { throw new IOException("No such map: " + filename); }

        // Read every line in the text file into the list
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        while (true) {

            String line = reader.readLine();

            // no more lines to read
            if(line == null) {

                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {

                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // Parse the lines to create a TileEngine
        height = lines.size();

        /*
            Instantiate temporary list in order to
            separate ground sprites from air sprites so
            that when they are added to the master list
            the sprites will be drawn in order for
            proper layering.
         */
        ArrayList airUnits = new ArrayList();
        ArrayList groundUnits = new ArrayList();

        // Temp Tile Map to Return
        TileMap newMap = new TileMap(width, height);

        for(int y = 0; y < height; y++) {

            String line = (String)lines.get(y);

            for(int x = 0; x < line.length(); x++) {

                char ch = line.charAt(x);

                // Check if the char represents tile A, B, C etc.
                int tile = ch - 'A';

                // If loading Road Tiles
                if(mapIndex == roads) {

                    if(tile >= 0 && tile < roadTiles.size()) {

                        newMap.setTile(x, y, (Image)roadTiles.get(tile));
                    }
                }

                // If Loading Water Tiles
                if(mapIndex == water) {

                    if(tile >= 0 && tile < waterTiles.size()) {

                        newMap.setTile(x, y, (Image)waterTiles.get(tile));
                    }
                }

                // If Loading Land Scape Tiles
                if(mapIndex == LScape) {

                    if(tile >= 0 && tile < LScapeTiles.size()) {

                        newMap.setTile(x, y, (Image)LScapeTiles.get(tile));
                    }
                }

                // If Loading Sprites
                if(mapIndex == sprites) {

                    if (ch == '1') {

                        Sprite sprite = generateSprite(1, x, y, resManager);
                        if(sprite != null) airUnits.add(sprite);
                    }

                    if (ch == '2') {

                        Sprite sprite = generateSprite(2, x, y, resManager);
                        if(sprite != null) groundUnits.add(sprite);
                    }

                    if (ch == '3') {

                        Sprite sprite = generateSprite(3, x, y, resManager);
                        if(sprite != null) groundUnits.add(sprite);
                    }
                }

                // If Loading Billboards
                if(mapIndex == billBoard)
                {
                    if(ch >='A' && ch <= 'Z') {

                        String file = "Billboards/billboard_" + ch + ".png";
                        Billboard billBoard = loadBillBoard(file, x, y);
                        if(billBoard != null) { newMap.addBillboard(billBoard); }
                    }
                }

                // If Loading US Camp
                if(mapIndex == camp) {

                    if(ch >='A' && ch <= 'Z') {

                        String file = "Billboards/Camp/billboard_" + ch + ".png";
                        Billboard billBoard = loadBillBoard(file, x, y);
                        if(billBoard != null) { newMap.addBillboard(billBoard); }
                    }
                }
            }
        }

        // Consolidate both Sprite List to the Map Sprite List
        // Get Ground Units
        Iterator i = groundUnits.iterator();

        while(i.hasNext()) {

            Sprite sprite = (Sprite)i.next();
            // Add to Map Sprite List
            newMap.addSprite(sprite);
        }

        // Get Air Units
        i = airUnits.iterator();

        while(i.hasNext()) {

            Sprite sprite = (Sprite)i.next();

            // Add to Map Sprite List
            newMap.addSprite(sprite);
        }

        // Add the player to the map at the U.S. Camp
        playerSprite.setX(1390);
        playerSprite.setY(12535);

        // Add the player to the map at the China Border
        //playerSprite.setX(1390);
        //playerSprite.setY(2000);

        newMap.setPlayer(playerSprite);

        // Add the Silo to the map
        silo.setX(2200);
        silo.setY(32);
        newMap.setSilo(silo);

        return newMap;
    }

    // Generate Sprite
    private Sprite generateSprite(int index, int tileX, int tileY, ResourceManager resManager) {

        // Adds a Sprite from the Sprite Map using an Array List
        // New OH58D Helicopter Sprite
        if(index == 1) {

            Sprite sprite = oh58DHelicopter("enemy", resManager);

            // Face the Sprites downward
            Helicopter helicopter = (Helicopter)sprite;
            helicopter.rotateImage(gc, 90);

            // Set Max Speed and Rotate Speed
            sprite.setMaxSpeed(0.2f);

            // Set Reload Time
            helicopter.setBulletReloadTime(700);

            // Center the sprite
            sprite.setX(TileMapRenderer.tilesToPixels(tileX) +
                    (TileMapRenderer.tilesToPixels(1) - helicopter.getWidth()) / 2);

            // Bottom-justify the sprite
            sprite.setY(TileMapRenderer.tilesToPixels(tileY + 1) - helicopter.getHeight());

            // Add Sprite to Zone
            // Add an offset to generate the correct row the sprite is in the map file.
            int zoneRow = tileY + 6;

            if(zoneRow >= 60 && zoneRow <= 89) {

                sprite.setZone(MainGameState.getZone_3());
                MainGameState.setZone_3_NumEnemies(1);
            }

            if(zoneRow >= 90 && zoneRow <= 119) {

                sprite.setZone(MainGameState.getZone_2());
                MainGameState.setZone_2_NumEnemies(1);
            }

            if(zoneRow >= 120 && zoneRow <= 150) {

                sprite.setZone(MainGameState.getZone_1());
                MainGameState.setZone_1_NumEnemies(1);
            }
            return sprite;
        }

        // New Ozelot AATank Sprite
        if(index == 2) {

            Sprite sprite = aaTank("enemy", resManager);

            // Face the Sprites downward
            AATank aaTank = (AATank)sprite;
            aaTank.rotateBody(gc, 90);
            aaTank.rotateTurret(gc, 90);

            // Set Max Speed
            sprite.setMaxSpeed(0.2f);

            // Set Reload Time
            aaTank.setRocketReloadTime(2000);

            // Center the sprite
            sprite.setX(TileMapRenderer.tilesToPixels(tileX) +
                    (TileMapRenderer.tilesToPixels(1) - aaTank.getWidth()) / 2);

            // Bottom-justify the sprite
            sprite.setY(TileMapRenderer.tilesToPixels(tileY + 1) - aaTank.getHeight());

            // Add Sprite to Zone
            // Add an offset to generate the correct row the sprite is in the map file.
            int zoneRow = tileY + 6;

            if(zoneRow >= 60 && zoneRow <= 89) {

                sprite.setZone(MainGameState.getZone_3());
                MainGameState.setZone_3_NumEnemies(1);
            }

            if(zoneRow >= 90 && zoneRow <= 119) {

                sprite.setZone(MainGameState.getZone_2());
                MainGameState.setZone_2_NumEnemies(1);
            }

            if(zoneRow >= 120 && zoneRow <= 150) {

                sprite.setZone(MainGameState.getZone_1());
                MainGameState.setZone_1_NumEnemies(1);
            }
            return sprite;
        }

        // New AAGun Sprite
        if(index == 3) {

            Sprite sprite = aaGun("enemy", resManager);

            AAGun aaGun = (AAGun)sprite;

            // Set Max Speed
            sprite.setMaxSpeed(0.2f);

            // Set Reload Time
            aaGun.setReloadTime(1500);

            // Center the sprite
            sprite.setX(TileMapRenderer.tilesToPixels(tileX) +
                    (TileMapRenderer.tilesToPixels(1) - aaGun.getWidth()) / 2);

            // Bottom-justify the sprite
            sprite.setY(TileMapRenderer.tilesToPixels(tileY + 1) - aaGun.getHeight() + 18);

            // Add Sprite to Zone
            // Add an offset to generate the correct row the sprite is in the map file.
            int zoneRow = tileY + 6;

            if(zoneRow >= 60 && zoneRow <= 89) {

                sprite.setZone(MainGameState.getZone_3());
                MainGameState.setZone_3_NumEnemies(1);
            }

            if(zoneRow >= 90 && zoneRow <= 119) {

                sprite.setZone(MainGameState.getZone_2());
                MainGameState.setZone_2_NumEnemies(1);
            }

            if(zoneRow >= 120 && zoneRow <= 150) {

                sprite.setZone(MainGameState.getZone_1());
                MainGameState.setZone_1_NumEnemies(1);
            }
            return sprite;
        }
        return null;
    }

    // Loading sprites and images from the map
    public void loadTileImages(int mapIndex) {

        // Keep looking for tile A,B,C, etc. This makes it easy to drop new tiles in the Images/Tiles directory

        char ch = 'A';

        // Path to the Tiles
        String fileName = "";

        // Get File Path and Instantiate List
        if(mapIndex == roads) {

            fileName = "Tiles/Roads/tile_";
            roadTiles = new ArrayList();
        } else if(mapIndex == water) {

            fileName = "Tiles/Water/tile_";
            waterTiles = new ArrayList();
        } else if(mapIndex == LScape) {

            fileName = "Tiles/LScape/tile_";
            LScapeTiles = new ArrayList();
        }

        // Loop through all of the Tiles
        while(true) {

            String name = fileName + ch + ".png";

            ClassLoader classLoader = getClass().getClassLoader();
            URL url = classLoader.getResource("Resources/Images/" + name);

            // If the File does not exist then break out of the Loop
            if(url == null) break;

            if(mapIndex == roads) roadTiles.add(loadImage(name));
            if(mapIndex == water) waterTiles.add(loadImage(name));
            if(mapIndex == LScape) LScapeTiles.add(loadImage(name));

            ch++;
        }
    }

    // Instantiate a Billboard
    private Billboard loadBillBoard(String location, int tileX, int tileY) {

        // Load image
        Image image = loadImage(location);

        int width = image.getWidth(null) / 2;

        int height = image.getHeight(null) / 2;

        Billboard billBoard = new Billboard(image);

        // Center the Structure over a tile
        billBoard.setX(TileMapRenderer.tilesToPixels(tileX) + 32 - width);
        billBoard.setY(TileMapRenderer.tilesToPixels(tileY) + 32 - height);

        // Calculate if Billboard starts on screen and set on Screen to true
        int zoneRow = tileY + 6;

        if(zoneRow >= 120) billBoard.setOnScreen(true);

        return billBoard;
    }

    // Load Player Sprite
    public void loadPlayerSprite(ResourceManager resManager) {

        // Instantiate the Player with a UH60 BlackHawk Helicopter
        playerSprite = uh60Helicopter("player", resManager);

        // Rotate Player Sprite to face up
        Helicopter player = (Helicopter)playerSprite;

        player.setIsOnScreen(true);

        player.rotateImage(gc,-90);
    }

    // Instantiate Sprites
    // Instantiate a UH60 Black Hawk Helicopter
    private Sprite uh60Helicopter(String name, ResourceManager resManager) {

        // Load images for the Main Body and a Secondary Image for the Rotors
        Image[] images = new Image[5];

        images[0] = loadImage("Helicopters/UH60/UH60_Hover.png");
        images[1] = loadImage("Helicopters/UH60/UH60_FW.png");
        images[2] = loadImage("Helicopters/UH60/UH60_BW.png");
        images[3] = loadImage("Helicopters/UH60/UH60_LT.png");
        images[4] = loadImage("Helicopters/UH60/UH60_RT.png");

        // Load the Blade Animation Images
        String UH_Blades = "Helicopters/UH60/Blades/UH60_Blades_";

        Image[] bladeImgs = new Image[9];

        for(int i = 0; i < bladeImgs.length; i++) {

            String location = UH_Blades + (i) + ".png";
            bladeImgs[i] = loadImage(location);
        }

        // Load the Dying Animation Images
        String UHExp = "Helicopters/UH60/Explosions/UH60_";

        Image[] explosionImgs = new Image[48];

        for(int i = 0; i < explosionImgs.length; i++) {

            String location = UHExp + (i) + ".png";
            explosionImgs[i] = loadImage(location);
        }

        // Create Collision Object
        // DualCircles = new DualCircles(int R1, int R2, int Angle, int offsetR, int offsetX)
        DualCircles collision = new DualCircles(63, 20, 60, 3);

        // New Helicopter
        Sprite sprite = new Helicopter(name, images, createBladesAnim(bladeImgs), createDyingAnim(explosionImgs),
                collision, UH,109);

        return sprite;
    }

    // Instantiate a OH58D Helicopter
    public Sprite oh58DHelicopter(String name, ResourceManager resManager) {

        // Load images for the Main Body and a Secondary Image for the Rotors
        Image[] images = new Image[5];

        images[0] = loadImage("Helicopters/OH58D/OH58D_Hover.png");
        images[1] = loadImage("Helicopters/OH58D/OH58D_FW.png");
        images[2] = loadImage("Helicopters/OH58D/OH58D_BW.png");
        images[3] = loadImage("Helicopters/OH58D/OH58D_LT.png");
        images[4] = loadImage("Helicopters/OH58D/OH58D_RT.png");

        // Load the Blade Animation Images
        String OH_Blades = "Helicopters/OH58D/Blades/OH58D_Blades_";

        Image[] bladeImgs = new Image[9];

        for(int i = 0; i < bladeImgs.length; i++) {

            String location = OH_Blades + (i) + ".png";
            bladeImgs[i] = loadImage(location);
        }

        // Load the Dying Animation Images
        String OHExp = "Helicopters/OH58D/Explosions/OH58_";

        Image[] explosionImgs = new Image[48];

        for(int i = 0; i < explosionImgs.length; i++) {

            String location = OHExp + (i) + ".png";
            explosionImgs[i] = loadImage(location);
        }

        // Create Collision Object = new DualCircles(int R1, int R2, int Angle, int offsetR, int offsetX)
        DualCircles collision = new DualCircles(68,16,90,5);

        // New Helicopter
        Sprite sprite = new Helicopter(name, images, createBladesAnim(bladeImgs), createDyingAnim(explosionImgs),
                collision, OH,125);

        return sprite;
    }

    // Instantiate a AATank Ozelot
    public Sprite aaTank(String name, ResourceManager resManager){

        // Load images for the Main Body and a Secondary Image for the Rotors
        Image[] images = new Image[4];

        images[0] = loadImage("Tanks/Ozelot/Ozelot_Turret.png");
        images[1] = loadImage("Tanks/Ozelot/Ozelot_Still.png");
        images[2] = loadImage("Tanks/Ozelot/Ozelot_FW.png");
        images[3] = loadImage("Tanks/Ozelot/Ozelot_BW.png");

        // Load the Dying Animation Images
        String ozelotExp = "Tanks/Ozelot/Explosion/Ozelot_";

        Image[] explosionImgs = new Image[48];

        for(int i = 0; i < explosionImgs.length; i++) {

            String location = ozelotExp + (i) + ".png";
            explosionImgs[i] = loadImage(location);
        }

        // Create Collision Object = new Circle(int X, int Y, int R)
        Circle collision = new Circle(0, 0, 30);

        // New AATank
        //(String name, Image[] images, Animation dying, Collision Object, AudioFormat PLAYBACK_FORMAT, int offset)
        Sprite sprite = new AATank(name, images, createDyingAnim(explosionImgs), collision, AA, 25);

        return sprite;
    }

    // Instantiate a AAGun
    public Sprite aaGun(String name, ResourceManager resManager) {

        // Load images for the Main Body and a Secondary Image for the Rotors
        Image[] images = new Image[2];

        images[0] = loadImage("Guns/AATurret.png");
        images[1] = loadImage("Guns/AAGunBase.png");

        // Load the Dying Animation Images
        String ozelotExp = "Guns/Explosion/AAGun_Exp_";

        Image[] explosionImgs = new Image[22];

        for(int i = 0; i < explosionImgs.length; i++) {

            String location = ozelotExp + (i) + ".png";
            explosionImgs[i] = loadImage(location);
        }

        // Create Collision Object = new Circle(int X, int Y, int R)
        Circle collision = new Circle(0, 0, 30);

        // New AAGun
        //(String name, Image[] images, Animation dying, Collision Object, AudioFormat PLAYBACK_FORMAT, int offset)
        Sprite sprite = new AAGun(name, images, createDyingAnim(explosionImgs), collision, AG,0);

        return sprite;
    }

    // Instantiate a Silo
    private Silo silo(ResourceManager resManager) {

        // Load image
        Image[] images = new Image[2];

        images[0] = loadImage("Silo/silo_open.png");
        images[1] = loadImage("Silo/silo_exp.png");

        // Load the Launch Animation Images
        String launch = "Silo/Launch/silo_";

        Image[] launchImgs = new Image[13];

        for(int i = 0; i < launchImgs.length; i++) {

            String location = launch + (i) + ".png";
            launchImgs[i] = loadImage(location);
        }

        // Load the Launch Explosion Animation Images
        String launchExp = "Silo/Launch_Exp/silo_launch_";

        Image[] launchExpImgs = new Image[19];

        for(int i = 0; i < launchExpImgs.length; i++) {

            String location = launchExp + (i) + ".png";
            launchExpImgs[i] = loadImage(location);
        }

        // Load the Open Doors Animation Images
        String openDoors = "Silo/OpenDoors/silo_";

        Image[] openDoorsImgs = new Image[25];

        for(int i = 0; i < openDoorsImgs.length; i++) {

            String location = openDoors + (i) + ".png";
            openDoorsImgs[i] = loadImage(location);
        }

        // Load the Explosion Animation Images
        String explosion = "Silo/Explosion/silo_exp_";

        Image[]   expImgs  = new Image[19];

        for(int i = 0; i < expImgs.length; i++) {

            String location = explosion + (i) + ".png";
            expImgs[i] = loadImage(location);
        }

        // New Silo (Image image, Image[] launch, Image[] explosion, Image[] launchExp, Image[] openDoors)
        Silo silo = new Silo(images, launchImgs, expImgs, launchExpImgs, openDoorsImgs);

        return silo;
    }

    // Create Dying Animation
    private Animation createBladesAnim(Image[] images) {

        Animation anim = new Animation();

        for(int i = 0; i < images.length; i++) anim.addFrame(images[i],16);

        return anim;
    }

    // Create Dying Animation
    private Animation createDyingAnim(Image[] images) {

        /*
            Create the animations by placing images
            in a sequence followed by a time each image is
            displayed. -addFrame(image, time);
         */

        Animation anim = new Animation();

        for(int i = 0; i < images.length -1; i++) anim.addFrame(images[i],60);

        anim.addFrame(images[images.length -1],3000);

        return anim;
    }

    // Create Sparks Animation
    private Animation createSparksAnim(Image[] images) {

        Animation anim = new Animation();

        for(int i = 0; i < images.length; i++) anim.addFrame(images[i],60);

        return anim;
    }

    // Create Sparks Animation
    private Animation createRocketAnim(Image[] images) {

        Animation anim = new Animation();

        for(int i = 0; i < images.length; i++) anim.addFrame(images[i],60);

        return anim;
    }


    // Get Indexes for Loading Images
    // Get Sprite Index
    public int getSprites() { return sprites; }

    // Get Roads Index
    public int getRoads() { return roads; }

    // Get US Camp Index
    public int getCamp() { return camp; }

    // Get Billboards Index
    public int getBillBoards() { return billBoard; }

    // Get Water Index
    public int getWater() { return water; }

    // Get Land Scape Index
    public int getLScape() { return LScape; }


    // PROJECTILES
    // Player Has Bullets
    public boolean playerHasProjectiles() { return playerProjectiles != null; }

    // Enemy Has Bullets
    public boolean enemyHasProjectiles() { return enemyProjectiles != null; }

    //Add a Bullet object to this Sprite
    public void addProjectile(Sprite sprite, int type, int offsetX, SoundManager soundManager, Sound sfx) {

        Projectile projectile = null;

        // Is Sprite Projectile Ally
        boolean ally = sprite.getName().equals("player");

        // Center the Projectile over the Sprite by taking
        // the Width and Height and dividing by 2.
        float spriteX = sprite.getX() + (sprite.getWidth()/2);
        float spriteY = sprite.getY() + (sprite.getHeight()/2);

        // Get Sprite Direction
        double cosA = sprite.getCosA();
        double sinA = sprite.getSinA();

        // Get the type of sprite
        if(isType(sprite, UH) || isType(sprite, OH)) {

            // Decrease Ammunition to Limit shots
            Helicopter helicopter = (Helicopter) sprite;

            // Get Angle
            int angle = helicopter.getAngle();

            // If Projectile is a Bullet
            if(type == bullet) {

                helicopter.decreaseBulletAmmo();

                Animation anim = createSparksAnim(sparksImgs);

                // Rotate Image to match the Sprite
                Image newImage = getRotatedImage(gc, bulletImg, angle);

                // Adjust Offset for Projectile
                float projectileX = ((int)(spriteX+35*sprite.getCosA()));
                float projectileY = ((int)(spriteY+35*sprite.getSinA()));

                projectile = new Projectile(projectileX-(bulletW/2), projectileY-(bulletH/2), cosA, sinA,0.3f,
                        newImage, bulletW, bulletH, 2, angle, anim, ally, 60, "bullet");

                // Play SFX
                soundManager.play(sfx);
            } else  /* Projectile is a Rocket*/ {

                // Check if there are any rockets left
                if(helicopter.getNumRockets() <= 0) {

                    if(helicopter.isEnemy()) return;
                    else if(!MainGameState.godMode_Enabled) return;
                }
                // Decrease Rocket Reload Time
                helicopter.decreaseRocketAmmo();

                // Decrease Amount of Rockets
                if(helicopter.isEnemy()) helicopter.decreaseRockets();
                if(!MainGameState.godMode_Enabled && helicopter.isPlayer()) helicopter.decreaseRockets();

                Animation anim = createRocketAnim(rocketExp);

                // Rotate Image to match the Sprite
                Image newImage = getRotatedImage(gc, rocketImg, angle);

                // Adjust Offset for Projectile
                float projectileX = ((int)(spriteX+40*sprite.getCosA()));
                float projectileY = ((int)(spriteY+40*sprite.getSinA()));

                // Instantiate a new Projectile
                projectile = new Projectile(projectileX-(rocketW/2),projectileY-(rocketH/2), cosA, sinA,0.6f,
                        newImage, rocketW, rocketH,4, angle, anim, ally, 1000,"rocket");

                // Play SFX
                soundManager.play(sfx);
            }
        } else if(isType(sprite, AA)) {

            // Decrease Ammunition to Limit shots
            AATank aaSprite = (AATank) sprite;

            // Get Angle
            int angle = aaSprite.getTurretAngle();

            cosA = aaSprite.getTurretCosA();
            sinA = aaSprite.getTurretSinA();

            aaSprite.decreaseRocketAmmo();

            Animation anim = null;

            // Rotate Image to match the Sprite
            Image newImage = getRotatedImage(gc, OZRocketImg, angle);

            // Adjust Offset for Projectile
            float projectileX = ((int)(spriteX+20*sprite.getCosA()));
            float projectileY = ((int)(spriteY+5*sprite.getSinA()));

            // Instantiate a new Projectile
            projectile = new Projectile(projectileX-(ozRocketW/2),projectileY-(ozRocketH/2), cosA, sinA,0.3f,
                    newImage, ozRocketW, ozRocketH, 4, angle, anim, ally,500,"rocket");

            // Play SFX
            soundManager.play(sfx);

        } else if(isType(sprite, AG)) {

            // Decrease Ammunition to Limit shots
            AAGun aaSprite = (AAGun)sprite;

            // Get Angle
            int angle = aaSprite.getAngle();

            cosA = aaSprite.getCosA();
            sinA = aaSprite.getSinA();

            aaSprite.decreaseAmmo();

            Animation anim = null;

            // Rotate Image to match the Sprite
            Image newImage = getRotatedImage(gc, bulletImg, angle);

            // Adjust Offset for Projectile
            float projectileX = ((int)((spriteX+offsetX)+30*sprite.getCosA()));
            float projectileY = ((int)(spriteY+30*sprite.getSinA()));

            // Instantiate a new Projectile
            projectile = new Projectile(projectileX-(bulletW/2),projectileY-(bulletH/2), cosA, sinA,0.3f,
                    newImage, bulletW, bulletH,2, angle, anim, ally,240, "bullet");

            // Play SFX
            soundManager.play(sfx);
        }

        // Add Projectile to List
        if(sprite.isPlayer()) playerProjectiles.add(projectile); else enemyProjectiles.add(projectile);

        // Start moving the Projectile
        projectile.moveForward();
    }

    // Add Bullet
    public int addBullet() { return this.bullet; }

    // Add Rocket
    public int addRocket() { return this.rocket; }

    // Gets an Iterator of all the Player's Bullets
    public Iterator getPlayerProjectiles() { return playerProjectiles.iterator(); }

    // Gets an Iterator of all the Enemies's Bullets
    public Iterator getEnemyProjectiles() { return enemyProjectiles.iterator(); }

    // Removes a Sprite object from this map
    public void removePlayerProjectile(Projectile projectile) { playerProjectiles.remove(projectile); }

    // Removes a Sprite object from this map
    public void removeEnemyProjectile(Projectile projectile) { enemyProjectiles.remove(projectile); }

    // Is Sprite Type
    public boolean isType(Sprite sprite, int type) { return sprite.getType() == type; }

    // Get Rotated Image
    private Image getRotatedImage(GraphicsConfiguration gc, Image image, int angle) {

        // Set up the transform
        AffineTransform transform = new AffineTransform();
        transform.translate(image.getWidth(null) / 2.0,image.getHeight(null) / 2.0 );

        transform.rotate(Math.toRadians(angle));

        // Put origin back to upper left corner
        transform.translate(-image.getWidth(null) / 2.0,-image.getHeight(null) / 2.0);

        // Create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.BITMASK);

        // Draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        AffineTransform origTransform = g.getTransform();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, transform, null);
        g.setTransform(origTransform);
        g.dispose();

        return newImage;
    }

} // End of Class.