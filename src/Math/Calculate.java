package Math;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 */
public class Calculate {

    // New Angle
    public static int angle(int angle, int theta) {

        // Change an Angle and make sure it is between 0 and 360
        int newAngle = angle + theta;

        if(newAngle >= 360) newAngle -= 360;
        if(newAngle < 0)    newAngle += 360;

        return newAngle;
    }

    // Distance To: Float In Values Returns Double
    public static double distanceTo(float x1, float y1, float x2, float y2) {

        // Check Distance and return
        double xDif = x1 - x2;
        double yDif = y1 - y2;

        return (sqr(xDif)) + (sqr(yDif));
    }

    // Distance To Circle: Int In Values Returns int
    public static double distanceTo(int r1, int r2) {

        // Check Distance and return
        int rDif = r1 + r2;

        return (sqr(rDif));
    }

    // Distance to Turn Towards: Float In Values Returns Double
    public static double turnDistance(float x1, float y1, float x2, float y2, double cosA, double sinA) {

        return (x2 - x1) * sinA - (y2 - y1) * cosA;
    }

    // Magnitude of Line
    public static double magV(double x0, double y0, double x1, double y1) {

        return Math.sqrt(sqr(x0-x1) + sqr(y0-y1));
    }

    // Square Double
    private static double sqr(double x){ return x * x; }

    // Square Int
    private static int sqr(int x){ return x * x; }

} // End of Class.
