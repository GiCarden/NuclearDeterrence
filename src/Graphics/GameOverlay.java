package Graphics;

import javax.swing.*;
import java.awt.*;
import Graphics.Shapes.Arrow;
import Graphics.Sprites.Helicopter;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class GameOverlay {

    private int statsY;
    private int arrowX;
    private int arrowY;
    private int nameY;
    private int healthY;
    private int rocketsY;
    public Arrow arrow;
    private Color color;
    private Image arrowBgImage;
    private Image bgImage;
    private String name;
    private Font font;

    // Game Overlay
    public GameOverlay(String[] files, int screenWidth, int screenHeight) {

        // Load the Image
        String filename = "Resources/Images/" + files[0];

        this.bgImage = new ImageIcon(getClass().getClassLoader().getResource(filename)).getImage();

        filename = "Resources/Images/" + files[1];

        this.arrowBgImage = new ImageIcon(getClass().getClassLoader().getResource(filename)).getImage();

        this.font = new Font("Stencil Std", Font.PLAIN, 14);

        this.color = new Color(0, 153, 0);

        this.arrow = new Arrow((screenWidth)- (arrowBgImage.getWidth(null)/2) - 11, screenHeight - 65, 0);

        this.statsY = screenHeight - 150;

        this.nameY = screenHeight - 85;

        this.healthY = screenHeight - 65;

        this.rocketsY = screenHeight - 45;

        this.arrowX = (screenWidth) - (arrowBgImage.getWidth(null)) - 10;

        this.arrowY = screenHeight - 105;
    }

    // Draw
    public void draw(Graphics2D g, Sprite sprite) {

        Helicopter player = (Helicopter)sprite;

        // Set Font
        g.setFont(font);

        // Set Color
        g.setColor(Color.WHITE);

        // Get Sprite's Health
        int healthPercent = sprite.getHealth();

        if(healthPercent < 0) healthPercent = 0;

        String health = "Health  : " + healthPercent + " %";

        // Get Number of Rockets
        String numRockets = "Rockets: " + player.getNumRockets();

        // Draw the Sprite's Stats at Bottom Left Corner of the screen
        g.drawImage(bgImage, 10, statsY, null);

        // Draw Name
        g.drawString(name, 180, nameY);

        // Draw the Health
        g.drawString(health, 180, healthY);

        // Draw Number of Rockets
        g.drawString(numRockets, 180, rocketsY);

        // Draw Image
        g.drawImage(arrowBgImage, arrowX, arrowY, null);

        // Draw Arrow Image
        arrow.draw(g, color);
    }

    // Set Name
    public void setName(String name) { this.name = name; }

} // End of Class