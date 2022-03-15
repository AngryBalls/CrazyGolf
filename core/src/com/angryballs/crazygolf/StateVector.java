package com.angryballs.crazygolf;

import com.badlogic.gdx.math.Vector2;

public class StateVector {
    private double x; // x coordinate
    private double y; // y coordinate    private double previousX;
    private double vx; // speed in x
    private double vy; // speed in y
    private static final double h = 0.1;  // a single time step of length h
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

    /** Method to update the state vector
     * @return the reason why the ball should stop or not
     * 0: no stop
     * 1: the ball has no speed and acceleration
     * 2: the ball is in the water (or tree)
     * 3: the ball is in the hole
     */
    public int iteration() {
        double dh_dx = derivative(x, y, true);
        double dh_dy = derivative(x, y, false);
        double aX = 0;
        double aY = 0;
        boolean sandFlag = false;

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

    /**Method to check if the ball inside the hole's radius
     *
     * @param x current X coordinate
     * @param centerX the target X coordinate
     * @param y current Y coordinate
     * @param centerY the target Y coordinate
     * @param r the radius around the hole
     * @return Is the ball inside the target's radius?
     */
    public boolean isInCircle(double x, double centerX, double y, double centerY, double r) {
        return Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2) <= r * r;
    }

    public double getHeight(double x, double y) {
        return LevelInfo.exampleInput.heightProfile(new Vector2((float) x, (float) y));
    }

    /**Method to calculate the partial derivative of Height function with respect to X or Y
     *
     * @param v1 current X coordinate
     * @param v2 current Y coordinate
     * @param isX defines if the derivation is w.r.t. X or Y
     * @return dh/dx or dh/dy
     */
    public double derivative(double v1, double v2, boolean isX) {
        if (isX)
            return (getHeight(v1 + dh, v2) - getHeight(v1, v2)) / dh;
        return (getHeight(v1, v2 + dh) - getHeight(v1, v2)) / dh;
    }

    /**Method to calculate the acceleration for a specific state vector in X or Y axis
     *
     * @param derResult the derivative of Height w.r.t. X or Y
     * @param isX defines if we compute acceleration for x or Y
     * @param u friction coefficient
     * @return acceleration w.r.t. X or Y
     */
    public double acceleration(double derResult, boolean isX, double u) {
        if (isX)
            return -g * derResult - u * g * (vx) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
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
}
