package TileMap;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Iterator;
import Graphics.Billboard;
import Graphics.Sprite;
import Graphics.Sprites.Silo;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Code modified from David Brackeen
 *  Copyright (c) 2003, David Brackeen
 *
 *  The TileMap class contains the data for a tile-based
 *  map, including Sprites. Each tile is a reference to an Image.
 */
public class TileMap {

    private Image[][] tiles;
    private LinkedList sprites;
    private LinkedList billBoards;
    public  Silo silo;
    private Sprite player;

    // Creates a new TileMap with the specified width and height (in number of tiles) of the map
    public TileMap(int width, int height) {

        tiles = new Image[width][height];
        sprites = new LinkedList();
        billBoards = new LinkedList();
    }

    // Gets the width of this TileMap (number of tiles across)
    public int getWidth() { return tiles.length; }

    // Gets the height of this TileMap (number of tiles down)
    public int getHeight() { return tiles[0].length; }

    // Get Tile
    public Image getTile(int x, int y) {

        // Gets the tile at the specified location. Returns null if no tile
        // is at the location or if the location is out of bounds.
        if(x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {

            return null;
        } else {

            return tiles[x][y];
        }
    }

    // Sets the tile at the specified location.
    public void setTile(int x, int y, Image tile) { tiles[x][y] = tile; }

    // Gets the player Sprite.
    public Sprite getPlayer() { return player; }

    // Sets the player Sprite.
    public void setPlayer(Sprite player) { this.player = player; }

    // Sets the Silo Sprite.
    public void setSilo(Silo silo) { this.silo = silo; }

    // Gets the Silo Sprite.
    public Silo getSilo() { return silo; }

    // Adds a Sprite object to this map.
    public void addSprite(Sprite sprite) { sprites.add(sprite); }

    // Adds a Sprite object to this map.
    public void addBillboard(Billboard billBoard) { billBoards.add(billBoard); }

    // Removes a Sprite object from this map.
    public void removeSprite(Sprite sprite) { sprites.remove(sprite); }

    // Gets an Iterator of all the Sprites in this map, excluding the player Sprite
    public Iterator getSprites() { return sprites.iterator(); }

    // Gets an Iterator of all the Structures in this map
    public Iterator getBillBoards() { return billBoards.iterator(); }

} // End of Class.