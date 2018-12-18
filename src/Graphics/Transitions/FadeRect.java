package Graphics.Transitions;

import java.awt.*;

/**
 * Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Fade Rectangle updates an alpha channel and sets a color to fade in or out.
 *  The alpha channel is a float value between 0 and 1, where 0 is fully transparent and 1 is fully visible.
 *
 *  To use, instantiate a FadeRect object and give it a color plus a float start fade value.
 *  The start value determines if the transition is fading in or out.
 */
public class FadeRect extends Transition {

    // Fade Rect
    public FadeRect(int x, int y, int w, int h, float alpha, Color color) { super(x, y, w, h, alpha, color); }

    // Update
    public void update(float a) { super.update(a); }

    // Draw
    public void draw(Graphics2D g) {

        g.setColor(super.getColor());

        g.setComposite(AlphaComposite.SrcOver.derive(super.getAlpha()));

        g.fillRect(super.getX(), super.getY(), super.getW(), super.getH());
    }

    // Get Alpha
    public float getAlpha() { return super.getAlpha(); }

} // End of Class.