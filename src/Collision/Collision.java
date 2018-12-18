package Collision;

import Game.MainGameState;
import Graphics.Shapes.Circle;
import Graphics.Sprite;
import Graphics.Sprites.*;
import Graphics.Character;
import Sound.Sound;
import Sound.SoundManager;
import TileMap.TileMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class Collision {

    // Is Sprite Pushed Back for when Ally Sprite's Collide
    private static boolean isPushedBack = false;

    // Timer used to delay calls to methods
    private static Timer timer = new Timer();

    // CHARACTER COLLISION
    // Check Character Collision
    public static void characterCollision(Character character, long elapsedTime, TileMap spriteMap,
                                          SoundManager soundManager, Sound sfx) {

        // Do not check dying or dead sprites
        if(!character.isAlive()) { return; }

        // Check for character collision with other sprites
        Sprite collisionSprite = getSpriteCollision(character, spriteMap);

        // If a Character Collision Sprite is returned
        if(collisionSprite instanceof Character) {

            Character otherSprite = (Character)collisionSprite;

            // Check Character Health and start dying mode if health is 0
            if(character.getHealth() <= 0) {

                // Play Dying SFX
                soundManager.play(sfx);

                // Stop character movement
                character.stopMovement();

                // Set Dying State
                character.setState(character.STATE_DYING);

                if(!otherSprite.isPlayer()) {

                    // Update Zone Number of Enemies
                    if(otherSprite.getZone() == MainGameState.getZone_1())
                        MainGameState.setZone_1_NumEnemies(-1);
                    else if(otherSprite.getZone() == MainGameState.getZone_2())
                        MainGameState.setZone_2_NumEnemies(-1);
                    else if(otherSprite.getZone() == MainGameState.getZone_3())
                        MainGameState.setZone_3_NumEnemies(-1);
                }
            }

            // If Character collided and instead was pushed back update Location
            if(isPushedBack) {

                // Normal Update To calculate previous move first
                character.update(elapsedTime);

                // Adjust push back Location
                // Update X Location
                float dx = character.getVelocityX();
                float oldX = character.getX();
                float newX = oldX + dx * elapsedTime;

                // Set Character X
                character.setX(newX);

                // Change Y Location
                float dy = character.getVelocityY();
                float oldY = character.getY();
                float newY = oldY + dy * elapsedTime;

                // Set Character Y
                character.setY(newY);

                // Reset Character Push Back
                setIsPushedBack(false);
            }

            // Check Other Sprite Health and start dying mode if health is 0
            if(otherSprite.getHealth() <= 0) {

                // Play Dying SFX
                // If Both Sprites die the SFX can sometimes sound distorted if they are both played at the
                // exact same time. To prevent this distortion, create a timer delay. After the delay play
                // the Other Sprite's Dying SFX. The delay does does not pause the thread but creates a delay
                // to a method call.

                // This will cancel the current task. If there is no active task, nothing happens.
                timer.cancel();
                timer = new Timer();

                // After timer ends play other Sprite's Dying SFX
                TimerTask action = new TimerTask() { public void run() { soundManager.play(sfx); } };

                // Start Timer with Delay amount
                timer.schedule(action, 300);

                // Stop character movement
                otherSprite.stopMovement();

                // Set Dying State
                otherSprite.setState(character.STATE_DYING);

                if(!otherSprite.isPlayer()) {

                    // Update Zone Number of Enemies
                    if(otherSprite.getZone() == MainGameState.getZone_1())
                        MainGameState.setZone_1_NumEnemies(-1);
                    else if(otherSprite.getZone() == MainGameState.getZone_2())
                        MainGameState.setZone_2_NumEnemies(-1);
                    else if(otherSprite.getZone() == MainGameState.getZone_3())
                        MainGameState.setZone_3_NumEnemies(-1);
                }
            }
        }
    }

    // Get Sprite that Collided with another Sprite
    private static Sprite getSpriteCollision(Sprite s1, TileMap spriteMap) {

        // Check collision between S1 and all map Sprites
        Iterator i = spriteMap.getSprites();

        while(i.hasNext()) {

            Sprite otherSprite = (Sprite)i.next();

            // If collision found, return the Sprite
            if(isCollision(s1, otherSprite)) return otherSprite;
        }

        // Check S1 collision against the Player as long as S1 is not the player.
        // This allows the other sprites to push back instead of colliding with
        // the player.
        if(!s1.isPlayer()) {

            Sprite player = spriteMap.getPlayer();

            // If collision found, return the Player
            if(isCollision(s1, player)) return player;
        }

        // No collision found
        return null;
    }

    // Is Sprite Collision
    private static boolean isCollision(Sprite s1, Sprite s2) {

        // Checks if two Sprites collide with one another.
        // Each Sprite's collision object could be One Circle or Two Circles.
        // Collision Models are stored as objects and need to be accessed as a
        // Circle or DualCircles object.
        // If the two Sprites are Allies, push sprites back and return.

        // If S1 is also S2 return, do not check same Sprite against itself
        if(s1.equals(s2)) return false;

        // If one of the Sprites is a dead character, return
        if(s1 instanceof Character && !((Character)s1).isAlive()) return false;
        if(s2 instanceof Character && !((Character)s2).isAlive()) return false;

        // IF the Sprite that collided was the player and God Mode is Enabled then return
        if(s1.isPlayer() && MainGameState.godMode_Enabled) return false;
        if(s2.isPlayer() && MainGameState.godMode_Enabled) return false;

        // Get screen location of each Collision Model.
        // Location equals center of the sprite by
        // dividing image width and height by 2.
        float x1 = s1.getX() + (s1.getWidth() / 2);
        float y1 = s1.getY() + (s1.getHeight() / 2);
        float x2 = s2.getX() + (s2.getWidth() / 2);
        float y2 = s2.getY() + (s2.getHeight() / 2);

        // Check which type of Collision Model each Sprite has.
        // If Sprite 1 has 1 Circle
        if(s1.getCollisionPoly() instanceof Circle) {

            // Get S1's Collision Model
            Circle s1Collision = (Circle) s1.getCollisionPoly();

            // If Second Sprite has 1 Circle
            if(s2.getCollisionPoly() instanceof Circle) {

                // Get S2's Collision Model
                Circle s2Collision = (Circle) s2.getCollisionPoly();

                // Get S2's Radius
                int s2R1 = s2Collision.getR();

                // If S1 is not an Ally then enlarge the Collision Radius.
                // This allows a larger distance for Collision preventing
                // S1 from Colliding with S2.
                if(!s1.isAlly(s2) && s1.isEnemy()) s2R1 *= 2;

                // Check S1 Circle 1 to S2 Circle 1
                // Has Collided With Circle(float x1, float y1, float x2, float y2, int r2)
                if(s1Collision.hasCollidedWith(x1, y1, x2, y2, s2R1)) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }
                return false;
            }

            // If S2 has two Circles
            if(s2.getCollisionPoly() instanceof DualCircles) {

                // Get S2's Collision Model
                DualCircles s2Collision = (DualCircles) s2.getCollisionPoly();

                // Get S2's Radius
                int s2R2 = s2Collision.circle2.getR();

                // If S1 is not an Ally then enlarge the Collision Radius.
                // This allows a larger distance for Collision preventing
                // S1 from Colliding with S2.
                if(!s1.isAlly(s2) && s1.isEnemy()) s2R2 *= 2;

                // Circle 2 needs to adjust position based on an offset
                // from circle 1. Set the X/Y of Sprite 2 Circle 2
                int circle2X = ((int)((x2+s2Collision.getOffsetX())-(s2Collision.getOffsetR()+s2R2)*s2.getCosA()));
                int circle2Y = ((int)((y2)-(s2Collision.getOffsetR()+s2R2)*s2.getSinA()));

                // S1 Circle 1 to S2 Circle 1
                // Has Collided With Circle1(float x1, float y1, float x2, float y2, int r2)
                if(s1Collision.hasCollidedWith(x1, y1, x2, y2, s2Collision.circle1.getR())) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }

                // S1 Circle 1 to S2 Circle 2
                // Has Collided With Circle2(float x1, float y1, float x2, float y2, int r2)
                if(s1Collision.hasCollidedWith(x1, y1, circle2X, circle2Y, s2R2)) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }
            }
        }

        // If S1 has two Circles
        if(s1.getCollisionPoly() instanceof DualCircles) {

            // Get S1's Collision Model
            DualCircles s1Collision = (DualCircles) s1.getCollisionPoly();

            // Get S1's Radius
            int s1R2 = s1Collision.circle2.getR();

            // Circle 2 needs to adjust position based on an offset
            // from circle 1. Set the X/Y of Sprite 1 Circle 2
            int circle2X = ((int)((x1+s1Collision.getOffsetX())-(s1Collision.getOffsetR()+s1R2)*s1.getCosA()));
            int circle2Y = ((int)((y1)-(s1Collision.getOffsetR()+s1R2)*s1.getSinA()));

            // If S2 has 1 Circle
            if(s2.getCollisionPoly() instanceof Circle) {

                // Get S2's Collision Model
                Circle s2Collision = (Circle) s2.getCollisionPoly();

                // Get S2's Radius
                int s2R2 = s2Collision.getR();

                // If S1 is not an Ally then enlarge the Collision Radius.
                // This allows a larger distance for Collision preventing
                // S1 from Colliding with S2.
                if(!s1.isAlly(s2) && s1.isEnemy()) s2R2 *= 2;

                // S1 Circle 1 to S2 Circle 1
                // Has Collided With Circle1(float x1, float y1, float x2, float y2, int r2)
                if (s1Collision.circle1.hasCollidedWith(x1, y1, x2, y2, s2R2)) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }

                // S1 Circle 1 to S2 Circle 1
                // Has Collided With Circle2(float x1, float y1, float x2, float y2, int r2)
                if (s1Collision.circle2.hasCollidedWith(circle2X, circle2Y, x2, y2, s2R2)) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }
            }

            // If S2 has 2 Circles
            if(s2.getCollisionPoly() instanceof DualCircles) {

                // Get S2's Collision Model
                DualCircles s2Collision = (DualCircles) s2.getCollisionPoly();

                int s2R1 = s2Collision.circle1.getR();
                int s2R2 = s2Collision.circle2.getR();

                // If S1 is not an Ally then enlarge the Collision Radius.
                // This allows a larger distance for Collision preventing
                // S1 from Colliding with S2.
                if(!s1.isAlly(s2) && s1.isEnemy()) {

                    s2R1 *= 2;
                    s2R2 *= 2;
                }

                // Circle 2 needs to adjust position based on an offset
                // from circle 1. Set the X/Y of Sprite 1 Circle 2
                int s2Circle2X =
                        ((int)((x2+s2Collision.getOffsetX())-(s2Collision.getOffsetR()+s2R2)*s2.getCosA()));
                int s2Circle2Y =
                        ((int)((y2)-(s2Collision.getOffsetR()+s2R2)*s2.getSinA()));

                // S1 Circle 1 to S2 Circle 1
                // Has Collided With Circle1(float x1, float y1, float x2, float y2, int r2)
                if(s1Collision.circle1.hasCollidedWith(x1, y1, x2, y2, s2R1)) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }

                // S1 Circle 1 to S2 Circle 2
                // Has Collided With Circle2(float x1, float y1, float x2, float y2, int r2)
                if(s1Collision.circle1.hasCollidedWith(x1, y1, s2Circle2X, s2Circle2Y, s2R2)) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun  && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }

                // S1 Circle 2 to S2 Circle 1
                // Has Collided With Circle2(float x1, float y1, float x2, float y2, int r2)
                if(s1Collision.circle2.hasCollidedWith(circle2X, circle2Y, x2, y2, s2R1)) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }

                // S1 Circle 2 to S2 Circle 2
                // Has Collided With Circle2(float x1, float y1, float x2, float y2, int r2)
                if(s1Collision.circle2.hasCollidedWith(circle2X, circle2Y, s2Circle2X, s2Circle2Y, s2R2)) {

                    // If Collision was caused by the Player then allow
                    // Collision and create damage.
                    if(!s1.isAlly(s2) && s1.isPlayer()) {

                        // Do nothing for collisions between Player and Ground Units
                        if(s2 instanceof AATank || s2 instanceof AAGun) return false;

                        s1.setHealth(0);
                        s2.setHealth(0);
                        return true;
                    } else {

                        // Do nothing for collisions between Sprite and Ground Units
                        if(s1 instanceof AATank && s2 instanceof Helicopter) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AATank) return false;
                        if(s1 instanceof Helicopter && s2 instanceof AAGun) return false;
                        if(s1 instanceof AAGun && s2 instanceof Helicopter) return false;

                        // Else Collision was caused by Allies or the Enemy,
                        // so push back and prevent Collision.
                        s1.moveBackward();
                        setIsPushedBack(true);
                        return true;
                    }
                }
            }
        }
        // No collision has been detected
        return false;
    }

    // Set Is Pushed Back
    private static void setIsPushedBack(boolean pushed) { isPushedBack = pushed; }


    // PROJECTILE COLLISION
    // Check Projectile Collision
    public static boolean projectileCollision(Projectile projectile, TileMap spriteMap, SoundManager soundManager,
                                              Sound sfx){

        // Check for collision with other sprites
        Sprite collisionSprite = getSpriteCollision(projectile, spriteMap);

        if(collisionSprite instanceof Character) {

            Character otherSprite = (Character)collisionSprite;

            // Check Character Health and start dying mode if health is 0
            if(otherSprite.getHealth() <= 0) {

                    // Play Dying SFX
                    soundManager.play(sfx);

                    // Stop character movement
                    otherSprite.stopMovement();

                    // Set Dying State
                    otherSprite.setState(otherSprite.STATE_DYING);

                    if(!otherSprite.isPlayer()) {

                        // Update Zone Number of Enemies
                        if(otherSprite.getZone() == MainGameState.getZone_1())
                            MainGameState.setZone_1_NumEnemies(-1);
                        else if(otherSprite.getZone() == MainGameState.getZone_2())
                            MainGameState.setZone_2_NumEnemies(-1);
                        else if(otherSprite.getZone() == MainGameState.getZone_3())
                            MainGameState.setZone_3_NumEnemies(-1);
                    }
                    // Collision Found return
                    return true;
            }
            else return true;
        }
        // No Collision Found Return
        return false;
    }

    // Get Sprite that Collided with Projectile
    private static Sprite getSpriteCollision(Projectile projectile, TileMap spriteMap) {

        // Check collision between Player's Projectile and all map Sprites
        // Else Check Other Sprite's Projectile collision against the Player

        if(projectile.isAlly()) {

            Iterator i = spriteMap.getSprites();

            while(i.hasNext()) {

                Sprite otherSprite = (Sprite) i.next();

                // If collision found, return the Sprite
                // Do not check dying or dead sprites
                if(isCollision(projectile, otherSprite)) return otherSprite;
            }
        } else {

            Sprite player = spriteMap.getPlayer();

            // If collision found, return the Player
            if(isCollision(projectile, player)) return player;
        }
        // No collision found
        return null;
    }

    // Is Projectile Collision
    private static boolean isCollision(Projectile projectile, Sprite sprite) {

        // If the Sprites is a dead character, return
        if(sprite instanceof Character && !((Character)sprite).isAlive()) return false;

        // Get screen location of each Collision Model.
        // Location equals center of the sprite by
        // dividing image width and height by 2.
        int x1 = Math.round(projectile.getX() + (projectile.getWidth() / 2));
        int y1 = Math.round(projectile.getY() + (projectile.getHeight()/ 2));
        int x2 = Math.round((sprite.getX() + (sprite.getWidth() / 2)));
        int y2 = Math.round((sprite.getY() + (sprite.getHeight() / 2)));

        // If S2 has 1 Circle
        if(sprite.getCollisionPoly() instanceof Circle) {

            // Get S2's Collision Model
            Circle s2Collision = (Circle) sprite.getCollisionPoly();

            // Has Collided With Circle(float x1, float y1, float x2, float y2, int r2)
            if(projectile.hasCollidedWith(x1, y1, x2, y2, s2Collision.getR())) {

                // IF the Sprite that collided was the player and God Mode is Enabled then return
                if(sprite.isPlayer() && MainGameState.godMode_Enabled) return true;

                // If Collision found create damage
                if(projectile.getType().equals("bullet")) sprite.decreaseHealth(10);
                if(projectile.getType().equals("rocket")) sprite.decreaseHealth(20);
                return true;
            }
            return false;
        }

        // If Sprite 2 has two Circles
        if(sprite.getCollisionPoly() instanceof DualCircles) {

            // Get S2's Collision Model
            DualCircles s2Collision = (DualCircles) sprite.getCollisionPoly();

            // Get Radius of both Circles
            int r1 = s2Collision.circle1.getR();
            int r2 = s2Collision.circle2.getR();

            // Circle 2 needs to adjust position based on an offset
            // from circle 1. Set the X/Y of Sprite 2 Circle 2
            int circle2X = ((int)((x2 + s2Collision.getOffsetX()) - (s2Collision.getOffsetR()+r2) * sprite.getCosA()));
            int circle2Y = ((int)((y2) - (s2Collision.getOffsetR()+r2) * sprite.getSinA()));

            // Has Collided With Circle 1(float x1, float y1, float x2, float y2, int r2)
            if(projectile.hasCollidedWith(x1, y1, x2, y2, r1)) {

                // IF the Sprite that collided was the player and God Mode is Enabled then return
                if(sprite.isPlayer() && MainGameState.godMode_Enabled) return true;

                // If Collision found create damage
                if(projectile.getType().equals("bullet")) sprite.decreaseHealth(10);
                if(projectile.getType().equals("rocket")) sprite.decreaseHealth(20);
                return true;
            }

            // Has Collided With Circle 2(float x1, float y1, float x2, float y2, int r2)
            if(projectile.hasCollidedWith(x1, y1, circle2X, circle2Y, r2)) {

                // IF the Sprite that collided was the player and God Mode is Enabled then return
                if(sprite.isPlayer() && MainGameState.godMode_Enabled) return true;

                // If Collision found create damage
                if(projectile.getType().equals("bullet")) sprite.decreaseHealth(10);
                if(projectile.getType().equals("rocket")) sprite.decreaseHealth(20);
                return true;
            }
        }
        // No collision has been detected
        return false;
    }

    // Is Projectile Collision
    public static boolean isCollision(Projectile projectile, Silo silo) {

        // If the Silo is dead, return
        if(silo.isDead()) return false;

        // Get screen location of each Collision Model.
        // Location equals center of the sprite by
        // dividing image width and height by 2.
        int x1 = Math.round(projectile.getX() + (projectile.getWidth() / 2));
        int y1 = Math.round(projectile.getY() + (projectile.getHeight()/ 2));
        int x2 = Math.round((silo.getX() + (silo.getWidth() / 2)));
        int y2 = Math.round((silo.getY() + (silo.getHeight() / 2)));

        // Has Collided With Circle(float x1, float y1, float x2, float y2, int r2)
        if(projectile.hasCollidedWith(x1, y1, x2, y2, silo.getR())) {

            // If Collision found create damage.
            silo.decreaseHealth(5);
            return true;
        }
        // No collision has been detected
        return false;
    }

    // Is Silo Rocket Collision
    public static boolean isCollision(Helicopter player, Silo silo, SoundManager soundManager, Sound sfx) {

        // Checks if the player collides with the rocket when it is launched

        // Get screen location of each Collision Model.
        // Location equals center of the sprite by
        // dividing image width and height by 2.
        float x1 = player.getX() + (player.getWidth() / 2);
        float y1 = player.getY() + (player.getHeight() / 2);
        float x2 = silo.getX() + (silo.getWidth() / 2);
        float y2 = silo.getY() + (silo.getHeight() / 2);

        int r2   = silo.getR();

        // If S1 has two Circles
        if(player.getCollisionPoly() instanceof DualCircles) {

            // Get S1's Collision Model
            DualCircles s1Collision = (DualCircles) player.getCollisionPoly();

            // Get S1's Radius
            int s1R2 = s1Collision.circle2.getR();

            // Circle 2 needs to adjust position based on an offset
            // from circle 1. Set the X/Y of Sprite 1 Circle 2
            int circle2X = ((int)((x1+s1Collision.getOffsetX())-(s1Collision.getOffsetR()+s1R2)*player.getCosA()));
            int circle2Y = ((int)((y1)-(s1Collision.getOffsetR()+r2)*player.getSinA()));


            // S1 Circle 1 to S2 Circle 1
            // Has Collided With Circle1(float x1, float y1, float x2, float y2, int r2)
            if (s1Collision.circle1.hasCollidedWith(x1, y1, x2, y2, r2)) {

                // Play Dying SFX
                soundManager.play(sfx);

                // Stop character movement
                player.stopMovement();

                // Set Dying State
                player.setState(player.STATE_DYING);
            }

            // S1 Circle 2 to S2 Circle 1
            // Has Collided With Circle2(float x1, float y1, float x2, float y2, int r2)
            if (s1Collision.circle2.hasCollidedWith(circle2X, circle2Y, x2, y2, r2)) {

                // Play Dying SFX
                soundManager.play(sfx);

                // Stop character movement
                player.stopMovement();

                // Set Dying State
                player.setState(player.STATE_DYING);
            }
        }
        // No collision has been detected
        return false;
    }

} // End of Class.