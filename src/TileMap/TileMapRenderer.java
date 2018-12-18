package TileMap;

import java.awt.*;
import java.util.Iterator;
import Collision.ScreenContains;
import Game.GameManager;
import Game.MainGameState;
import Graphics.Sprite;
import Graphics.Billboard;
import Graphics.Sprites.*;
import Game.GameResourceManager;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *     Copyright (c) 2016, Nuclear Deterrence
 *
 *     Code modified from David Brackeen
 *     Copyright (c) 2003, David Brackeen
 *
 *     The TileMapRenderer class draws a TileMap on the screen.
 *     It draws all tiles, sprites, and an optional background image
 *     centered around the position of the player.
 *
 *     <p>If the width of background image is smaller the width of
 *     the tile map, the background image will appear to move
 *     slowly, creating a parallax background effect.
 *
 *     <p>Also, three static methods are provided to convert pixels
 *     to tile positions, and vice-versa.
 *
 *     <p>This TileMapRender uses a tile size of 64.
 */
public class TileMapRenderer {

    private static final int TILE_SIZE = 64;

    // The size in bits of the tile Math.pow(2, TILE_SIZE_BITS) = TILE_SIZE
    private static final int TILE_SIZE_BITS = 6;

    // Draws the specified TileMap
    public void draw(GraphicsConfiguration gc, Graphics2D g, TileMap spriteMap, TileMap roadMap, TileMap billBoardMap,
                     TileMap LScapeMap, TileMap waterMap, int screenWidth, int screenHeight, boolean colBoundVisible,
                                                                ScreenContains rect, GameResourceManager resManager) {

        // Get the Player from the Map
        Helicopter player = (Helicopter)spriteMap.getPlayer();

        // Get Width and Height of the Map
        int mapWidth  = tilesToPixels(waterMap.getWidth());
        int mapHeight = tilesToPixels(waterMap.getHeight());

        // Get the scrolling position of the map based on Player's position
        // Get X Offset
        int offsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);

        // Get Y Offset
        int offsetY = screenHeight / 2 - Math.round(player.getY()) - TILE_SIZE;
        offsetY = Math.min(offsetY, 0);
        offsetY = Math.max(offsetY, screenHeight - mapHeight);


        // DRAW BACKGROUND
        g.setColor(Color.black);
        g.fillRect(0, 0, screenWidth, screenHeight);


        // DRAW TILES
        // Draw the Water
        drawTiles(g, offsetX, offsetY, screenWidth, waterMap);

        // Draw the LandScape
        drawTiles(g, offsetX, offsetY, screenWidth, LScapeMap);

        // Draw the Roads
        drawTiles(g, offsetX, offsetY, screenWidth, roadMap);


        // DRAW MAP BILLBOARDS
        Iterator i = billBoardMap.getBillBoards();

        drawMapBillBoards(g, i, offsetX, offsetY);

        // DRAW SILO
        Silo silo = spriteMap.getSilo();

        if(silo.isOnScreen()) silo.draw(g, offsetX, offsetY, colBoundVisible);


        // DRAW MAP SPRITES
        i = spriteMap.getSprites();

        drawMapSprites(gc, g, i, offsetX, offsetY, colBoundVisible);


        // DRAW PLAYER
        player.draw(gc, g, offsetX, offsetY, colBoundVisible);


        // DRAW PROJECTILES
        // Get the Enemy Projectiles
        i = resManager.getEnemyProjectiles();

        drawProjectiles(gc, g, i, offsetX, offsetY);

        // Get the Player Projectiles
        i = resManager.getPlayerProjectiles();

        drawProjectiles(gc, g, i, offsetX, offsetY);


        // COLLISION BOUNDARY
        // If Collision Boundary is Visible then Draw the On Screen Rectangle
        if(colBoundVisible) rect.draw(g, offsetX, offsetY);

        // Draw CountDown
        if(silo.isOnScreen()) silo.drawCountDown(g, screenWidth);

