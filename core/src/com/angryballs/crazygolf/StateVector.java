package com.angryballs.crazygolf;

public class StateVector {
    private double x; // x coordinate
    private double y; // y coordinate    private double previousX;
    private double previousY;
    private double vx; // speed in x
    private double vy; // speed in y
    private static final double h = 1; // a single time step of length h
    private static final double g = 9.81;
    private static final double uk = 0.1;
    private static final double us = 0.2;
    private static final double dh = 0.000000001;   //derivative step

    public StateVector(double x0, double y0, double vx0, double vy0) {
        this.x = x0;
        this.y = y0;
        this.vx = vx0;
        this.vy = vy0;
    }

    public boolean iteration() {
        double dh_dx = derivative(x, y, true);
        double dh_dy = derivative(x, y, false);
        double aX = acceleration(dh_dx, true);
        double aY = acceleration(dh_dy, false);
        x += h * vx;
        y += h * vy;
        vx += h * aX;
        vy += h * aY;

        if (vx == 0 && vy == 0) {
            if (dh_dx == 0 && dh_dy == 0)
                return false;
            else if (us > Math.sqrt(Math.pow(dh_dx, 2) + Math.pow(dh_dy, 2)))
                return false;
            else
                return true;
        }else
            return true;
        //Check if the ball is in the final position (will not move)
    }

    public double getHeight(double x, double y) {
        return 0.5 * (Math.sin((x - y) / 7) + 0.9);
    }

    public double derivative(double v1, double v2, boolean isX) {
        if (isX)
            return (getHeight(v1 + dh, v2) - getHeight(v1, v2)) / dh;
        else
            return (getHeight(v1, v2 + dh) - getHeight(v1, v2)) / dh;
    }

    public double acceleration(double derResult, boolean isX) {
        if (isX)
            return -g * derResult - uk * g * (vx) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
        else
            return -g * derResult - uk * g * (vy) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
    }

}
