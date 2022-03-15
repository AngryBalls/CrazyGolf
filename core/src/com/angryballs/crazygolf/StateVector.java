package com.angryballs.crazygolf;

import com.badlogic.gdx.math.Vector2;

public class StateVector {
    private double x; // x coordinate
    private double y; // y coordinate    private double previousX;
    private double vx; // speed in x
    private double vy; // speed in y
    private static final double h = 0.1; // a single time step of length h
    private static final double g = 9.81;
    private static final double uk = LevelInfo.exampleInput.grassKineticFrictionCoeff;
    private static final double us = LevelInfo.exampleInput.grassStaticFrictionCoeff;
    private static final double dh = 0.000000001;   //derivative step

    private static final double usk = LevelInfo.exampleInput.sandKineticFrictionCoeff;
    private static final double uss = LevelInfo.exampleInput.sandStaticFrictionCoeff;

    private static final double xt = LevelInfo.exampleInput.endPosition.x;
    private static final double yt = LevelInfo.exampleInput.endPosition.y;
    private static final double r = LevelInfo.exampleInput.holeRadius;

    private static final Vector2 sandBoundsX = LevelInfo.exampleInput.sandPitBounds[0];
    private static final Vector2 sandBoundsY = LevelInfo.exampleInput.sandPitBounds[1];

    public StateVector(double vx0, double vy0) {
        this.vx = vx0;
        this.vy = vy0;

        this.x = LevelInfo.exampleInput.startPosition.x;
        this.y = LevelInfo.exampleInput.startPosition.y;
    }

    /**
     * @return the reason why the ball should stop or not
     * 0: don't stop    1: the ball is rest     2: the ball is in the water (or tree)
     * 3: the ball is in the hole
     */
    public int iteration() {
        double dh_dx = derivative(x, y, true);
        double dh_dy = derivative(x, y, false);
        double aX = 0;
        double aY = 0;
        boolean sandFlag = false;
        // TODO: check the position. If it is in the sand pits, change u to calculate acceleration
        if ( (x <= sandBoundsX.y && x >= sandBoundsX.x) && (y <= sandBoundsY.y && y >= sandBoundsY.x) ) {
            aX = acceleration(dh_dx, true, this.usk);
            aY = acceleration(dh_dy, false, this.usk);
            sandFlag = true;
        }else{
             aX = acceleration(dh_dx, true, this.uk);
             aY = acceleration(dh_dy, false, this.uk);
        }
        this.x += h * vx;
        this.y += h * vy;
        this.vx += h * aX;
        this.vy += h * aY;

        // TODO: add the range of trees
        if (getHeight(this.x, this.y) < 0)
            return 2;

        // (x-targetX)^2 + (y-targetY)^2 <= radius^2
        if (isInCircle(this.x, this.xt, this.y, this.yt, this.r))
            return 3;

        //Check if the ball is in the final position (will not move)
        if (vx <= 0.1 && vy <= 0.1) { //0.09
            if (dh_dx == 0 && dh_dy == 0)
                return 1;
            else if (!sandFlag && us > Math.sqrt(Math.pow(dh_dx, 2) + Math.pow(dh_dy, 2)))
                return 1;
            else if(sandFlag && uss > Math.sqrt(Math.pow(dh_dx, 2) + Math.pow(dh_dy, 2)))
                return 1;
            else
                return 0;
        } else
            return 0;
    }

    public boolean isInCircle(double x, double centerX, double y, double centerY, double r) {
        return Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2) <= r * r;
    }

    public double getHeight(double x, double y) {
        return LevelInfo.exampleInput.heightProfile(new Vector2((float) x, (float) y));
    }

    public double derivative(double v1, double v2, boolean isX) {
        if (isX)
            return (getHeight(v1 + dh, v2) - getHeight(v1, v2)) / dh;
        else
            return (getHeight(v1, v2 + dh) - getHeight(v1, v2)) / dh;
    }

    public double acceleration(double derResult, boolean isX, double u) {
        if (isX)
            return -g * derResult - u * g * (vx) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
        else
            return -g * derResult - u * g * (vy) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public static void main(String[] args) {
        StateVector sv = new StateVector(1, 0);
        int code = sv.iteration();
        while (code == 0) {
            System.out.println("X: " + sv.getX() + ", Y: " + sv.getY() + ", Vx: " + sv.getVx() + ", Vy: " + sv.getVy());
            code = sv.iteration();
        }

    }
}
