package Math;

/**
 *  Code created by: Brett Bearden & Giovanni Cardenas
 *
 *  Copyright (c) 2016, Nuclear Deterrence
 *
 *  Calculate Sin(x) and Cos(x) for every angle 0 to 360. Storing the list allows the calculations to be
 *  computed once instead of time their needed.
 */
public class Lookup {

    public static double[] cos = getCos();

    public static double[] sin = getSin();

    // Get Cos
    public static double[] getCos() {

        double[] cos = new double[360];

        for(int i = 0; i < 360; i++) { cos[i] = Math.cos(i * Math.PI / 180); }

        return cos;
    }

    // Get Sin
    public static double[] getSin() {

        double[] sin = new double[360];

        for(int i = 0; i < 360; i++) { sin[i] = Math.sin(i * Math.PI / 180); }

        return sin;
    }

} // End of Class.