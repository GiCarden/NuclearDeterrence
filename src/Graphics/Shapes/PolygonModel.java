package Graphics.Shapes;

import java.awt.*;
import Math.Calculate;
import Math.Lookup;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class PolygonModel {

    private int x;
    private int y;
    private int angle;
    private double cosA;
    private double sinA;

    // PolygonModel
    public PolygonModel(int x, int y, int angle) {

        this.x = x;
        this.y = y;
        this.angle = angle;
        cosA = Lookup.cos[angle];
        sinA = Lookup.sin[angle];
    }

    // Draw
    public void draw(Graphics g, int[][] xStruct, int[][] yStruct, Color color) {

        g.setColor(color);

        int[] xPoints = new int[6];
        int[] yPoints = new int[6];

        // Draw Each Polygon
        for(int poly = 0; poly < xStruct.length; poly++) {

            for(int vertex = 0; vertex < xStruct[poly].length; vertex++) {

                xPoints[vertex] = (int)(xStruct[poly][vertex] * cosA - yStruct[poly][vertex] * sinA) + x;
                yPoints[vertex] = (int)(xStruct[poly][vertex] * sinA + yStruct[poly][vertex] * cosA) + y;
            }

            g.drawPolygon(xPoints, yPoints, xStruct[poly].length);
        }
    }


    public void moveForward(int distance) { moveBy((int)(distance * cosA), (int)(distance * sinA)); }

    // Move By
    public void moveBy(int dx, int dy) { x += dx; y += dy; }

    // rotate
    public void rotate(int degree) {

        angle = Calculate.angle(angle, degree);
        cosA  = Lookup.cos[angle];
        sinA  = Lookup.sin[angle];
    }

    // Get X
    public int getX() { return x; }

    // Get Y
    public int getY() { return y; }

    // Set X
    public void setX(int x) { this.x = x; }

    // Set Y
    public void setY(int y) { this.y = y; }

    // Get CosA
    public double getCosA() { return this.cosA; }

    // Get SinA
    public double getSinA() { return this.sinA; }

} // End of Class.
