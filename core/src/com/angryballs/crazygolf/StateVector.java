package com.angryballs.crazygolf;

public class StateVector {
    private double x; // x coordinate
    private double y; // y coordinate
    private double vx; // speed in x
    private double vy; // speed in y
    private static final double h = 1; // a single time step of length h
    private static final double g = 9.81;
    private static final double uk = 0.1;


    public StateVector(double x0, double y0, double vx0, double vy0) {
        this.x = x0;
        this.y = y0;
        this.vx = vx0;
        this.vy = vy0;
    }

    public void iteration() {
        x += h * vx;
        y += h * vy;


    }

    public double getHeight() {
        return 0.5 * (Math.sin((x - y) / 7) + 0.9);
    }

    public double derivative() {
        double current = getHeight();
        return 0;
    }


}