        // Draw God Mode
        if(MainGameState.godMode_Enabled) {

            Font font = new Font("Stencil Std", Font.PLAIN, 20);
            g.setColor(Color.RED);
            g.setFont(font);
            g.drawString("God Mode Enabled", 2, 20);
        }
    }

    // Draw Tiles
    private void drawTiles(Graphics2D g, int offsetX, int offsetY, int screenWidth, TileMap map) {

        // Draw only the Visible Tiles on the Screen
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX  = firstTileX + pixelsToTiles(screenWidth) + 1;

        int shakeOffsetX = offsetX;

        if(GameManager.shake.shaking()) shakeOffsetX += GameManager.shake.getOffset();

        for(int y = 0; y < map.getHeight(); y++) {

            for(int x = firstTileX; x <= lastTileX; x++) {

                Image bgImage = map.getTile(x, y);

                if(bgImage != null)
                    g.drawImage(bgImage, tilesToPixels(x) + shakeOffsetX, tilesToPixels(y) + offsetY, null);
            }
        }
    }

    // Draw Projectiles
    private void drawProjectiles(GraphicsConfiguration gc, Graphics2D g, Iterator i, int offsetX, int offsetY){

        // Generate new position and determine which type of projectile to draw

        while(i.hasNext()) {

            Projectile projectile = (Projectile)i.next();

            // Add Map Offset to each Projectile and Draw if it is on screen
            if(projectile.isOnScreen()) {

                int projectileX = Math.round(projectile.getX()) + offsetX;
                int projectileY = Math.round(projectile.getY()) + offsetY;
                projectile.draw(gc, g, projectileX, projectileY);
            }
        }
    }

    // Draw Map Sprites
    private void drawMapSprites(GraphicsConfiguration gc, Graphics2D g, Iterator i, int offsetX, int offsetY,
                                                                                            boolean colBoundVisible){

        // Only Draw Sprites that are on the Screen
        while(i.hasNext()) {

            Sprite sprite = (Sprite)i.next();

            // Check if Sprite is on the Screen
            if(sprite.isOnScreen()) {

                // If the Sprite is an AATank
                if(sprite instanceof AATank) {

                    int shakeOffsetX = offsetX;
                    if(GameManager.shake.shaking()) shakeOffsetX += GameManager.shake.getOffset();
                    AATank aaTank = (AATank)sprite;
                    aaTank.draw(gc, g, shakeOffsetX, offsetY, colBoundVisible);
                }

                // If the Sprite is an AAGun
                if(sprite instanceof AAGun) {

                    int shakeOffsetX = offsetX;
                    if(GameManager.shake.shaking()) shakeOffsetX += GameManager.shake.getOffset();
                    AAGun aaGun = (AAGun)sprite;
                    aaGun.draw(gc, g, shakeOffsetX, offsetY, colBoundVisible);
                }

                // If the Sprite is a Helicopter
                if(sprite instanceof Helicopter) {

                    Helicopter helicopter = (Helicopter)sprite;
                    helicopter.draw(gc, g, offsetX, offsetY, colBoundVisible);
                }
            }
        }
    }

    // Draw Map Sprites
    private void drawMapBillBoards(Graphics2D g, Iterator i, int offsetX, int offsetY){

        // Only Draw Structures that are on the Screen
        while(i.hasNext()) {

            int shakeOffsetX = offsetX;

            if(GameManager.shake.shaking()) shakeOffsetX += GameManager.shake.getOffset();

            Billboard billBoard = (Billboard)i.next();

            // Check if Structure is on the Screen
            if(billBoard.isOnScreen()) billBoard.draw(g, shakeOffsetX, offsetY);
        }
    }

    // Converts a pixel position to a tile position
    public static int pixelsToTiles(int pixels) {

        // use shifting to get correct values for negative pixels
        return pixels >> TILE_SIZE_BITS;

        // or, for tile sizes that aren't a power of two, use the floor function:
        // return (int)Math.floor((float)pixels / TILE_SIZE);
    }

    // Converts a tile position to a pixel position
    public static int tilesToPixels(int numTiles) {

        // no real reason to use shifting here.
        // it's slightly faster, but doesn't add up to much
        // on modern processors.
        return numTiles << TILE_SIZE_BITS;

        // use this if the tile size isn't a power of 2:
        //return numTiles * TILE_SIZE;
    }

} // End of Class.