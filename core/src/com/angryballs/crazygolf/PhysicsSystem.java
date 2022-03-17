package com.angryballs.crazygolf;

import com.badlogic.gdx.math.Vector2;

public class PhysicsSystem {

    // Current State
    public float x; // x coordinate
    public float y; // y coordinate
    public float vx; // speed in x
    public float vy; // speed in y

    // Physics properties
    private static final float h = 0.001f; // a single time step of length h
    private static final float g = 9.81f;
    private static final float dh = 0.000000001f; // derivative step

    private float uk;
    private float us;

    private float usk;
    private float uss;

    private float xt;
    private float yt;
    private float r;

    private Vector2 sandBoundsX;
    private Vector2 sandBoundsY;

    private LevelInfo levelInfo;

    public PhysicsSystem(LevelInfo info) {
        levelInfo = info;

        applyPhysicsProperties();
        reset();
    }

    private void applyPhysicsProperties() {
        uk = LevelInfo.exampleInput.grassKineticFrictionCoeff;
        us = LevelInfo.exampleInput.grassStaticFrictionCoeff;

        usk = LevelInfo.exampleInput.sandKineticFrictionCoeff;
        uss = LevelInfo.exampleInput.sandStaticFrictionCoeff;

        xt = LevelInfo.exampleInput.endPosition.x;
        yt = LevelInfo.exampleInput.endPosition.y;
        r = LevelInfo.exampleInput.holeRadius;

        sandBoundsX = LevelInfo.exampleInput.sandPitBounds[0];
        sandBoundsY = LevelInfo.exampleInput.sandPitBounds[1];
    }

    private void reset() {
        vx = vy = 0;
        x = levelInfo.startPosition.x + 128;
        y = levelInfo.startPosition.y + 128;
    }

    private boolean ballMoving = false;

    public void performMove(Vector2 velocity) {
        if (velocity.epsilonEquals(Vector2.Zero)) {
            System.out.println("Didn't actually hit the ball.");
            return;
        }
        vx = velocity.x;
        vy = velocity.y;
        ballMoving = true;
    }

    /**
     * Method to update the state vector
     *
     * @return the reason why the ball should stop or not
     *         0: no stop
     *         1: the ball has no speed and acceleration
     *         2: the ball is in the water (or tree)
     *         3: the ball is in the hole
     */
    public int iteration() {
        if (!ballMoving) {
            System.out.println("Iteration is called on stationary ball, returning early.");
            return 1;
        }
        Vector2 dh = derivative(x, y);
        Vector2 a;
        boolean sandFlag = false;

        if ((x >= sandBoundsX.x && x <= sandBoundsY.x)
                && (y >= sandBoundsX.y && y <= sandBoundsY.y)) {
            a = acceleration(dh, usk);
            sandFlag = true;
        } else {
            a = acceleration(dh, uk);
        }

        this.x += h * vx;
        this.y += h * vy;
        this.vx += h * a.x;
        this.vy += h * a.y;

        // TODO: add the range of trees
        if (getHeight(this.x, this.y) < 0)
            return 2;

        // (x-targetX)^2 + (y-targetY)^2 <= radius^2
        if (isInCircle(this.x, this.xt, this.y, this.yt, this.r))
            return 3;

        // Check if the ball is in the final position (will not move)
        if (Math.abs(vx) <= 0.1 && Math.abs(vy) <= 0.1) { // 0.09
            if (dh.x == 0 && dh.y == 0) {
                ballMoving = false;
                return 1;
            } else if (!sandFlag && us > Math.sqrt(Math.pow(dh.x, 2) + Math.pow(dh.y, 2))) {
                ballMoving = false;
                return 1;
            } else if (sandFlag && uss > Math.sqrt(Math.pow(dh.x, 2) + Math.pow(dh.y, 2))) {
                ballMoving = false;
                return 1;
            } else
                return 0;
        } else
            return 0;
    }

    /**
     * Method to check if the ball inside the hole's radius
     *
     * @param x       current X coordinate
     * @param centerX the target X coordinate
     * @param y       current Y coordinate
     * @param centerY the target Y coordinate
     * @param r       the radius around the hole
     * @return Is the ball inside the target's radius?
     */
    public boolean isInCircle(double x, double centerX, double y, double centerY, double r) {
        return Math.pow((x - centerX), 2) + Math.pow((y - centerY), 2) <= r * r;
    }

    public float getHeight(double x, double y) {
        return levelInfo.heightProfile(new Vector2((float) x, (float) y));
    }

    /**
     * Method to calculate the partial derivative of Height function with respect to
     * X or Y
     *
     * @param v1 current X coordinate
     * @param v2 current Y coordinate
     * @return dh/dx AND dh/dy
     */
    public Vector2 derivative(float v1, float v2) {
        return new Vector2((getHeight(v1 + dh, v2) - getHeight(v1, v2)) / dh,
                (getHeight(v1, v2 + dh) - getHeight(v1, v2)) / dh);
    }

    /**
     * Method to calculate the acceleration for a specific state vector in X or Y
     * axis
     *
     * @param u friction coefficient
     * @return acceleration w.r.t. X AND Y
     */
    public Vector2 acceleration(Vector2 dh, double u) {
        return new Vector2((float) (-g * dh.x - u * g * (vx) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2))),
                (float) (-g * dh.y - u * g * (vx) / Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2))));
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
        PhysicsSystem sv = new PhysicsSystem(LevelInfo.exampleInput);
        sv.performMove(new Vector2(1, 0));
        int code = sv.iteration();
        while (code == 0) {
            System.out.println("X: " + sv.getX() + ", Y: " + sv.getY() + ", Vx: " + sv.getVx() + ", Vy: " + sv.getVy());
            code = sv.iteration();
        }
    }
}
